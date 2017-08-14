package demo.example.geospatial;

import com.arangodb.springframework.core.repository.ArangoRepository;
import com.arangodb.springframework.core.repository.query.derived.geo.Range;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.*;

public interface CityRepository extends ArangoRepository<City> {

    GeoResult<City> findByPopulationGreaterThanEqualAndLocationWithin(long population, Point location, Distance distance);

    GeoResults<City> findByLocationWithinOrNameStartsWith(Point location, Range<Double> distanceRange, String prefix);

    GeoPage<City> findByLocationNearAndPopulationLessThan(Point location, Pageable pageable, int population);
}
