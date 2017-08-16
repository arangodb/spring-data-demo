package demo.example.crud;

import com.arangodb.springframework.core.repository.ArangoRepository;

public interface UserRepository extends ArangoRepository<User> {
}
