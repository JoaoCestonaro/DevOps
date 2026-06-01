package br.com.fatecads.fatecads.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppService {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${whatsapp.service.url:http://localhost:3001}")
    private String whatsappServiceUrl;

    @Value("${whatsapp.service.token:troque-este-token}")
    private String whatsappServiceToken;

    public void enviarMensagem(String telefone, String mensagem) {
        try {
            String json = "{\"to\":\"" + escapeJson(telefone) + "\",\"message\":\"" + escapeJson(mensagem) + "\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(whatsappServiceUrl + "/send-message"))
                    .header("Content-Type", "application/json")
                    .header("X-Internal-Token", whatsappServiceToken)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                System.out.println("Falha ao enviar WhatsApp: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            System.out.println("Falha ao enviar WhatsApp: " + e.getMessage());
        }
    }

    private String escapeJson(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
