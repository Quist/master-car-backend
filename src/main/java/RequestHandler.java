import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;
import java.io.IOException;

class RequestHandler implements HttpHandler{
	
	public void handle(HttpExchange exchange) throws IOException {

		System.out.println("Received request: " + exchange.getRequestURI());
		System.out.println("Remote address: " + exchange.getRemoteAddress());

		String response = "Hei du ;))";
		exchange.sendResponseHeaders(200, response.length());
		OutputStream outputStream = exchange.getResponseBody();
		outputStream.write(response.getBytes());
		outputStream.close();
	}	
}