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

import java.util.Arrays;
import java.util.Collection;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.arangodb.spring.demo.entity.Character;
import com.arangodb.spring.demo.repository.CharacterRepository;
import com.arangodb.springframework.core.ArangoOperations;

/**
 * @author Mark Vollmary
 *
 */
@ComponentScan("com.arangodb.spring.demo")
public class CrudRunner implements CommandLineRunner {

	@Autowired
	private ArangoOperations operations;
	@Autowired
	private CharacterRepository repository;

	@Override
	public void run(final String... args) throws Exception {
		// first drop the database so that we can run this multiple times with the same dataset
		operations.dropDatabase();

		System.out.println("# CRUD operations");

		// save a single entity in the database
		// there is no need of creating the collection first. This happen automatically
		final Character nedStark = new Character("Ned", "Stark", true, 41);
		repository.save(nedStark);
		// the generated id from the database is set in the original entity
		System.out.println(String.format("Ned Stark saved in the database with id: '%s'", nedStark.getId()));

		// lets take a look whether we can find Ned Stark in the database
		final Character foundNed = repository.findById(nedStark.getId()).get();
		System.out.println(String.format("Found %s", foundNed));

		// as everyone probably knows Ned Stark died in the first season.
		// So we have to update his 'alive' flag
		nedStark.setAlive(false);
		repository.save(nedStark);
		final Character deadNed = repository.findById(nedStark.getId()).get();
		System.out.println(String.format("Ned Stark after 'alive' flag was updated: %s", deadNed));

		// lets save some additional characters
		final Collection<Character> createCharacters = createCharacters();
		System.out.println(String.format("Save %s additional characters", createCharacters.size()));
		repository.saveAll(createCharacters);

		final Iterable<Character> all = repository.findAll();
		final long count = StreamSupport.stream(Spliterators.spliteratorUnknownSize(all.iterator(), 0), false).count();
		System.out.println(String.format("A total of %s characters are persisted in the database", count));

		System.out.println("## Return all characters sorted by name");
		final Iterable<Character> allSorted = repository.findAll(Sort.by(Sort.Direction.ASC, "name"));
		allSorted.forEach(System.out::println);

		System.out.println("## Return the first 5 characters sorted by name");
		final Page<Character> first5Sorted = repository
				.findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "name")));
		first5Sorted.forEach(System.out::println);
	}

	public static Collection<Character> createCharacters() {
		return Arrays.asList(new Character("Ned", "Stark", false, 41), new Character("Robert", "Baratheon", false),
			new Character("Jaime", "Lannister", true, 36), new Character("Catelyn", "Stark", false, 40),
			new Character("Cersei", "Lannister", true, 36), new Character("Daenerys", "Targaryen", true, 16),
			new Character("Jorah", "Mormont", false), new Character("Petyr", "Baelish", false),
			new Character("Viserys", "Targaryen", false), new Character("Jon", "Snow", true, 16),
			new Character("Sansa", "Stark", true, 13), new Character("Arya", "Stark", true, 11),
			new Character("Robb", "Stark", false), new Character("Theon", "Greyjoy", true, 16),
			new Character("Bran", "Stark", true, 10), new Character("Joffrey", "Baratheon", false, 19),
			new Character("Sandor", "Clegane", true), new Character("Tyrion", "Lannister", true, 32),
			new Character("Khal", "Drogo", false), new Character("Tywin", "Lannister", false),
			new Character("Davos", "Seaworth", true, 49), new Character("Samwell", "Tarly", true, 17),
			new Character("Stannis", "Baratheon", false), new Character("Melisandre", null, true),
			new Character("Margaery", "Tyrell", false), new Character("Jeor", "Mormont", false),
			new Character("Bronn", null, true), new Character("Varys", null, true), new Character("Shae", null, false),
			new Character("Talisa", "Maegyr", false), new Character("Gendry", null, false),
			new Character("Ygritte", null, false), new Character("Tormund", "Giantsbane", true),
			new Character("Gilly", null, true), new Character("Brienne", "Tarth", true, 32),
			new Character("Ramsay", "Bolton", true), new Character("Ellaria", "Sand", true),
			new Character("Daario", "Naharis", true), new Character("Missandei", null, true),
			new Character("Tommen", "Baratheon", true), new Character("Jaqen", "H'ghar", true),
			new Character("Roose", "Bolton", true), new Character("The High Sparrow", null, true));
	}

}
