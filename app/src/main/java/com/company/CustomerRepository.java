package com.company;

import java.util.List;

import com.arangodb.springframework.core.repository.ArangoRepository;

public interface CustomerRepository extends ArangoRepository<Customer> {

    Customer findByFirstNameAndLastName(String firstName, String lastName);

    List<Customer> findByLastName(String lastName);
}
