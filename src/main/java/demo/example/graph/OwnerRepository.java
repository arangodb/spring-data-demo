package demo.example.graph;

import com.arangodb.springframework.core.repository.ArangoRepository;

import java.util.List;

/**
 * Created by markmccormick on 24/08/2017.
 */
public interface OwnerRepository extends ArangoRepository<Owner> {

    List<Owner> findByHouseValueGreaterThan(int value);

    List<Owner> findByHouseRoomsName(String name);

}
