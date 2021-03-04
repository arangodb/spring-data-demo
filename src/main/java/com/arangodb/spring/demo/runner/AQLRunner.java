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

import com.arangodb.ArangoCursor;
import com.arangodb.spring.demo.entity.Character;
import com.arangodb.spring.demo.entity.ChildOf;
import com.arangodb.spring.demo.repository.CharacterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Mark Vollmary
 */
public class AQLRunner implements CommandLineRunner {

    @Autowired
    private CharacterRepository repository;

    @Override
    public void run(final String... args) throws Exception {
        System.out.println("# AQL queries");

        System.out.println("## Find all characters with surname 'Lannister' (sort by age ascending)");
        Iterable<Character> lannisters = repository.getWithSurname("Lannister");
        lannisters.forEach(System.out::println);

        System.out.println("## Find all characters with surname 'Lannister' which are older than 35");
        Map<String, Object> bindvars = new HashMap<>();
        bindvars.put("surname", "Lannister");
        bindvars.put("@col", Character.class);
        ArangoCursor<Character> oldLannisters = repository.getWithSurnameOlderThan(35, bindvars);
        System.out.println(String.format("Found %s documents", oldLannisters.getCount()));
        oldLannisters.forEach(System.out::println);

        System.out.println("## Find all childs and grantchilds of 'Tywin Lannister' (sort by age descending)");
        repository.findByNameAndSurname("Tywin", "Lannister").ifPresent(tywin -> {
            Set<Character> childs = repository.getAllChildsAndGrandchilds(tywin.getArangoId(), ChildOf.class);
            childs.forEach(System.out::println);
        });
    }

}
