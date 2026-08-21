require('dotenv').config();

const express = require('express');
const axios = require('axios');

const app = express();
app.use(express.json({ limit: '1mb' }));

const PORT = Number(process.env.PORT || 3000);
const BASE_URL = process.env.DARAJA_BASE_URL || 'https://sandbox.safaricom.co.ke';
const pendingPayments = new Map();

function required(name) {
  const value = process.env[name];
  if (!value) throw new Error(`${name} is not configured on the server.`);
  return value;
}

function normalizePhone(value) {
  const digits = String(value || '').replace(/\D/g, '');
  if (digits.startsWith('254') && digits.length === 12) return digits;
  if (digits.startsWith('0') && digits.length === 10) return `254${digits.slice(1)}`;
  if (digits.startsWith('7') && digits.length === 9) return `254${digits}`;
  throw new Error('Enter a valid Kenyan M-Pesa number, e.g. 0712345678.');
}

function darajaPassword(timestamp) {
  return Buffer.from(`${required('DARAJA_SHORTCODE')}${required('DARAJA_PASSKEY')}${timestamp}`).toString('base64');
}

async function accessToken() {
  const key = required('DARAJA_CONSUMER_KEY');
  const secret = required('DARAJA_CONSUMER_SECRET');
  const auth = Buffer.from(`${key}:${secret}`).toString('base64');
  const response = await axios.get(`${BASE_URL}/oauth/v1/generate?grant_type=client_credentials`, {
    headers: { Authorization: `Basic ${auth}` },
    timeout: 15000
  });
  return response.data.access_token;
}

app.get('/api/health', (_req, res) => {
  res.json({ ok: true, sandbox: BASE_URL.includes('sandbox'), time: new Date().toISOString() });
});

app.post('/api/mpesa/stkpush', async (req, res) => {
  try {
    const { phone, amount, referenceId, purpose } = req.body || {};
    const numericAmount = Math.round(Number(amount));
    if (!referenceId || !purpose || !Number.isFinite(numericAmount) || numericAmount < 1) {
      return res.status(400).json({ error: 'referenceId, purpose and a positive amount are required.' });
    }

    const normalizedPhone = normalizePhone(phone);
    const timestamp = new Date().toISOString().replace(/[-:TZ.]/g, '').slice(0, 14);
    const token = await accessToken();

    const response = await axios.post(`${BASE_URL}/mpesa/stkpush/v1/processrequest`, {
      BusinessShortCode: required('DARAJA_SHORTCODE'),
      Password: darajaPassword(timestamp),
      Timestamp: timestamp,
      TransactionType: 'CustomerPayBillOnline',
      Amount: numericAmount,
      PartyA: normalizedPhone,
      PartyB: required('DARAJA_SHORTCODE'),
      PhoneNumber: normalizedPhone,
      CallBackURL: required('DARAJA_CALLBACK_URL'),
      AccountReference: String(referenceId).slice(0, 12),
      TransactionDesc: String(purpose).slice(0, 13)
    }, {
      headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
      timeout: 20000
    });

    const data = response.data || {};
    pendingPayments.set(referenceId, {
      referenceId,
      purpose,
      amount: numericAmount,
      phone: normalizedPhone,
      status: data.ResponseCode === '0' ? 'pending' : 'failed',
      checkoutRequestId: data.CheckoutRequestID || null,
      merchantRequestId: data.MerchantRequestID || null,
      responseCode: data.ResponseCode,
      responseDescription: data.ResponseDescription,
      createdAt: Date.now()
    });

    return res.json({
      accepted: data.ResponseCode === '0',
      referenceId,
      checkoutRequestId: data.CheckoutRequestID || null,
      message: data.CustomerMessage || data.ResponseDescription || 'STK Push sent.'
    });
  } catch (error) {
    const detail = error.response?.data?.errorMessage || error.response?.data?.ResponseDescription || error.message;
    console.error('STK push failed:', detail);
    return res.status(502).json({ error: detail || 'Unable to contact M-Pesa.' });
  }
});

app.post('/api/mpesa/callback', (req, res) => {
  // Safaricom expects a quick HTTP acknowledgement. The callback is the source of truth for payment success.
  res.json({ ResultCode: 0, ResultDesc: 'Accepted' });

  try {
    const callback = req.body?.Body?.stkCallback;
    if (!callback) return;

    const checkoutRequestId = callback.CheckoutRequestID;
    let paymentEntry;
    for (const entry of pendingPayments.values()) {
      if (entry.checkoutRequestId === checkoutRequestId) {
        paymentEntry = entry;
        break;
      }
    }
    if (!paymentEntry) {
      console.warn('Received callback for unknown CheckoutRequestID:', checkoutRequestId);
      return;
    }

    const resultCode = Number(callback.ResultCode);
    paymentEntry.status = resultCode === 0 ? 'paid' : 'failed';
    paymentEntry.resultCode = resultCode;
    paymentEntry.resultDesc = callback.ResultDesc || '';
    paymentEntry.completedAt = Date.now();

    const metadata = {};
    for (const item of callback.CallbackMetadata?.Item || []) {
      if (item.Name) metadata[item.Name] = item.Value ?? null;
    }
    paymentEntry.mpesaReceiptNumber = metadata.MpesaReceiptNumber || null;
    paymentEntry.transactionDate = metadata.TransactionDate || null;
    paymentEntry.callbackPhone = metadata.PhoneNumber || null;

    pendingPayments.set(paymentEntry.referenceId, paymentEntry);
    console.log(`M-Pesa ${paymentEntry.referenceId}: ${paymentEntry.status}`, paymentEntry.mpesaReceiptNumber || '');
  } catch (error) {
    console.error('Callback processing error:', error.message);
  }
});

app.get('/api/mpesa/status/:referenceId', (req, res) => {
  const payment = pendingPayments.get(req.params.referenceId);
  if (!payment) return res.status(404).json({ status: 'not_found' });
  return res.json({
    referenceId: payment.referenceId,
    status: payment.status,
    resultCode: payment.resultCode ?? null,
    resultDesc: payment.resultDesc || payment.responseDescription || null,
    mpesaReceiptNumber: payment.mpesaReceiptNumber || null,
    checkoutRequestId: payment.checkoutRequestId || null
  });
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`Morning Star M-Pesa server listening on http://0.0.0.0:${PORT}`);
  console.log(`Sandbox: ${BASE_URL.includes('sandbox')}`);
});
