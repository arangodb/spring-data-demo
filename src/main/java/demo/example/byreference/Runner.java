package demo.example.byreference;

import com.arangodb.springframework.core.ArangoOperations;
import demo.AbstractRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;
import java.util.List;

@ComponentScan("demo")
public class Runner extends AbstractRunner {

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Override
    public void run(String... args) throws Exception {

        repository.deleteAll();
        LOGGER.log("deleteAll() - cleared 'employee' collection");
        LOGGER.log(BREAK);

        assignmentRepository.deleteAll();
        LOGGER.log("deleteAll() - cleared 'assignment' collection");
        LOGGER.log(BREAK);

        Employee bob = new Employee("Bob", null);
        Employee cindy = new Employee("Cindy", bob);
        Employee john = new Employee("John", bob);

        List<Employee> employees = Arrays.asList(bob, cindy, john);

        Assignment presentation = new Assignment("Presentation");
        Assignment demonstration = new Assignment("Demonstration");

        assignmentRepository.save(presentation);
        assignmentRepository.save(demonstration);

        bob.setAssignments(Arrays.asList(new Assignment[] { presentation }));
        cindy.setAssignments(Arrays.asList(new Assignment[] { presentation, demonstration }));
        john.setAssignments(Arrays.asList(new Assignment[] { demonstration }));

        // save manager ref before other objects
        repository.save(bob);
        repository.save(employees);

        LOGGER.log("All 3 employees:");
        LOGGER.log(BREAK);
        LOGGER.log(repository.findAll());
        LOGGER.log();

        LOGGER.log("findByManagerNameIn([\"Bob\"]) - expecting Cindy and John:");
        LOGGER.log(BREAK);
        LOGGER.log(repository.findByManagerNameIn(new String[] {"Bob"}));
        LOGGER.log();

        LOGGER.log("findByAssignmentsDescriptionRegex(\"tation$\") - expecting Bob and Cindy:");
        LOGGER.log(BREAK);
        LOGGER.log(repository.findByAssignmentsDescriptionRegex("tation$"));
        LOGGER.log();
    }
}
