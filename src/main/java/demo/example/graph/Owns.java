package demo.example.graph;

import com.arangodb.springframework.annotation.*;
import org.springframework.data.annotation.Id;

/**
 * Created by markmccormick on 24/08/2017.
 */
@Edge
public class Owns {

    @Id
    private String id;
    @Key
    private String key;
    @Rev
    private String rev;

    @From
    private Owner from;

    @To
    private House to;

    public Owns() {
        super();
    }

    public Owns(final Owner from, final House to) {
        super();
        this.from = from;
        this.to = to;
    }

    public Owner getFrom() {
        return from;
    }

    public void setFrom(final Owner from) {
        this.from = from;
    }

    public House getTo() {
        return to;
    }

    public void setTo(final House to) {
        this.to = to;
    }

}
