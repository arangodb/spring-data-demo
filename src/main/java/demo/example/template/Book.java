package demo.example.template;

import org.springframework.data.annotation.Id;

public class Book {

    @Id
    private String id;

    private String name;

    public Book(String name) { this.name = name; }

    public String getId() { return id; }

    @Override
    public String toString() {
        return String.format("Book: {id: '%s', name: '%s'}", id, name);
    }
}
