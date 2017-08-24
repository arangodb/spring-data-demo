package demo.example.graph;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Key;
import com.arangodb.springframework.annotation.Relations;
import com.arangodb.springframework.annotation.Rev;
import org.springframework.data.annotation.Id;

import java.util.Collection;

/**
 * Created by markmccormick on 24/08/2017.
 */
@Document
public class House {
    @Id
    private String id;
    @Key
    private String key;
    @Rev
    private String rev;

    private String address;
    private int value;

    @Relations(edges = {Contains.class})
    private Collection<Room> rooms;

    public House(String address, int value) {
        this.address = address;
        this.value = value;
    }

    public Collection<Room> getRooms() {
        return rooms;
    }

    public void setRooms(Collection<Room> rooms) {
        this.rooms = rooms;
    }
}
