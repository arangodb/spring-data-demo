package demo.example.basic;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Field;
import com.arangodb.springframework.annotation.GeoIndexed;
import org.springframework.data.annotation.Id;

@Document("customers")
public class Customer {

    @Id
    private String id;

    private String firstName;

    @Field("surname")
    private String lastName;

    @GeoIndexed
    private double[] location;

    public Customer(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public double[] getLocation() { return location; }

    public void setLocation(double[] location) { this.location = location; }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%s, firstName='%s', lastName='%s']",
                id, firstName, lastName);
    }
}

