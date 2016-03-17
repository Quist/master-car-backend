package carsystem.httphandlers;

import carsystem.Car;
import carsystem.CarService;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

public class CarHandler implements HttpHandler {
    final static Logger logger = LoggerFactory.getLogger(CarHandler.class);

    private final CarService carService;

    public CarHandler(CarService carService) {
        this.carService = carService;
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        logger.debug("Received HTTP " + httpExchange.getRequestMethod() + " on URI " + httpExchange.getRequestURI());

        Headers headers = httpExchange.getRequestHeaders();
        String authKey = headers.getFirst("Dil-auth");
        if (authKey == null || ! authKey.equals("1337")) {
            logger.warn("Client does not provide header dil-auth");
            sendNotAuthorized(httpExchange);
            return;
        }

        httpExchange.getResponseHeaders().set("dil-test", "xxx");


        String method = httpExchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGetCar(httpExchange);
                break;
            case "POST":
                handlePost(httpExchange);
                break;
            case "DELETE" :
                handleDelete(httpExchange);
                break;
            case "PUT" :
                handlePut(httpExchange);
                break;
            default:
                logger.error("Unsupported method");
                throw new UnsupportedOperationException();
        }
    }

    private void handlePut(HttpExchange httpExchange) throws IOException {
        String body = readRequestBody(httpExchange);
        Car car = null;
        try{
            car = new Gson().fromJson(body, Car.class);
        } catch(Exception e) {
            e.printStackTrace();
        }

        try {
            carService.updateCar(car);
            httpExchange.sendResponseHeaders(204, -1);
            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.close();
        } catch (NoSuchElementException e) {
            sendNotFound(httpExchange);
        }

    }

    private void handleDelete(HttpExchange httpExchange) throws IOException {
        String carId = getCarId(httpExchange.getRequestURI());
        if (carId == null) {
            handleDeleteAll(httpExchange);
        } else {
            handleDeleteSpecific(httpExchange, carId);
        }
    }

    private void handleDeleteSpecific(HttpExchange httpExchange, String carId) throws IOException {
        Car car = carService.getCar(carId);
        if(car == null) {
            sendNotFound(httpExchange);
        } else {
            carService.removeCar(car);
            httpExchange.sendResponseHeaders(204, -1);
            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.close();
        }
    }

    private void handleDeleteAll(HttpExchange httpExchange) throws IOException {
        carService.removeCars();
        httpExchange.sendResponseHeaders(204, -1);
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.close();
    }

    private void handlePost(HttpExchange httpExchange) throws IOException {
        String body = readRequestBody(httpExchange);

        Car car = null;
        try{
            car = new Gson().fromJson(body, Car.class);
        } catch(Exception e) {
            e.printStackTrace();
        }
        Car carResult = carService.addCar(car);
        String payload = new Gson().toJson(carResult);

        httpExchange.sendResponseHeaders(201, payload.length());
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(payload.getBytes());
        outputStream.close();
    }

    private void handleGetCar(HttpExchange exchange) throws IOException {

        String carId = getCarId(exchange.getRequestURI());
        String payload;
        if (carId != null) {
            Car car = carService.getCar(carId);
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
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write("".getBytes());
        outputStream.close();
    }

    private void sendNotAuthorized(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(401, 0);
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write("".getBytes());
        outputStream.close();
    }

    private String getCarId(URI uri) {
        String paths[] =  uri.getPath().split("/");
        if (paths.length <= 2) {
            return null;
        }

        return paths[2];
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
