package demo.example.aql;

import com.arangodb.entity.BaseDocument;
import com.arangodb.model.AqlQueryOptions;
import com.arangodb.springframework.annotation.BindVars;
import com.arangodb.springframework.annotation.Param;
import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.core.repository.ArangoRepository;

import java.util.Map;
import java.util.Optional;

public interface CustomerRepository extends ArangoRepository<Customer> {

    @Query("FOR c in @@collection FILTER c.name == @name RETURN c")
    Optional<Customer> getByNameAql(@Param("@collection") String collection, @Param("name") String name, AqlQueryOptions options);

    @Query("FOR c in customer FILTER c._id == @0 AND c.age == @1 RETURN c")
    Map<String, Object> getByIdAndAgeAql(String id, long age);

    @Query("FOR c IN customer FILTER c._id == @id AND c.name == @0 RETURN c")
    BaseDocument getByIdAndNameWithBindVarsAql(String name, @BindVars Map bindVars);
}
