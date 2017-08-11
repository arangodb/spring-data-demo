package demo.example.basic;

import demo.AbstractRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;

import java.io.PrintStream;

@ComponentScan("demo")
public class Runner extends AbstractRunner implements CommandLineRunner {

    @Autowired
    private CustomerRepository repository;

    @Override
    public void run(String... args) throws Exception {

        repository.deleteAll();

        // save a couple of customers

        repository.save(new Customer("Alice", "Smith"));
        repository.save(new Customer("Bob", "Smith"));
        Customer withLocationSet = new Customer("Bruce", "Wayne");
        withLocationSet.setLocation(new double[] {10, 5});
        repository.save(withLocationSet);

        PRINT_STREAM.println(repository.findByLocationWithin(
                new Point(10, 6), new Distance(200, Metrics.KILOMETERS)));

        // fetch all customers
        PRINT_STREAM.println("Customers found with findAll():");
        PRINT_STREAM.println("-------------------------------");
        for (Customer customer : repository.findAll()) {
            PRINT_STREAM.println(customer);
        }
        PRINT_STREAM.println();

        // fetch an individual customer
        PRINT_STREAM.println("Customer found with findByFirstName('Alice'):");
        PRINT_STREAM.println("--------------------------------");
        PRINT_STREAM.println(repository.findByFirstNameAndLastName("Alice", "Smith"));

        PRINT_STREAM.println("Customers found with findByLastName('Smith'):");
        PRINT_STREAM.println("--------------------------------");
        for (Customer customer : repository.findByLastName("Smith")) {
            PRINT_STREAM.println(customer + ", id: " + customer.getId());
        }
    }
}
