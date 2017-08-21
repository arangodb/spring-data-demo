package demo.example.aql;

import com.arangodb.springframework.annotation.*;
import org.springframework.data.annotation.Id;

public class Customer {

    @Id
    private String id;

    private String name;
    private String surname;
    private int age;

    public Customer() { super(); }

    public Customer(final String name, final String surname, final int age) {
        super();
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Customer {id: " + id + ", name: " + name + ", surname: " + surname + ", age: " + age + "}";
    }

    public String getId() { return id; }
}
