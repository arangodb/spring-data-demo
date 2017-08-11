package demo.example.byexample;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Field;
import org.springframework.data.annotation.Id;

/**
 * Created by markmccormick on 11/08/2017.
 */
@Document("people")
public class Person {

    @Field("firstname")
    private String name;
    private String surname;
    private long age;

    public Person(String name, String surname, long age) {
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    @Override
    public String toString() {
        return String.format("Person: {name: '%s', surname: '%s', age: '%d'}", name, surname, age);
    }
}
