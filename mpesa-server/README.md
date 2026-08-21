# Morning Star Daraja Sandbox server

This is the local Node/Express backend for the Android app's M-Pesa STK Push flow.

## 1. Install

```powershell
cd mpesa-server
npm install
```

## 2. Configure credentials

Copy `.env.example` to `.env` and put the Daraja Sandbox credentials in `.env`.

Never put the Consumer Secret or Passkey in the Android project or GitHub.

## 3. Expose the callback

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

The Android app reads `mpesa.server.url` from the project's ignored `local.properties`.

For an Android emulator:

```properties
mpesa.server.url=http://10.0.2.2:3000/api/
```

For a physical phone, use the Windows PC's LAN IP instead, for example:

```properties
mpesa.server.url=http://192.168.1.20:3000/api/
```

The phone and PC must be on the same network and Windows Firewall must allow the Node server port.

## Payment verification

The Android app does not treat the initial STK response as payment success. It polls the server until the Safaricom callback marks the reference `paid` or `failed`. Only `paid` continues membership registration, renewal, or order completion.
