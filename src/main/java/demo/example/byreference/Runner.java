package demo.example.byreference;

import demo.AbstractRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;
import java.util.List;

@ComponentScan("demo")
public class Runner extends AbstractRunner {

    @Autowired
    private EmployeeRepository repository;

    @Override
    public void run(String... args) throws Exception {

        repository.deleteAll();
        LOGGER.log("deleteAll() - cleared 'employee' collection");
        LOGGER.log(BREAK);

        Employee bob = new Employee("Bob", null);
        Employee cindy = new Employee("Cindy", bob);
        Employee john = new Employee("John", bob);

        List<Employee> employees = Arrays.asList(bob, cindy, john);

        repository.save(employees);

        bob.setColleagues(Arrays.asList(cindy, john));
        cindy.setColleagues(Arrays.asList(bob, john));
        john.setColleagues(Arrays.asList(bob, cindy));

        repository.save(employees);

        LOGGER.log("All 3 employees:");
        LOGGER.log(BREAK);
        LOGGER.log(employees);
        LOGGER.log(BREAK);
        LOGGER.log(repository.findAll());
        LOGGER.log();
    }
}
