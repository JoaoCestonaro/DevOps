# WhatsApp Service

Servico Node que envia mensagens usando Baileys e expoe um endpoint interno para o projeto Spring Boot.

## Como usar

1. Instale Node.js 20 ou superior.
2. Entre nesta pasta e instale as dependencias:

```bash
npm install
```

3. Crie um arquivo `.env` baseado em `.env.example` e use o mesmo token configurado em `application.properties`.
4. Inicie o servico:

```bash
npm start
```

5. Escaneie o QR Code exibido no terminal com o WhatsApp.

## Endpoint

```http
POST /send-message
X-Internal-Token: troque-este-token
Content-Type: application/json

{
  "to": "5511999999999",
  "message": "Mensagem de teste"
}
```

A pasta `auth/` guarda a sessao local do WhatsApp e nao deve ser versionada.
