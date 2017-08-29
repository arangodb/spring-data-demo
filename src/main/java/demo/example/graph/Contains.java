package demo.example.graph;

import com.arangodb.springframework.annotation.*;
import org.springframework.data.annotation.Id;

/**
 * Created by markmccormick on 24/08/2017.
 */
@Edge
public class Contains {

    @Id
    private String id;
    @Key
    private String key;
    @Rev
    private String rev;

    @From
    private House from;

    @To
    private Room to;

    public Contains() {
        super();
    }

    public Contains(final House from, final Room to) {
        super();
        this.from = from;
        this.to = to;
    }

    public House getFrom() {
        return from;
    }

    public void setFrom(final House from) {
        this.from = from;
    }

    public Room getTo() {
        return to;
    }

    public void setTo(final Room to) {
        this.to = to;
    }

}
