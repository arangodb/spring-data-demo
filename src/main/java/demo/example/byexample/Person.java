package demo.example.byexample;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Field;

@Document("people")
public class Person {

    @Field("firstname")
    private String name;
    private String surname;
    private int age;

    public Person(String name, String surname, int age) {
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    @Override
    public String toString() {
        return String.format("Person: {name: '%s', surname: '%s', age: '%d'}", name, surname, age);
    }
}
