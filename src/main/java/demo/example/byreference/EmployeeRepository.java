package demo.example.byreference;

import com.arangodb.springframework.core.repository.ArangoRepository;

import java.util.List;
import java.util.Set;

public interface EmployeeRepository extends ArangoRepository<Employee> {

    Set<Employee> findByManagerNameIn(String[] names);

    Set<Employee> findByManagerStartsWith(String prefix);

    Set<Employee> findByManagerNull();

    List<Employee> findByAssignmentsDescriptionRegex(String pattern);
}
