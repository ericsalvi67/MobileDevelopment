import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class SingleServer {
    private static final int PORT = 8080;
    private static final httpWorker httpWorker = new httpWorker();

    private static HashMap<String, String> users = new HashMap<>() {
        {
            put("admin", "admin");
            put("eric.salvi", "teste123");
            put("univates.teste", "univates2026");
        }
    };

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/login", new LoginHandler());
            server.createContext("/health", new HealthHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("HTTP server running on http://localhost:" + PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                httpWorker.Response(exchange, 405, Map.of("error", "Use GET"));
                return;
            }

            httpWorker.Response(exchange, 200, Map.of("status", "OK"));
        }
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                httpWorker.Response(exchange, 405, Map.of("error", "Use POST"));
                return;
            }

            try {
                LoginViewModel loginData = httpWorker.deserialize(exchange);

                if (loginData.email == null || loginData.password == null) {
                    httpWorker.Response(exchange, 400, Map.of("error", "Missing email or password"));
                    return;
                }

                if (!users.containsKey(loginData.email)) {
                    httpWorker.Response(exchange, 404, Map.of("error", "User not found"));
                    return;
                }

                if (!users.get(loginData.email).equals(loginData.password)) {
                    httpWorker.Response(exchange, 401, Map.of("error", "Invalid credentials"));
                    return;
                }

                httpWorker.Response(exchange, 200, Map.of("message", "Logado com sucesso"));
            } catch (JsonProcessingException e) {
                httpWorker.Response(exchange, 400, Map.of(
                        "error", "Invalid JSON body",
                        "details", e.getOriginalMessage()));
            } catch (Exception e) {
                httpWorker.Response(exchange, 500, Map.of("error", "Internal server error"));
            }
        }
    }

}