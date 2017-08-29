package demo.example.geospatial;

import com.arangodb.springframework.core.repository.ArangoRepository;
import org.springframework.data.domain.Range;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.*;

import java.util.List;

public interface CityRepository extends ArangoRepository<City> {

    //                                                                                 could be replaced with geo.Circle
    GeoResult<City> findByPopulationGreaterThanEqualAndLocationWithin(long population, Point location, Distance distance);

    //                                                    could be replaced with geo.Ring
    GeoResults<City> findByLocationWithinOrNameStartsWith(Point location, Range<Double> distanceRange, String prefix);

    GeoPage<City> findByLocationNearAndPopulationLessThan(Point location, Pageable pageable, int population);

    List<City> findByLocationWithin(Polygon polygon);
}
