package carsystem;

import java.util.ArrayList;
import java.util.List;

public class CarRepository {
    private ArrayList<Car> cars;

    public CarRepository() {
        cars = new ArrayList<Car>();
    }

    public List<Car> getCars() {
        return cars;
    }

    public void addCar(Car car) {
        System.out.println("Adding car to repository");
        cars.add(car);
    }
}
