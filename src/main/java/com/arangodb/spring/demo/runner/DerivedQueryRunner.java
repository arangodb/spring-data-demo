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

package com.arangodb.spring.demo.runner;

import com.arangodb.spring.demo.entity.Character;
import com.arangodb.spring.demo.repository.CharacterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Mark Vollmary
 */
@ComponentScan("com.arangodb.spring.demo")
public class DerivedQueryRunner implements CommandLineRunner {

    @Autowired
    private CharacterRepository repository;

    @Override
    public void run(final String... args) throws Exception {
        System.out.println("# Derived queries");

        System.out.println("## Find all characters with surname 'Lannister'");
        Iterable<Character> lannisters = repository.findBySurname("Lannister");
        lannisters.forEach(System.out::println);

        System.out.println("## Find top 2 Lannnisters ordered by age");
        Collection<Character> top2 = repository.findTop2DistinctBySurnameIgnoreCaseOrderByAgeDesc("lannister");
        top2.forEach(System.out::println);

        System.out.println(
                "## Find all characters which name is 'Bran' or 'Sansa' and it's surname ends with 'ark' and are between 10 and 16 years old");
        List<Character> youngStarks = repository.findBySurnameEndsWithAndAgeBetweenAndNameInAllIgnoreCase("ark",
                10, 16, new String[]{"Bran", "Sansa"});
        youngStarks.forEach(System.out::println);

        System.out.println("## Find a single character by name & surname");
        Optional<Character> tyrion = repository.findByNameAndSurname("Tyrion", "Lannister");
        tyrion.ifPresent(c -> System.out.println(String.format("Found %s", c)));

        System.out.println("## Count how many characters are still alive");
        Integer alive = repository.countByAliveTrue();
        System.out.println(String.format("There are %s characters still alive", alive));

        System.out.println("## Remove all characters except of which surname is 'Stark' and which are still alive");
        repository.removeBySurnameNotLikeOrAliveFalse("Stark");
        repository.findAll().forEach(System.out::println);
    }
}
