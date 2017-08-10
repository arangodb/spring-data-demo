package com.company;

import com.arangodb.springframework.core.repository.ArangoRepository;

/**
 * Created by user on 10/08/17.
 */
public interface UserRepository extends ArangoRepository<User> {
}
