/*
 * DISCLAIMER
 *
 * Copyright 2017 ArangoDB GmbH, Cologne, Germany
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright holder is ArangoDB GmbH, Cologne, Germany
 */

package com.arangodb.spring.demo.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.repository.query.Param;

import com.arangodb.ArangoCursor;
import com.arangodb.spring.demo.entity.Character;
import com.arangodb.springframework.annotation.BindVars;
import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.annotation.QueryOptions;
import com.arangodb.springframework.repository.ArangoRepository;

/**
 * @author Mark Vollmary
 *
 */
public interface CharacterRepository extends ArangoRepository<Character, String> {

	Iterable<Character> findBySurname(String surname);

	Collection<Character> findTop2DistinctBySurnameIgnoreCaseOrderByAgeDesc(String surname);

	List<Character> findBySurnameEndsWithAndAgeBetweenAndNameInAllIgnoreCase(
		String suffix,
		int lowerBound,
		int upperBound,
		String[] nameList);

	Optional<Character> findByNameAndSurname(String name, String surname);

	Integer countByAliveTrue();

	void removeBySurnameNotLikeOrAliveFalse(String surname);

	Iterable<Character> findByChildsName(String name);

	Iterable<Character> findByChildsAgeBetween(int lowerBound, int upperBound);

	@Query("FOR c IN characters FILTER c.age > @value SORT c.age DESC RETURN c")
	Iterable<Character> getOlderThan(int value);

	@Query("FOR c IN characters FILTER c.surname == @surname SORT c.age ASC RETURN c")
	Iterable<Character> getWithSurname(@Param("surname") String value);

	@Query("FOR c IN @@col FILTER c.surname == @surname AND c.age > @age RETURN c")
	@QueryOptions(count = true)
	ArangoCursor<Character> getWithSurnameOlderThan(@Param("age") int value, @BindVars Map<String, Object> bindvars);

	@Query("FOR v IN 1..2 INBOUND @id @@edgeCol SORT v.age DESC RETURN DISTINCT v")
	Set<Character> getAllChildsAndGrandchilds(@Param("id") String id, @Param("@edgeCol") Class<?> edgeCollection);

}
