package demo.example.byexample;

import com.arangodb.springframework.core.repository.ArangoRepository;

/**
 * Created by markmccormick on 11/08/2017.
 */
public interface PersonRepository extends ArangoRepository<Person> {
}
