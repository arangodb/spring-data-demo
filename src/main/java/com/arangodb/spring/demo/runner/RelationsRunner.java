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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;

import com.arangodb.spring.demo.entity.Character;
import com.arangodb.spring.demo.entity.ChildOf;
import com.arangodb.spring.demo.repository.CharacterRepository;
import com.arangodb.spring.demo.repository.ChildOfRepository;

/**
 * @author Mark Vollmary
 *
 */
@ComponentScan("com.arangodb.spring.demo")
public class RelationsRunner implements CommandLineRunner {

	@Autowired
	private CharacterRepository characterRepo;
	@Autowired
	private ChildOfRepository childOfRepo;

	@Override
	public void run(final String... args) throws Exception {
		System.out.println("# Relations");
		characterRepo.saveAll(CrudRunner.createCharacters());

		// first create some relations for the Starks and Lannisters
		characterRepo.findByNameAndSurname("Ned", "Stark").ifPresent(ned -> {
			characterRepo.findByNameAndSurname("Catelyn", "Stark").ifPresent(catelyn -> {
				characterRepo.findByNameAndSurname("Robb", "Stark").ifPresent(robb -> {
					childOfRepo.saveAll(Arrays.asList(new ChildOf(robb, ned), new ChildOf(robb, catelyn)));
				});
				characterRepo.findByNameAndSurname("Sansa", "Stark").ifPresent(sansa -> {
					childOfRepo.saveAll(Arrays.asList(new ChildOf(sansa, ned), new ChildOf(sansa, catelyn)));
				});
				characterRepo.findByNameAndSurname("Arya", "Stark").ifPresent(arya -> {
					childOfRepo.saveAll(Arrays.asList(new ChildOf(arya, ned), new ChildOf(arya, catelyn)));
				});
				characterRepo.findByNameAndSurname("Bran", "Stark").ifPresent(bran -> {
					childOfRepo.saveAll(Arrays.asList(new ChildOf(bran, ned), new ChildOf(bran, catelyn)));
				});
			});
			characterRepo.findByNameAndSurname("Jon", "Snow")
					.ifPresent(bran -> childOfRepo.save(new ChildOf(bran, ned)));
		});

		characterRepo.findByNameAndSurname("Tywin", "Lannister").ifPresent(tywin -> {
			characterRepo.findByNameAndSurname("Jaime", "Lannister").ifPresent(jaime -> {
				childOfRepo.save(new ChildOf(jaime, tywin));
				characterRepo.findByNameAndSurname("Cersei", "Lannister").ifPresent(cersei -> {
					childOfRepo.save(new ChildOf(cersei, tywin));
					characterRepo.findByNameAndSurname("Joffrey", "Baratheon").ifPresent(joffrey -> {
						childOfRepo.saveAll(Arrays.asList(new ChildOf(joffrey, jaime), new ChildOf(joffrey, cersei)));
					});
				});
			});
			characterRepo.findByNameAndSurname("Tyrion", "Lannister")
					.ifPresent(tyrion -> childOfRepo.save(new ChildOf(tyrion, tywin)));
		});

		// after we add `@Relations(edges = ChildOf.class, lazy = true) Collection<Character> childs;` in Character
		// we can now load all children of a Character when we fetch the character
		characterRepo.findByNameAndSurname("Ned", "Stark").ifPresent(nedStark -> {
			System.out.println(String.format("## These are the children of %s:", nedStark));
			nedStark.getChilds().forEach(System.out::println);
		});

		// the fields 'childs' isn't persisted in the character document itself, it's represented through
		// the edges. Nevertheless we can write a derived method which includes properties of the connected character
		System.out.println("## These are the parents of 'Sansa'");
		final Iterable<Character> parentsOfSansa = characterRepo.findByChildsName("Sansa");
		parentsOfSansa.forEach(System.out::println);

		System.out.println("## These parents have a child which is between 16 and 20 years old");
		final Iterable<Character> childsBetween16a20 = characterRepo.findByChildsAgeBetween(16, 20);
		childsBetween16a20.forEach(System.out::println);
	}

}
