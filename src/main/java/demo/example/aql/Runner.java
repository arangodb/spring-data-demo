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
        LOGGER.log("Cleared 'customer' collection");
        LOGGER.log(BREAK);

        Customer john = new Customer("John", "Smith", 56);
        Customer adam = new Customer("Adam", "Smith", 294);
        Customer matt = new Customer("Matt", "Smith", 34);

        Collection<Customer> customers = new HashSet<>();
        customers.add(john);
        customers.add(adam);
        customers.add(matt);

        repository.save(customers);
        LOGGER.log("Add to character collection: ");
        LOGGER.log(BREAK);
        for (Customer c: customers) { LOGGER.log(c); }
        LOGGER.log();

        LOGGER.log("Run getByNameAql, passing 'Adam'");
        LOGGER.log(BREAK);
        Optional<Customer> adamOption = repository.getByNameAql("customer", "Adam", new AqlQueryOptions());
        LOGGER.log(adamOption);
        LOGGER.log();

        LOGGER.log("Run getByIdAndAgeAql, passing 'matt.getId' and '34'");
        LOGGER.log(BREAK);
        Map<String, Object> returnVal = repository.getByIdAndAgeAql(matt.getId(), 34);
        LOGGER.log("Customer {id: " + returnVal.get("_id") + ", name: " + returnVal.get("name") +
                ", surname: " + returnVal.get("surname") + ", age: " + returnVal.get("age") + "}");
        LOGGER.log();

        LOGGER.log("Run getByIdAndNameWithBindVarsAql, passing 'john.getId' and 'John'");
        LOGGER.log(BREAK);
        Map<String, Object> bindVars = new HashMap<>();
        bindVars.put("id", john.getId());
        BaseDocument returned = repository.getByIdAndNameWithBindVarsAql("John", bindVars);
        LOGGER.log("Customer {id: " + returned.getId() + ", name: " + returned.getProperties().get("name") +
                ", surname: " + returned.getProperties().get("surname") + ", age: " + returned.getProperties().get("age") + "}");
        LOGGER.log();

    }
}
