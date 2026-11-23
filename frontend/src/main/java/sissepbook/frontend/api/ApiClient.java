package sissepbook.frontend.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import sissepbook.frontend.session.Sesion;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Cliente HTTP (java.net.http) hacia http://localhost:8080.
 * - Agrega el header Authorization: Bearer <token> de Sesion.
 * - Métodos get/post/put/delete que (de)serializan JSON con Jackson.
 * - Si responde 401 lanza ApiException(401) para volver al Login;
 *   cualquier otro error lanza ApiException(codigo, mensaje) con el mensaje del backend.
 */
public class ApiClient {

    private static final String BASE_URL = System.getProperty("api.baseUrl", "http://localhost:8080");

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static ObjectMapper mapper() {
        return MAPPER;
    }

        public <T> T get(String path, Class<T> tipo) {
        return getConParser(path, (data) -> MAPPER.readValue(data, tipo));
    }

    public <T> T getList(String path, TypeReference<T> tipo) {
        return getConParser(path, (data) -> MAPPER.readValue(data, tipo));
    }

    public <T> T post(String path, Object body, Class<T> tipo) {
        return enviar("POST", path, body, (data) -> MAPPER.readValue(data, tipo));
    }

    public <T> T put(String path, Object body, Class<T> tipo) {
        return enviar("PUT", path, body, (data) -> MAPPER.readValue(data, tipo));
    }

    public void delete(String path) {
        enviar("DELETE", path, null, (data) -> null);
    }

    // ---------------------------------------------------------------------

    private interface Parser<T> {
        T parse(String data) throws Exception;
    }

        private <T> T getConParser(String path, Parser<T> parser) {
        return enviar(HttpRequest.newBuilder(), "GET", path, null, parser);
    }

    private <T> T enviar(String metodo, String path, Object body, Parser<T> parser) {
        return enviar(HttpRequest.newBuilder(), metodo, path, body, parser);
    }

    private <T> T enviar(HttpRequest.Builder builder, String metodo, String path, Object body, Parser<T> parser) {
        try {
            builder.uri(URI.create(BASE_URL + path))
                    .timeout(Duration.ofSeconds(20))
                    .header("Accept", "application/json");

            if (Sesion.getToken() != null) {
                builder.header("Authorization", "Bearer " + Sesion.getToken());
            }

            String cuerpo = null;
            if (body != null) {
                cuerpo = MAPPER.writeValueAsString(body);
                builder.header("Content-Type", "application/json");
            }

            HttpRequest request = builder.method(metodo,
                    cuerpo != null ? HttpRequest.BodyPublishers.ofString(cuerpo) : HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            String data = response.body();

            if (status >= 200 && status < 300) {
                if (data == null || data.isBlank()) {
                    return null;
                }
                return parser.parse(data);
            }

            throw new ApiException(status, extraerMensaje(data, status));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("No se pudo conectar con el servidor: " + e.getMessage());
        }
    }

    private String extraerMensaje(String data, int status) {
        if (data != null && !data.isBlank()) {
            try {
                JsonNode node = MAPPER.readTree(data);
                if (node.has("mensaje")) {
                    return node.get("mensaje").asText();
                }
                if (node.has("error")) {
                    return node.get("error").asText();
                }
            } catch (Exception ignored) {
                // data no era JSON; se devuelve crudo abajo
            }
            return data;
        }
        return switch (status) {
            case 401 -> "Sesión expirada. Inicia sesión nuevamente.";
            case 403 -> "No tienes permisos para esta operación.";
            case 404 -> "Recurso no encontrado.";
            default -> "Error del servidor (" + status + ").";
        };
    }
}
