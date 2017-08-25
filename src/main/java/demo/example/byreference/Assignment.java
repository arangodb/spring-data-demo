package demo.example.byreference;

import org.springframework.data.annotation.Id;

public class Assignment {

    @Id
    private String id;

    private final String description;

    public Assignment(String description) { this.description = description; }

    public String getDescription() { return description; }
}
