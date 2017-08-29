package demo.example.derived;

import com.arangodb.springframework.core.repository.ArangoRepository;

import java.util.Collection;

public interface CharacterRepository extends ArangoRepository<Character> {

    Integer countByAliveTrue();

    Collection<Character> findTop2DistinctBySurnameIgnoreCaseOrderByAgeDesc(String surname);

    Collection<Character> findBySurnameEndsWithAndAgeBetweenAndNameInAllIgnoreCase(String suffix, int lowerBound, int upperBound, String[] nameList);

    void removeBySurnameNotLikeOrAliveFalse(String surnamePattern);
}
