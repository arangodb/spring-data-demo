package demo.example.crud;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Field;
import com.arangodb.springframework.annotation.Key;
import com.arangodb.springframework.annotation.Rev;
import org.springframework.data.annotation.Id;

@Document("users")
public class User {

    @Id
    private String id;

    @Key
    private String key;

    @Rev
    private String rev;

    private String username;

    @Field("lvl")
    private long level;

    public User(String username, long level) {
        this.username = username;
        this.level = level;
    }

    public String getId() { return id; }

    @Override
    public String toString() {
        return String.format("User: {id: '%s', key: '%s', rev: '%s', username: '%s', level: '%d'}", id, key, rev, username, level);
    }
}
