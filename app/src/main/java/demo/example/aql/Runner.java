package demo.example.aql;

import com.arangodb.entity.BaseDocument;
import com.arangodb.model.AqlQueryOptions;
import demo.AbstractRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

import java.util.*;

@ComponentScan("demo")
public class Runner extends AbstractRunner {

    @Autowired
    private CustomerRepository repository;

    @Override
    public void run(String... args) throws Exception {

        repository.deleteAll();
        PRINT_STREAM.println("Cleared 'customer' collection");
        PRINT_STREAM.println(BREAK);

        Customer john = new Customer("John", "Smith", 56);
        Customer adam = new Customer("Adam", "Smith", 294);
        Customer matt = new Customer("Matt", "Smith", 34);

        Collection<Customer> customers = new HashSet<>();
        customers.add(john);
        customers.add(adam);
        customers.add(matt);

        repository.save(customers);
        PRINT_STREAM.println("Add to character collection: ");
        PRINT_STREAM.println(BREAK);
        for (Customer c: customers) { PRINT_STREAM.println(c); }
        PRINT_STREAM.println();

        PRINT_STREAM.println("Run getByNameAql, passing 'Adam'");
        PRINT_STREAM.println(BREAK);
        Optional<Customer> adamOption = repository.getByNameAql("customer", "Adam", new AqlQueryOptions());
        PRINT_STREAM.println(adamOption);
        PRINT_STREAM.println();

        PRINT_STREAM.println("Run getByIdAndAgeAql, passing 'matt.getId' and '34'");
        PRINT_STREAM.println(BREAK);
        Map<String, Object> returnVal = repository.getByIdAndAgeAql(matt.getId(), 34);
        PRINT_STREAM.println("Customer {id: " + returnVal.get("_id") + ", name: " + returnVal.get("name") +
                ", surname: " + returnVal.get("surname") + ", age: " + returnVal.get("age") + "}");
        PRINT_STREAM.println();

        PRINT_STREAM.println("Run getByIdAndNameWithBindVarsAql, passing 'john.getId' and 'John'");
        PRINT_STREAM.println(BREAK);
        Map<String, Object> bindVars = new HashMap<>();
        bindVars.put("id", john.getId());
        BaseDocument returned = repository.getByIdAndNameWithBindVarsAql("John", bindVars);
        PRINT_STREAM.println("Customer {id: " + returned.getId() + ", name: " + returned.getProperties().get("name") +
                ", surname: " + returned.getProperties().get("surname") + ", age: " + returned.getProperties().get("age") + "}");
        PRINT_STREAM.println();

    }
}
