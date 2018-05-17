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

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import com.arangodb.spring.demo.entity.Character;
import com.arangodb.spring.demo.repository.CharacterRepository;

/**
 * @author Mark Vollmary
 *
 */
@ComponentScan("com.arangodb.spring.demo")
public class ByExampleRunner implements CommandLineRunner {

	@Autowired
	private CharacterRepository repository;

	@Override
	public void run(final String... args) throws Exception {
		System.out.println("# Query by example");

		final Character nedStark = new Character("Ned", "Stark", false, 41);

		System.out.println(String.format("## Find character which match %s", nedStark));
		final Character foundNedStark = repository.findOne(Example.of(nedStark)).get();
		System.out.println(String.format("Found %s", foundNedStark));

		System.out.println("## Find all dead Starks");
		// because we only care of surname and alive in our entity we have to ignore the other
		// fields in our ExampleMatcher
		final Iterable<Character> allDeadStarks = repository
				.findAll(Example.of(new Character(null, "Stark", false), ExampleMatcher.matchingAll()
						.withMatcher("surname", match -> match.exact()).withIgnorePaths("name", "age")));
		allDeadStarks.forEach(System.out::println);

		System.out.println("## Find all Starks which are 30 years younger than Ned Stark");
		// instead of changing the age for the Ned Stark entity use a transformer within the ExampleMatcher.
		// Because we are using the entity fetched from the db we have to ignore the field 'id' which isn't null.
		final Iterable<Character> allYoungerStarks = repository.findAll(Example.of(foundNedStark,
			ExampleMatcher.matchingAll().withMatcher("surname", match -> match.exact())
					.withIgnorePaths("id", "name", "alive")
					.withTransformer("age", age -> Optional.of(((int) age.get()) - 30))));
		allYoungerStarks.forEach(System.out::println);

		System.out.println("## Find all character which surname ends with 'ark' (ignore case)");
		final Iterable<Character> ark = repository.findAll(Example.of(new Character(null, "ark", false),
			ExampleMatcher.matchingAll().withMatcher("surname", match -> match.endsWith()).withIgnoreCase()
					.withIgnorePaths("name", "alive", "age")));
		ark.forEach(System.out::println);
	}

}
