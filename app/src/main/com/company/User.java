package com.company;

import com.arangodb.springframework.annotation.Document;
import org.springframework.data.annotation.Id;

/**
 * Created by user on 10/08/17.
 */
@Document
public class User {

    @Id
    private String id;

    private String name;

    public User() {}

    public User(String name) {
        this.name = name;
    }
}
