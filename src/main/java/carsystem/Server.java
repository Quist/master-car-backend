package carsystem;

import java.io.IOException;

import carsystem.httphandlers.RequestHandler;
import com.sun.net.httpserver.HttpServer;
import carsystem.httphandlers.CarHandler;

import java.net.InetSocketAddress;

class Server {
	public static void main(String args[]) {
		System.out.println("Hei verden!");
        if (args.length < 1) {
            System.out.println("Usage: server port");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);

        CarService carService = new CarService(new CarRepository());

        HttpServer server = null; 
        try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch(IOException e){
        	e.printStackTrace();
        }
		server.createContext("/angela.txt", new RequestHandler());
        server.createContext("/cars", new CarHandler(carService));
		server.setExecutor(null);

		System.out.println("carsystem.Server listening on port " + port);
		server.start();
	}
}
