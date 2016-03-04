package carsystem;
import java.util.List;
import java.util.NoSuchElementException;

public class CarService {
    private final CarRepository repository;

    public CarService(CarRepository carRepository) {
        this.repository = carRepository;
        repository.addCar(new Car("BMW", "a134123"));
    }

    public List<Car> getCars() {
        return repository.getCars();
    }

    public void addCar(Car car) {
        repository.addCar(car);
    }

    public Car getCar(String id) {
        for (Car car: repository.getCars()) {
            if (car.getRegistration().equals(id)) {
                System.out.println("Found car with registration: " + id);
                return car;
            }
        }
        System.out.println("Did not find car with registration: " + id);
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
