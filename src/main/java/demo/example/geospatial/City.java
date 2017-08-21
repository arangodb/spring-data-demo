package demo.example.geospatial;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.GeoIndexed;

import java.util.List;

@Document("cities")
public class City {

    private String name;
    private long population;

    @GeoIndexed
    private double[] location;

    public City(String name, long population, double[] location) {
        this.name = name;
        this.population = population;
        this.location = location;
    }

    @Override
    public String toString() {
        return String.format("City : {name: '%s', population: '%d, location: [%f, %f]}",
                name, population, location[0], location[1]);
    }
}
