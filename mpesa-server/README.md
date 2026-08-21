# Morning Star Daraja Sandbox server

This is the local Node/Express backend for the Android app's M-Pesa STK Push flow.

## 1. Install

```powershell
cd mpesa-server
npm install
```

## 2. Configure real Daraja sandbox credentials

Copy `.env.example` to `.env` and put the Daraja Sandbox credentials in `.env`.

Never put the Consumer Secret or Passkey in the Android project or GitHub.

## 3. Expose the callback for real Daraja testing

Safaricom must be able to reach the callback over HTTPS. Start the server, then expose port 3000 with ngrok:

```powershell
ngrok http 3000
```

Set `DARAJA_CALLBACK_URL` to:

```text
https://YOUR-NGROK-DOMAIN.ngrok-free.app/api/mpesa/callback
```

Restart the Node server after changing `.env`.

## 4. Start the server

```powershell
npm start
```

Check:

```text
http://localhost:3000/api/health
```

## 5. Connect the Android app

The Android app reads `mpesa.server.url` and `mpesa.demo.mode` from the ignored project `local.properties`.

For a real Daraja sandbox test through the Node server:

```properties
mpesa.server.url=http://10.0.2.2:3000/api/
mpesa.demo.mode=false
```

For a physical phone, replace `10.0.2.2` with the Windows PC's LAN IP.

## 6. Presentation-safe mode

For the school presentation, you can avoid any real M-Pesa transaction entirely:

```properties
mpesa.demo.mode=true
```

In this mode the app clearly labels the screen **PRESENTATION SANDBOX**, does not send an STK Push and does not request an M-Pesa PIN. It creates a clearly marked `sandbox_demo` payment result in Firebase and continues through the same membership/order confirmation path.

This is intentionally separate from the real Daraja integration so a presentation success can never be mistaken for a live payment.

## Payment verification

With `mpesa.demo.mode=false`, the Android app does not treat the initial STK response as payment success. It polls the server until the Safaricom callback marks the reference `paid` or `failed`. Only `paid` continues membership registration, renewal, or order completion.
