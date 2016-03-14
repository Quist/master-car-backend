package carsystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CarRepository {
    final static Logger logger = LoggerFactory.getLogger(CarRepository.class);

    private ArrayList<Car> cars;

    public CarRepository() {
        cars = new ArrayList<Car>();
    }

    public List<Car> getCars() {
        return cars;
    }

    public Car addCar(Car car) {
        logger.debug("Adding car to repository");
        cars.add(car);
        return car;
    }

    public void removeCar(Car car) {
        cars.remove(car);
    }

    public void removeCars() {
        logger.info("Removing all cars");
        cars.clear();
    }
}
