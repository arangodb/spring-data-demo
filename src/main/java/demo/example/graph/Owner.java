package demo.example.graph;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Key;
import com.arangodb.springframework.annotation.Relations;
import com.arangodb.springframework.annotation.Rev;
import org.springframework.data.annotation.Id;

/**
 * Created by markmccormick on 24/08/2017.
 */
@Document
public class Owner {

    @Id
    private String id;
    @Key
    private String key;
    @Rev
    private String rev;

    private String name;
    private int age;

    @Relations(edges = {Owns.class})
    private House house;

    public Owner(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    @Override
    public String toString() {
        return "Owner {name: " + name + ", age: " + age + "}";
    }
}
