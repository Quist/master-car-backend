package carsystem.httphandlers;

import carsystem.Car;
import carsystem.CarService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;

public class CarHandler implements HttpHandler {

    private final CarService carService;

    public CarHandler(CarService carService) {
        this.carService = carService;
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Received HTTP " + httpExchange.getRequestMethod() + " on URI " + httpExchange.getRequestURI());
        String method = httpExchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGetCar(httpExchange);
                break;
            case "POST":
                handlePost(httpExchange);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void handlePost(HttpExchange httpExchange) throws IOException {
        System.out.println("Handling post");
        String body = readRequestBody(httpExchange);

        Car car = null;
        try{
            car = new Gson().fromJson(body, Car.class);
        } catch(Exception e) {
            e.printStackTrace();
        }
        carService.addCar(car);

        httpExchange.sendResponseHeaders(201, 0);
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.close();
    }

    private void handleGetCar(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        String paths[] =  uri.getPath().split("/");

        String payload;
        if (uriHasId(paths)) {
            Car car = carService.getCar(paths[2]);
            if (car == null) {
                sendNotFound(exchange);
                return;
            }
            payload = new Gson().toJson(car);
        } else {
            List<Car> cars = carService.getCars();
            payload = new Gson().toJson(cars);
        }

        exchange.sendResponseHeaders(200, payload.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(payload.getBytes());
        outputStream.close();
    }

    private void sendNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
    }

    private boolean uriHasId(String[] paths) {
        if (paths.length > 2) {
            return true;
        } else {
            return false;
        }
    }

    private String readRequestBody(HttpExchange httpExchange) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(httpExchange.getRequestBody()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
