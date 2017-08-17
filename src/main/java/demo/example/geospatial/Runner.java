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
        LOGGER.log("deleteAll() - cleared 'cities' collection");
        LOGGER.log(BREAK);

        Collection<City> cities = Arrays.asList(new City[] {
                new City("Glasgow", 598830, new double[] {55.858, -4.259}),
                new City("Edinburgh", 495360, new double[] {55.953056, -3.188889}),
                new City("London", 8788000, new double[] {51.507222, -0.1275}),
                new City("Birmingham", 1101000, new double[] {52.483056, -1.893611}),
                new City("Liverpool", 465700, new double[] {53.4, -2.983333})
        });

        repository.save(cities);
        LOGGER.log("All 5 cities:");
        LOGGER.log(BREAK);
        LOGGER.log(cities);
        LOGGER.log();

        Point reading = new Point(-0.973056, 51.454167);

        LOGGER.log("findByPopulationGreaterThanEqualAndLocationWithin(1 000 000, Reading, 60 KM) - expecting London:");
        LOGGER.log(BREAK);
        LOGGER.log(repository.findByPopulationGreaterThanEqualAndLocationWithin(
                1000000, reading, new Distance(60, Metrics.KILOMETERS)));
        LOGGER.log();

        Point glasgow = new Point(-4.259, 55.858);

        LOGGER.log("findByLocationWithinOrNameStartsWith(Glasgow, [100 KM, 500 KM], \"L\") - expecting Liverpool, Birmingham, London:");
        LOGGER.log(BREAK);
        LOGGER.log(repository.findByLocationWithinOrNameStartsWith(
                glasgow, new Range(100000, 500000), "L"));
        LOGGER.log();

        LOGGER.log("findByLocationNearAndPopulationLessThan(Origin, PageRequest[1, 2], 2 000 000) - expecting Glasgow and Edinburgh:");
        LOGGER.log(BREAK);
        Page page = repository.findByLocationNearAndPopulationLessThan(new Point(0, 0), new PageRequest(1, 2), 2000000);
        LOGGER.log(page);
        LOGGER.log(page.getContent());
        LOGGER.log("Total elements: " + page.getTotalElements());
        LOGGER.log("Total pages: " + page.getTotalPages());
        LOGGER.log();
    }
}
