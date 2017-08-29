package demo.example.graph;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Key;
import com.arangodb.springframework.annotation.Rev;
import org.springframework.data.annotation.Id;

/**
 * Created by markmccormick on 24/08/2017.
 */
@Document
public class Room {

    @Id
    private String id;
    @Key
    private String key;
    @Rev
    private String rev;

    private String name;

    public Room(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
