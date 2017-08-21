package demo.example.derived;

import com.arangodb.springframework.annotation.*;
import org.springframework.data.annotation.Id;

@Document
public class Character {

    @Id
    private String id;

    private String name;
    private String surname;
    private int age;
    private boolean alive;

    public Character() { }

    public Character(final String name, final String surname, final int age, final boolean alive) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.alive = alive;
    }

    @Override
    public String toString() {
        return "Character {id: " + id + ", name: " + name + ", surname: " + surname + ", age: " + age + "}";
    }
}
