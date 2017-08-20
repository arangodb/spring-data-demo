package demo.example.byreference;

import com.arangodb.springframework.core.repository.ArangoRepository;

import java.util.List;

public interface EmployeeRepository extends ArangoRepository<Employee> {

    Employee findByManagerNameStartsWith(String prefix);

    List<Employee> findByColleaguesNameRegex(String pattern);
}
