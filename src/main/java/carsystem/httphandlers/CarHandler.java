package carsystem.httphandlers;

import carsystem.Car;
import carsystem.CarRepository;
import carsystem.CarService;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CarHandler extends HttpServlet {
    final static Logger logger = LoggerFactory.getLogger(CarHandler.class);

    private final CarService carService = new CarService(new CarRepository());

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
                //handleDelete(httpExchange);
                break;
            case "PUT" :
                handlePut(httpExchange);
                break;
            default:
                logger.error("Unsupported method");
                throw new UnsupportedOperationException();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String authKey = req.getHeader("Dil-auth");
        if (authKey == null || ! authKey.equals("1337")) {
            logger.warn("Client does not provide header dil-auth");
            resp.sendError(401);
            return;
        }
        resp.setHeader("dil-test", "xxx");
        
        String carId = getCarId(req.getRequestURI());
        String payload;
        if (carId != null) {
            Car car = carService.getCar(carId);
            if (car == null) {
                resp.sendError(404);
                return;
            }
            payload = new Gson().toJson(car);
        } else {
            List<Car> cars = carService.getCars();
            payload = new Gson().toJson(cars);
        }
        
        resp.setStatus(200);
        resp.setHeader("Content-Type", "application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(payload);
        writer.close();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String authKey = req.getHeader("Dil-auth");
        if (authKey == null || ! authKey.equals("1337")) {
            logger.warn("Client does not provide header dil-auth");
            resp.sendError(401);
            return;
        }
        resp.setHeader("dil-test", "xxx");
        
        String body = getBody(req);

        Car car = null;
        try{
            car = new Gson().fromJson(body, Car.class);
        } catch(Exception e) {
            e.printStackTrace();
        }
        Car carResult = carService.addCar(car);
        String payload = new Gson().toJson(carResult);

        resp.setStatus(201);
        resp.setHeader("Content-Type", "application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(payload);
        writer.close();
    }
    
    public static String getBody(HttpServletRequest request) throws IOException {

    String body = null;
    StringBuilder stringBuilder = new StringBuilder();
    BufferedReader bufferedReader = null;

    try {
        InputStream inputStream = request.getInputStream();
        if (inputStream != null) {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            char[] charBuffer = new char[128];
            int bytesRead = -1;
            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                stringBuilder.append(charBuffer, 0, bytesRead);
            }
        } else {
            stringBuilder.append("");
        }
    } catch (IOException ex) {
        throw ex;
    } finally {
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException ex) {
                throw ex;
            }
        }
    }

    body = stringBuilder.toString();
    return body;
}
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String authKey = req.getHeader("Dil-auth");
        if (authKey == null || ! authKey.equals("1337")) {
            logger.warn("Client does not provide header dil-auth");
            resp.sendError(401);
            return;
        }
        resp.setHeader("dil-test", "xxx");
        
        String body = getBody(req);
        Car car = null;
        try{
            car = new Gson().fromJson(body, Car.class);
        } catch(Exception e) {
            e.printStackTrace();
        }

        try {
            carService.updateCar(car);
            resp.setStatus(204);
        } catch (NoSuchElementException e) {
            resp.sendError(404);
        }
    }
    
        @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String authKey = req.getHeader("Dil-auth");
        if (authKey == null || ! authKey.equals("1337")) {
            logger.warn("Client does not provide header dil-auth");
            resp.sendError(401);
            return;
        }
        resp.setHeader("dil-test", "xxx");
        
        String carId = getCarId(req.getRequestURI());
        if (carId == null) {
            carService.removeCars();
            resp.setStatus(204);
        } else {
            handleDeleteSpecific(resp, carId);
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

    private void handleDeleteSpecific(HttpServletResponse resp, String carId) throws IOException {
        Car car = carService.getCar(carId);
        if(car == null) {
            resp.sendError(404);
        } else {
            carService.removeCar(car);
            resp.setStatus(204);
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

        String carId = getCarId(exchange.getRequestURI().getPath());
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

    private String getCarId(String uri) {
        String paths[] =  uri.split("/");
        
        if (paths[paths.length - 1].equals("cars")) {
            return null;
        } else {
            return paths[paths.length-1];
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
