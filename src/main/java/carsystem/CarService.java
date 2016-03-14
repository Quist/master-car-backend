package carsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.NoSuchElementException;

public class CarService {
    final static Logger logger = LoggerFactory.getLogger(CarService.class);

    private final CarRepository repository;

    public CarService(CarRepository carRepository) {
        this.repository = carRepository;
    }

    public List<Car> getCars() {
        return repository.getCars();
    }

    public Car addCar(Car car) {
        return repository.addCar(car);
    }

    public Car getCar(String id) {
        for (Car car: repository.getCars()) {
            if (car.getRegistration().equals(id)) {
                logger.debug("Found car with registration: " + id);
                return car;
            }
        }
        logger.debug("Did not find car with registration: " + id);
        return null;
    }

    public void removeCar(Car car) {
        repository.removeCar(car);
    }

    public void updateCar(Car car) throws NoSuchElementException{
        Car originalCar = getCar(car.getRegistration());
        if (originalCar == null) {
            throw new NoSuchElementException();
        } else {
            repository.removeCar(originalCar);
            repository.addCar(car);
        }

    }
}
