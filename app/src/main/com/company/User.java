package com.company;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Field;
import org.springframework.data.annotation.Id;

/**
 * Created by user on 10/08/17.
 */
@Document
public class User {

    @Id
    private String id;

    private String name;

    @Field("surname")
    private String lastName;

    public User() {}

    public User(String name) {
        this.name = name;
    }

    public User(String name, String lastName) {
        this.name = name;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return String.format("User: {id: '%s', name: '%s', lastName: '%s'}", id, name, lastName);
    }
}
