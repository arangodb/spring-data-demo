package demo.example.crud;

import com.arangodb.springframework.annotation.*;
import org.springframework.data.annotation.Id;

@HashIndex(fields = {"username", "lvl"})
@Document("users")
public class User {

    @Id
    private String id;

    @Key
    private String key;

    @Rev
    private String rev;

    @FulltextIndexed
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
        return String.format("User: {id: '%s', key: '%s', rev: '%s', username: '%s', level: '%d'}\n", id, key, rev, username, level);
    }
}
