package carsystem.httphandlers;

import carsystem.Car;
import carsystem.CarService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class CarHandler implements HttpHandler {

    private final CarService carService;

    public CarHandler(CarService carService) {
        this.carService = carService;
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Received http request.");
        String method = httpExchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGetCar(httpExchange);
                break;
            default:
                break;
        }
    }

    private void handleGetCar(HttpExchange exchange) throws IOException {
        List<Car> cars = carService.getCars();
        String payload = new Gson().toJson(cars);

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, payload.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(payload.getBytes());
        outputStream.close();
    }
}
