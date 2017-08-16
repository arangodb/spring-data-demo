package demo.example.geospatial;

import demo.AbstractRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Range;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;

import java.util.Arrays;
import java.util.Collection;

@ComponentScan("demo")
public class Runner extends AbstractRunner {

    @Autowired
    CityRepository repository;

    @Override
    public void run(String... args) throws Exception {

        repository.deleteAll();
        LOGGER.warn("deleteAll() - cleared 'cities' collection");
        PRINT_STREAM.println(BREAK);

        Collection<City> cities = Arrays.asList(new City[] {
                new City("Glasgow", 598830, Arrays.asList(new Double[] {55.858, -4.259})),
                new City("Edinburgh", 495360, Arrays.asList(new Double[] {55.953056, -3.188889})),
                new City("London", 8788000, Arrays.asList(new Double[] {51.507222, -0.1275})),
                new City("Birmingham", 1101000, Arrays.asList(new Double[] {52.483056, -1.893611})),
                new City("Liverpool", 465700, Arrays.asList(new Double[] {53.4, -2.983333}))
        });

        repository.save(cities);
        PRINT_STREAM.println("All 5 cities:");
        PRINT_STREAM.println(BREAK);
        PRINT_STREAM.println(cities);
        PRINT_STREAM.println();

        Point reading = new Point(-0.973056, 51.454167);

        PRINT_STREAM.println("findByPopulationGreaterThanEqualAndLocationWithin(1 000 000, Reading, 60 KM) - expecting London:");
        PRINT_STREAM.println(BREAK);
        PRINT_STREAM.println(repository.findByPopulationGreaterThanEqualAndLocationWithin(
                1000000, reading, new Distance(60, Metrics.KILOMETERS)));
        PRINT_STREAM.println();

        Point glasgow = new Point(-4.259, 55.858);

        PRINT_STREAM.println("findByLocationWithinOrNameStartsWith(Glasgow, [100 KM, 500 KM], \"L\") - expecting Liverpool, Birmingham, London:");
        PRINT_STREAM.println(BREAK);
        PRINT_STREAM.println(repository.findByLocationWithinOrNameStartsWith(
                glasgow, new Range(100000, 500000), "L"));
        PRINT_STREAM.println();

        PRINT_STREAM.println("findByLocationNearAndPopulationLessThan(Origin, PageRequest[1, 2], 2 000 000) - expecting Glasgow and Edinburgh:");
        PRINT_STREAM.println(BREAK);
        Page page = repository.findByLocationNearAndPopulationLessThan(new Point(0, 0), new PageRequest(1, 2), 2000000);
        PRINT_STREAM.println(page);
        PRINT_STREAM.println(page.getContent());
        PRINT_STREAM.println("Total elements: " + page.getTotalElements());
        PRINT_STREAM.println("Total pages: " + page.getTotalPages());
        PRINT_STREAM.println();
    }
}
