import 'dotenv/config';
import express from 'express';
import qrcode from 'qrcode-terminal';
import pino from 'pino';
import makeWASocket, {
  DisconnectReason,
  fetchLatestBaileysVersion,
  useMultiFileAuthState
} from 'baileys';

const PORT = Number(process.env.PORT || 3001);
const INTERNAL_TOKEN = process.env.INTERNAL_TOKEN || 'troque-este-token';
const AUTH_DIR = 'auth';

const app = express();
app.use(express.json());

let sock;
let isConnected = false;
let isStarting = false;

async function startWhatsApp() {
  if (isStarting) {
    return;
  }

  isStarting = true;
  const { state, saveCreds } = await useMultiFileAuthState(AUTH_DIR);
  const { version } = await fetchLatestBaileysVersion();

  sock = makeWASocket({
    version,
    auth: state,
    logger: pino({ level: 'silent' })
  });

  sock.ev.on('creds.update', saveCreds);
  sock.ev.on('connection.update', ({ connection, lastDisconnect, qr }) => {
    if (qr) {
      console.log('Escaneie o QR Code abaixo com o WhatsApp:');
      qrcode.generate(qr, { small: true });
    }

    if (connection === 'open') {
      isConnected = true;
      isStarting = false;
      console.log('WhatsApp conectado.');
    }

    if (connection === 'close') {
      isConnected = false;
      isStarting = false;

      const statusCode = lastDisconnect?.error?.output?.statusCode;
      if (statusCode !== DisconnectReason.loggedOut) {
        console.log('Conexao encerrada. Tentando reconectar...');
        startWhatsApp().catch((error) => console.error('Falha ao reconectar:', error));
      } else {
        console.log('Sessao encerrada. Apague a pasta auth e escaneie um novo QR Code.');
      }
    }
  });
}

function requireInternalToken(req, res, next) {
  if (req.header('X-Internal-Token') !== INTERNAL_TOKEN) {
    return res.status(401).json({ error: 'Token interno invalido.' });
  }

  next();
}

function normalizeJid(phoneNumber) {
  const digits = String(phoneNumber || '').replace(/\D/g, '');
  if (!digits) {
    return '';
  }

  return `${digits}@s.whatsapp.net`;
}

app.get('/health', (req, res) => {
  res.json({ connected: isConnected });
});

app.post('/send-message', requireInternalToken, async (req, res) => {
  const jid = normalizeJid(req.body?.to);
  const message = String(req.body?.message || '').trim();

  if (!jid || !message) {
    return res.status(400).json({ error: 'Informe to e message.' });
  }

  if (!sock || !isConnected) {
    return res.status(503).json({ error: 'WhatsApp ainda nao esta conectado.' });
  }

  await sock.sendMessage(jid, { text: message });
  return res.json({ sent: true });
});

app.listen(PORT, () => {
  console.log(`WhatsApp service ouvindo na porta ${PORT}.`);
  startWhatsApp().catch((error) => {
    isStarting = false;
    console.error('Falha ao iniciar WhatsApp:', error);
  });
});
