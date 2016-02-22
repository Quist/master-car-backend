import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

class RequestHandler implements HttpHandler{
	
	public void handle(HttpExchange exchange) throws IOException {

		System.out.println("Received request: " + exchange.getRequestURI());
		System.out.println("Remote address: " + exchange.getRemoteAddress());
		System.out.println("Method: " + exchange.getRequestMethod());

		Set<String> headers = exchange.getRequestHeaders().keySet();
		System.out.println("Headers:");

		Iterator<String> iterator = headers.iterator();
		while(iterator.hasNext()) {
            String key = iterator.next();
			System.out.println(key + ": " + exchange.getRequestHeaders().get(key));
		}

		String response = "Hei du ;))";
		exchange.sendResponseHeaders(200, response.length());
		OutputStream outputStream = exchange.getResponseBody();
		outputStream.write(response.getBytes());
		outputStream.close();
	}	
}