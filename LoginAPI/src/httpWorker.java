import java.io.IOException;
import java.io.OutputStream;
import com.sun.net.httpserver.HttpExchange;

import com.fasterxml.jackson.databind.ObjectMapper;

public class httpWorker {

    public httpWorker() {
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public void Response(HttpExchange exchange, int statusCode, Object payload) throws IOException {
        byte[] response = MAPPER.writeValueAsBytes(payload);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(response);
        }
        exchange.close();
    }

    public LoginViewModel deserialize(HttpExchange exchange) throws IOException {
        try {
            return MAPPER.readValue(exchange.getRequestBody(), LoginViewModel.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
