import java.io.IOException;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

class Server {
	public static void main(String args[]) throws IOException {
		System.out.println("Hei verden!");

		HttpServer server = HttpServer.create(new InetSocketAddress(3000), 0);
		server.createContext("/angela.txt", new RequestHandler());
		server.setExecutor(null);
		server.start();
	}
}
