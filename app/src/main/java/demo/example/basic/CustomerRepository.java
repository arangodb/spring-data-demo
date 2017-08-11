package demo.example.basic;

import java.util.List;

import com.arangodb.springframework.core.repository.ArangoRepository;
import demo.example.basic.Customer;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;

public interface CustomerRepository extends ArangoRepository<Customer> {

    Customer findByFirstNameAndLastName(String firstName, String lastName);

    List<Customer> findByLastName(String lastName);

    GeoResult<Customer> findByLocationWithin(Point location, Distance distance);
}
