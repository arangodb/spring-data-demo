package demo.example.basic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;

import java.io.PrintStream;

@ComponentScan("demo")
public class Runner implements CommandLineRunner {

    @Autowired
    private CustomerRepository repository;

    @Override
    public void run(String... args) throws Exception {

        PrintStream printStream = System.err;

        repository.deleteAll();

        // save a couple of customers

        repository.save(new Customer("Alice", "Smith"));
        repository.save(new Customer("Bob", "Smith"));
        Customer withLocationSet = new Customer("Bruce", "Wayne");
        withLocationSet.setLocation(new double[] {10, 5});
        repository.save(withLocationSet);

        printStream.println(repository.findByLocationWithin(
                new Point(10, 6), new Distance(200, Metrics.KILOMETERS)));

        // fetch all customers
        printStream.println("Customers found with findAll():");
        printStream.println("-------------------------------");
        for (Customer customer : repository.findAll()) {
            printStream.println(customer);
        }
        printStream.println();

        // fetch an individual customer
        printStream.println("Customer found with findByFirstName('Alice'):");
        printStream.println("--------------------------------");
        printStream.println(repository.findByFirstNameAndLastName("Alice", "Smith"));

        printStream.println("Customers found with findByLastName('Smith'):");
        printStream.println("--------------------------------");
        for (Customer customer : repository.findByLastName("Smith")) {
            printStream.println(customer + ", id: " + customer.getId());
        }
    }
}
