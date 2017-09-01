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

package com.arangodb.spring.demo.entity;

import java.util.Collection;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.HashIndex;
import com.arangodb.springframework.annotation.Relations;

/**
 * @author Mark Vollmary
 *
 */
@Document("characters")
@HashIndex(fields = { "name", "surname" }, unique = true)
public class Character {

	@Id
	private String id;

	private String name;
	private String surname;
	private boolean alive;
	private Integer age;
	@Relations(edges = ChildOf.class, lazy = true)
	private Collection<Character> childs;

	public Character() {
		super();
	}

	public Character(final String name, final String surname, final boolean alive) {
		super();
		this.name = name;
		this.surname = surname;
		this.alive = alive;
	}

	public Character(final String name, final String surname, final boolean alive, final Integer age) {
		super();
		this.name = name;
		this.surname = surname;
		this.alive = alive;
		this.age = age;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(final String surname) {
		this.surname = surname;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(final boolean alive) {
		this.alive = alive;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(final Integer age) {
		this.age = age;
	}

	public Collection<Character> getChilds() {
		return childs;
	}

	public void setChilds(final Collection<Character> childs) {
		this.childs = childs;
	}

	@Override
	public String toString() {
		return "Character [id=" + id + ", name=" + name + ", surname=" + surname + ", alive=" + alive + ", age=" + age
				+ "]";
	}

}
