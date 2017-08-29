package demo.example.byreference;

import com.arangodb.springframework.core.repository.ArangoRepository;

import java.util.List;
import java.util.Set;

public interface EmployeeRepository extends ArangoRepository<Employee> {

    Set<Employee> findByManagerNameIn(String[] names);

    List<Employee> findByAssignmentsDescriptionRegex(String pattern);
}
