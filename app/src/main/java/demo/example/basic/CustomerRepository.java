package demo.example.basic;

import java.util.List;

import com.arangodb.springframework.core.repository.ArangoRepository;
import demo.example.basic.Customer;

public interface CustomerRepository extends ArangoRepository<Customer> {

    Customer findByFirstNameAndLastName(String firstName, String lastName);

    List<Customer> findByLastName(String lastName);
}
