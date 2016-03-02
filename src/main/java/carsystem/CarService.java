package carsystem;
import java.util.List;

public class CarService {
    private final CarRepository repository;

    public CarService(CarRepository carRepository) {
        this.repository = carRepository;
        repository.addCar(new Car("bwm", "a134123"));
    }

    public List<Car> getCars() {
        return repository.getCars();
    }
}
