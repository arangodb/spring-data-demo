![ArangoDB-Logo](https://docs.arangodb.com/assets/arangodb_logo_2016_inverted.png)

# Spring Data ArangoDB - Demo

This is a short demo how to use [Spring Data ArangoDB](https://github.com/arangodb/spring-data) with an example dataset of **Game of Thrones** characters and locations.

## Table of Contents

* [Getting Started](#getting-started)
  * [Build a project with Maven](#build-a-project-with-maven)
  * [Create an Application class](#create-an-application-class)
  * [Create an Configuration class](#create-an-configuration-class)
* [Data modeling](#data-modeling)
* [CRUD operations](#crud-operations)
  * [Create a repository](#create-a-repository)
  * [Create an CommandLineRunner](#create-an-commandlinerunner)
  * [Save and read an entity](#save-and-read-an-entity)
  * [Run the demo](#run-the-demo)
  * [Update an entity](#update-an-entity)
  * [Save and read multiple entities](#save-and-read-multiple-entities)
  * [Create an index](#create-an-index)
  * [Read with sorting and paging](#read-with-sorting-and-paging)
* [Query by example](#query-by-example)
  * [Single entity](#single-entity)
  * [Multiple entities](#multiple-entities)
* [Derived queries](#derived-queries)
  * [Simple findBy](#simple-findby)
  * [More complexe findBy](#more-complexe-findby)
  * [Single entity result](#single-entity-result)
  * [countBy](#countby)
  * [removeBy](#removeby)
* [Relations](#relations)
  * [Read relations within an entity](#read-relations-within-an-entity)
  * [findBy including relations](#findby-including-relations)
* [Query methods](#query-methods)
  * [Number matching](#number-matching)
  * [Param annotation](#param-annotation)
  * [BindVars annotation](#bindvars-annotation)
  * [QueryOptions annotation](#queryoptions-annotation)
  * [Graph traversal](#graph-traversal)
* [Geospacial queries](#geospacial-queries)


# Getting Started

## Build a project with Maven

First we have to setup a project and add every needed dependency. For this demo we use `Maven` and `Spring Boot`.

We have to create a maven pom.xml:
``` xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.arangodb</groupId>
  <artifactId>spring-data-demo</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <packaging>jar</packaging>

  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.5.6.RELEASE</version>
  </parent>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <java.version>1.8</java.version>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter</artifactId>
    </dependency>
    <dependency>
      <groupId>com.arangodb</groupId>
      <artifactId>spring-data</artifactId>
      <version>1.0.0</version>
    </dependency>
  </dependencies>

</project>
```

## Create an Application class

After we have ensured that we can fetch all the necessary dependencies, we can create our first classes.

The class `DemoApplication` is our main class where we will later add certain `CommandLineRunner` instances to be executed.

``` java
package com.arangodb.spring.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
  public static void main(final String... args) {
    Object[] runner = new Object[] {};
    System.exit(SpringApplication.exit(SpringApplication.run(runner, args)));
  }
}
```

## Create an Configuration class

We also need a configuration class to setup everthing to connect to our ArangoDB instance and to declare that all needed Spring Beans are processed by the Spring container.

* `@EnableArangoRepositories` Defines where Spring can find your repositories
* `arango()` Method to configure the connection to the ArangoDB instance
* `database()` Method to define the database name

``` java
package com.arangodb.spring.demo;

import org.springframework.context.annotation.Configuration;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDB.Builder;
import com.arangodb.springframework.annotation.EnableArangoRepositories;
import com.arangodb.springframework.config.AbstractArangoConfiguration;

@Configuration
@EnableArangoRepositories(basePackages = { "com.arangodb.spring.demo" })
public class DemoConfiguration extends AbstractArangoConfiguration {
  @Override
  public Builder arango() {
    return new ArangoDB.Builder().host("localhost", 8529).user("root").password(null);
  }

  @Override
  public String database() {
    return "spring-demo";
  }
}
```

# Data modeling

Lets create our first bean which will represent a collection in our database. With the `@Document` annotation we define the collection as a document collection. In our case we also define the alternative name *characters* for the collection. By default the collection name is determined by the class name. `@Document` also provides additional options for the collection which will be used at creation time of the collection.

Because many operations on documents requires a document handle it's recommended to add a field of type `String` annotated wit `@Id` to every entity. The name doesn't matter. It's further recommended not to set or change the id by hand.

``` java
package com.arangodb.spring.demo.entity;

import org.springframework.data.annotation.Id;
import com.arangodb.springframework.annotation.Document;

@Document("characters")
public class Character {

  @Id
  private String id;

  private String name;
  private String surname;
  private boolean alive;
  private Integer age;

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

  // getter & setter

  @Override
  public String toString() {
    return "Character [id=" + id + ", name=" + name + ", surname=" + surname + ", alive=" + alive + ", age=" + age + "]";
  }

}
```

# CRUD operations

## Create a repository

Now that we have our data model we want to store data. For this we create a repository interface which extends `ArangoRepository`. This give use access to CRUD operations, pagein and query by example mechanics.

``` java
package com.arangodb.spring.demo.repository;

import com.arangodb.spring.demo.entity.Character;
import com.arangodb.springframework.repository.ArangoRepository;

public interface CharacterRepository extends ArangoRepository<Character> {

}
```

## Create an CommandLineRunner

To run our demo with Spring Boot we have to create a class implementing `CommandLineRunner`. In this class we can use the `@Authowired` annotation to inject our `CharacterRepository` - we created one step earlier - and also `ArangoOperations` which offers a central support for interactions with the database over a rich feature set. It mostly offers the features from the [ArangoDB Java driver](https://github.com/arangodb/arangodb-java-driver) with additional exception translation.

To get the injection successfully running we have to add `@ComponentScan` to our runner to define where Spring can find our confugiration class `DemoConfiguration`.

``` java
package com.arangodb.spring.demo.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;

import com.arangodb.spring.demo.entity.Character;
import com.arangodb.spring.demo.repository.CharacterRepository;
import com.arangodb.springframework.core.ArangoOperations;

@ComponentScan("com.arangodb.spring.demo")
public class CrudRunner implements CommandLineRunner {

  @Autowired
  private ArangoOperations operations;
  @Autowired
  private CharacterRepository repository;

  @Override
  public void run(final String... args) throws Exception {

  }

}
```

## Save and read an entity

It's time to save our first entity in the database. Both the database and the collection don't have to be created on our own. This happens automatically as soon as we execute a database request with the components involved, we don't have to leave the Java world to manage our database.

After we saved a character in the database the id in the original entity is updated with the one generated from the database. We can then use this id to find our persisted entity.

``` java
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

    // save a single entity in the database
    // there is no need of creating the collection first. This happen automatically
    final Character nedStark = new Character("Ned", "Stark", true, 41);
    repository.save(nedStark);
    // the generated id from the database is set in the original entity
    System.out.println(String.format("Ned Stark saved in the database with id: '%s'", nedStark.getId()));

    // lets take a look whether we can find Ned Stark in the database
    final Character foundNed = repository.findOne(nedStark.getId());
    System.out.println(String.format("Found %s", foundNed));
  }

}
```

## Run the demo

The last thing we have to do before we can successfully run our demo application is to add our command line runner `CrudRunner` in the list of runners in our main class `DemoApplication`.

``` java
Object[] runner = new Object[] { CrudRunner.class };
```

After executing the demo application we should see the following lines within our console output. The id will of course deviate.

```
Ned Stark saved in the database with id: 'characters/259672'
Found Character [id=characters/259672, name=Ned, surname=Stark, alive=true, age=41]
```

## Update an entity

As everyone probably knows, Ned Stark died in the first season of Game of Thrones. So we have to update his 'alive' flag. Thanks to our `id` field in the class `Character`, we can use the method `save()` from our repository to perform an upsert with our variable `nedStark` in which `id` is already set.

Lets add the following lines of code to the end of our `run()` method in `CrudRunner`.

``` java
nedStark.setAlive(false);
repository.save(nedStark);
final Character deadNed = repository.findOne(nedStark.getId());
System.out.println(String.format("The 'alive' flag of the persisted Ned Stark is now '%s'", deadNed.isAlive()));
```

If we run our demo a second time the console output should look:

```
Ned Stark saved in the database with id: 'characters/261070'
Found Character [id=characters/261070, name=Ned, surname=Stark, alive=true, age=41]
Ned Stark after 'alive' flag was updated: Character [id=characters/261070, name=Ned, surname=Stark, alive=false, age=41]
```

## Save and read multiple entities

What we can do with a single entity, we can also do with multiple entities. It's not just a single method call for convenience purpose, it also requires only one database request.

Let's save a bunch of characters. Only main casts of Game of Thrones but that's already a lot. After that we fetch all of them from our collection and count them.

Extend the `run()` method with these lines of code.

``` java
Collection<Character> createCharacters = createCharacters();
System.out.println(String.format("Save %s additional chracters", createCharacters.size()));
repository.save(createCharacters);

Iterable<Character> all = repository.findAll();
long count = StreamSupport.stream(Spliterators.spliteratorUnknownSize(all.iterator(), 0), false).count();
System.out.println(String.format("A total of %s characters are persisted in the database", count));
```

We also need the method `createCharacters()` which looks as follow:

``` java
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
```

After executing the demo again the console should print the following additional lines:

```
Save 43 additional characters
A total of 44 characters are persisted in the database
```

Counting is just an example of what you can do with the returned entities and it's also a bad one. Fetching every entity from a collection only to count them is very inefficient. As an alternative we can use `ArangoOperations` for it.

``` java
long count = operations.collection(Character.class).count();
```

## Create an index

As you can see, we saved 43 additional characters and fetched a total of 44 characters from the database. The one more is our previews saved character Ned Stark. When you take a closer look into `createCharacters()` you will see that there is also an character instance for Ned Stark. So now we have two Ned Starks in our collection.

To avoid this we could create a unique hash index on our collection `characters` for the field `name` and `surname`. To do so we have to add the `@HashIndex` annotation in our class `Character`. The first time our collection will be used within our program the index will be automatically created on the collection.

``` java
@Document("characters")
@HashIndex(fields = { "name", "surname" }, unique = true)
public class Character {
```

The next time we run our demo the output will be changed to:

```
Save 43 additional characters
A total of 43 characters are persisted in the database
```

## Read with sorting and paging

Next to the normal `findAll()`, `ArangoRepository` also offers the ability to sort the fetched entities by a given field name. Adding the following source code at the end of your `run()` method gives you all characters sorted by field `name`.

``` java
System.out.println("## Return all characters sorted by name");
Iterable<Character> allSorted = repository.findAll(new Sort(new Sort.Order(Sort.Direction.ASC, "name")));
allSorted.forEach(System.out::println);
```

Furthermore it's possible to use pagination combined with sorting. With the following code you get the first 5 characters sorted by `name`.

``` java
System.out.println("## Return the first 5 characters sorted by name");
Page<Character> first5Sorted = repository.findAll(new PageRequest(0, 5, new Sort(new Sort.Order(Sort.Direction.ASC, "name"))));
first5Sorted.forEach(System.out::println);
```

Your console output should include Arya Stark, Bran Stark, Brienne Tarth, Bronn and Catelyn Stark.

```
## Return the first 5 characters sorted by name
Character [id=characters/264848, name=Arya, surname=Stark, alive=true, age=11]
Character [id=characters/264851, name=Bran, surname=Stark, alive=true, age=10]
Character [id=characters/264871, name=Brienne, surname=Tarth, alive=true, age=32]
Character [id=characters/264863, name=Bronn, surname=null, alive=true, age=null]
Character [id=characters/264840, name=Catelyn, surname=Stark, alive=false, age=40]
```

# Query by example
```

Since version 1.12 Spring Data provides the interface `QueryByExampleExecutor` which is also supported by ArangoDB Spring Data. It allows execution of queries by `Example` instances.

Lets create a new `CommandLineRunner` for this

``` java
package com.arangodb.spring.demo.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import com.arangodb.spring.demo.entity.Character;
import com.arangodb.spring.demo.repository.CharacterRepository;

@ComponentScan("com.arangodb.spring.demo")
public class ByExampleRunner implements CommandLineRunner {

  @Autowired
  private CharacterRepository repository;

  @Override
  public void run(final String... args) throws Exception {
    System.out.println("# Query by example");
  }

}
```

and add it to our `DemoApplication`.

``` java
Object[] runner = new Object[] { CrudRunner.class, ByExampleRunner.class };
```

## Single entity

First we want to find Net Stark again. But this time we don't know the `id` of the persisted entity. We start with creating a `Character` with the same property values as the searched one. Then create an `Example` instance of it with `Example.of(T)` and search for it with `findOne(Example)` from our `CharacterRepository`.

``` java
final Character nedStark = new Character("Ned", "Stark", false, 41);

System.out.println(String.format("## Find character which exactly match %s", nedStark));
Character foundNedStark = repository.findOne(Example.of(nedStark));
System.out.println(String.format("Found %s", foundNedStark));
```

If we did everything right the console output should be:

```
# Query by example
## Find character which match Character [id=null, name=Ned, surname=Stark, alive=false, age=41]
Found Character [id=characters/264832, name=Ned, surname=Stark, alive=false, age=41]
```

## Multiple entities

Now we want to find more than one entity. The time I watched Game of Thrones I saw a lot of Starks dying, so lets take look who is already dead. For this we create a new instance of Character with `surname` 'Stark' and `alive` false. Because we only care of these two fields in our entity we have to ignore the other fields in our ExampleMatcher.

``` java
System.out.println("## Find all dead Starks");
Iterable<Character> allDeadStarks = repository
.findAll(Example.of(new Character(null, "Stark", false), ExampleMatcher.matchingAll()
.withMatcher("surname", match -> match.exact()).withIgnorePaths("name", "age")));
allDeadStarks.forEach(System.out::println);
```

After executing the application the console output should be:

```
## Find all dead Starks
Character [id=characters/264832, name=Ned, surname=Stark, alive=false, age=41]
Character [id=characters/264840, name=Catelyn, surname=Stark, alive=false, age=40]
Character [id=characters/264849, name=Robb, surname=Stark, alive=false, age=null]
```

In addition to search for specific values from our example entity, we can search for dynamically depending values. In the next example we search for a Stark with is 30 younger than Ned Stark. Instead of changing the age for Ned Stark in the before fetched entity, we use a transformer within the ExampleMatcher and subtract 30 from the age of Ned Stark.
``` java
System.out.println("## Find all Starks which are 30 years younger than Ned Stark");

Iterable<Character> allYoungerStarks = repository.findAll(
  Example.of(foundNedStark, ExampleMatcher.matchingAll().withMatcher("surname", match -> match.exact())
  .withIgnorePaths("id", "name", "alive").withTransformer("age", age -> ((int) age) - 30)));
allYoungerStarks.forEach(System.out::println);
```
Because we are using the entity `foundNedStark` - fetched from the database - we have to ignore the field `id` which isn't null in this case.

The console output should only include Arya Stark.

```
## Find all Starks which are 30 years younger than Ned Stark
Character [id=characters/264848, name=Arya, surname=Stark, alive=true, age=11]
```

Aside from searching for exact and transformed values we can - in case of type `String` also search for other expressions. In this last case we search for every character which it's `surname` ends with 'ark'. The console output should include every Stark.

``` java
System.out.println("## Find all character which surname ends with 'ark' (ignore case)");
Iterable<Character> ark = repository.findAll(Example.of(new Character(null, "ark", false),
  ExampleMatcher.matchingAll().withMatcher("surname", match -> match.endsWith()).withIgnoreCase()
  .withIgnorePaths("name", "alive", "age")));
ark.forEach(System.out::println);
```

# Derived queries

Spring Data ArangoDB supports Queries derived from methods names by splitting it into its semantic parts and converting
into AQL. The mechanism strips the prefixes `find..By`, `get..By`, `query..By`, `read..By`, `stream..By`, `count..By`,
`exists..By`, `delete..By`, `remove..By` from the method and parses the rest. The By acts as a separator to indicate the
start of the criteria for the query to be built. You can define conditions on entity properties and concatenate them
with `And` and `Or`.

The complete list of part types for derived queries can be found [here](https://github.com/arangodb/spring-data#derived-methods).

## Simple findBy

Lets start with an easy example. We want to find characters based on their `surname`.

The only thing we have to do is to add a method `fundBySurname(String)` to our `CharacterRepository` with a return type which allows the method to return multiple instances of `Character`. For more information of which return types are possible take a look [here](https://github.com/arangodb/spring-data#return-types).

``` java
public interface CharacterRepository extends ArangoRepository<Character> {

  Iterable<Character> findBySurname(String surname);

}
```

After we extended our repository we create a new `CommandLineRunner` and add it to our `DemoApplication`.

``` java
Object[] runner = new Object[] { CrudRunner.class, ByExampleRunner.class, DerivedMethodRunner.class };
```

In the `run()` method we call our new method `findBySurname(String)` and try to find all characters with the `surname` 'Lannister'.

``` java
package com.arangodb.spring.demo.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;

import com.arangodb.spring.demo.entity.Character;
import com.arangodb.spring.demo.repository.CharacterRepository;

@ComponentScan("com.arangodb.spring.demo")
public class DerivedQueriesRunner implements CommandLineRunner {

  @Autowired
  private CharacterRepository repository;

  @Override
  public void run(final String... args) throws Exception {
    System.out.println("# Derived queries");

    System.out.println("## Find all characters with surname 'Lannister'");
    Iterable<Character> lannisters = repository.findBySurname("Lannister");
    lannisters.forEach(System.out::println);
  }
}
```

After executing the demo application we should see the following lines within our console output.

```
# Derived queries
## Find all characters with surname 'Lannister'
Character [id=characters/264839, name=Jaime, surname=Lannister, alive=true, age=36]
Character [id=characters/264841, name=Cersei, surname=Lannister, alive=true, age=36]
Character [id=characters/264854, name=Tyrion, surname=Lannister, alive=true, age=32]
Character [id=characters/264856, name=Tywin, surname=Lannister, alive=false, age=null]
```

## More complexe findBy

Now we're creating some methods with more parts and look how they fit together. Lets also use some different return types. Again we simply add the methods in our `CharacterRepository`.

``` java
Collection<Character> findTop2DistinctBySurnameIgnoreCaseOrderByAgeDesc(String surname);

List<Character> findBySurnameEndsWithAndAgeBetweenAndNameInAllIgnoreCase(
  String suffix,
  int lowerBound,
  int upperBound,
  String[] nameList);
```

And the method calls in `DerivedMethodRunner`.

``` java
System.out.println("## Find top 2 Lannnisters ordered by age");
Collection<Character> top2 = repository.findTop2DistinctBySurnameIgnoreCaseOrderByAgeDesc("lannister");
top2.forEach(System.out::println);

System.out.println(
"## Find all characters which name is 'Bran' or 'Sansa' and it's surname ends with 'ark' and are between 10 and 16 years old");
List<Character> youngStarks = repository.findBySurnameEndsWithAndAgeBetweenAndNameInAllIgnoreCase("ark",
  10, 16, new String[] { "Bran", "Sansa" });
youngStarks.forEach(System.out::println);
```

The new methods produce the following console output.

```
## Find top 2 Lannnisters ordered by age
Character [id=characters/264839, name=Jaime, surname=Lannister, alive=true, age=36]
Character [id=characters/264841, name=Cersei, surname=Lannister, alive=true, age=36]
## Find all characters which name is 'Bran' or 'Sansa' and it's surname ends with 'ark' and are between 10 and 16 years old
Character [id=characters/264851, name=Bran, surname=Stark, alive=true, age=10]
Character [id=characters/264847, name=Sansa, surname=Stark, alive=true, age=13]
```

## Single entity result

With derived queries we can not only query for multiple entities. If we expect only a single entity as the result we can use the corresponding return type.

Because we have a unique hash index on the fields `name` and `surname` we can expect only a single entity when we query for both.

For this example we add the method `findByNameAndSurname(String, String)` in `CharacterRepository` which return type is `Character`.

``` java
Character findByNameAndSurname(String name, String surname);
```

When we add the method call in `DerivedMethodRunner` we should take care of `null` as a possible return value.

``` java
System.out.println("## Find a single character by name & surname");
Character tyrion = repository.findByNameAndSurname("Tyrion", "Lannister");
if (tyrion != null) {
  System.out.println(String.format("Found %s", tyrion));
});
```

At this point it is possible and recommended to use `Optional<T>` which was introduced with Java 8.

`CharacterRepository`:

``` java
Optional<Character> findByNameAndSurname(String name, String surname);
```

`DerivedMethodRunner`:

``` java
System.out.println("## Find a single character by name & surname");
Optional<Character> tyrion = repository.findByNameAndSurname("Tyrion", "Lannister");
tyrion.ifPresent(c -> {
  System.out.println(String.format("Found %s", c));
});
```

The console output should in both cases look:

```
## Find a single character by name & surname
Found Character [id=characters/264854, name=Tyrion, surname=Lannister, alive=true, age=32]
```

## countBy

Aside from `findBy` there are other prefixes supported - like `countBy`. Other than the before used `operations.collection(Character.class).count();` the `countBy` is able to include filter conditions.

With the following lines of code we are able to count only characters which are still alive.

`CharacterRepository`:

``` java
Integer countByAliveTrue();
```

`DerivedMethodRunner`:

``` java
System.out.println("## Count how many characters are still alive");
Integer alive = repository.countByAliveTrue();
System.out.println(String.format("There are %s characters still alive", alive));
```

## removeBy

The last example for derived queries is `removeBy`. In that we remove all characters except of which surname is 'Stark' and which are still alive.

``` java
System.out.println("## Remove all characters except of which surname is 'Stark' and which are still alive");
repository.removeBySurnameNotLikeOrAliveFalse("Stark");
repository.findAll().forEach(System.out::println);
```

We expect only Arya, Bran and Sansa to be left.

```
## Remove all characters except of which surname is 'Stark' and which are still alive
Character [id=characters/264851, name=Bran, surname=Stark, alive=true, age=10]
Character [id=characters/264847, name=Sansa, surname=Stark, alive=true, age=13]
Character [id=characters/264848, name=Arya, surname=Stark, alive=true, age=11]
```

## Relations

Because ArangoDB as a multi-model database provides graph as a key feature, Spring Data ArangoDB also supports a feature set for it.

With the `@Relations` annotation we can define relationships between our entities. To demonstrate this we use our before created entity `Character`.

First we have to add a field `Collection<Character> childs` annotated with `@Relations(edges = ChildOf.class, lazy = true)` to `Character`.

``` java
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

  ...
}
```

Then we have to created an entity for the edge we stated in `@Relations`. Other than a normal entity annotated with `@Document` this entity will be annotated with `@Edge`. This allows Spring Data ArangoDB to create a collection from type `EDGE` in the database. Just like `Character`, `ChildOf` will also get a field for its `id`. To connect two `Character` entities it also get a field from type `Character` annotated with `@From` and a field from type `Character` annotated with `@To`. `ChildOf` will be persisted in the database with the ids of these two `Character`.

``` java
package com.arangodb.spring.demo.entity;

import org.springframework.data.annotation.Id;
import com.arangodb.springframework.annotation.Edge;
import com.arangodb.springframework.annotation.From;
import com.arangodb.springframework.annotation.To;

@Edge
public class ChildOf {

  @Id
  private String id;

  @From
  private Character child;

  @To
  private Character parent;

  public ChildOf(final Character child, final Character parent) {
    super();
    this.child = child;
    this.parent = parent;
  }

  // setter & getter

  @Override
  public String toString() {
    return "ChildOf [id=" + id + ", child=" + child + ", parent=" + parent + "]";
  }

}
```

To save instances of `ChildOf` in the database we also create a repository for it the same way we created `CharacterRepository`.

``` java
package com.arangodb.spring.demo.repository;

import com.arangodb.spring.demo.entity.ChildOf;
import com.arangodb.springframework.repository.ArangoRepository;

public interface ChildOfRepository extends ArangoRepository<ChildOf> {

}
```

Now we implement another `CommandLineRunner` called `RelationsRunner` and add it to our `DemoApplication` like we did with all the runners before.

``` java
Object[] runner = new Object[] { CrudRunner.class, ByExampleRunner.class, DerivedMethodRunner.class, RelationsRunner.class };
```

In the new created `RelationsRunner` we inject `CharacterRepository` and `ChildOfRepository` and built our relations. First we have to save some characters because we removed the most of them within the last chapter of this demo. To do so we use the static `createCharacter()` method from our `CrudRunner`. After we have successfully persist our characters we want to save some relationships with our edge entity `ChildOf`. Because `ChildOf` require instances of `Character` with `id` field set from the database we first have to find them through our `CharacterRepository`. To ensure we find the correct `Character` we use the derived query method `findByNameAndSurename(String, String)` which give us one specific `Character`. Then we create instances of `ChildOf` and save them through `ChildOfRepository`.

``` java
package com.arangodb.spring.demo.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import com.arangodb.spring.demo.entity.Character;
import com.arangodb.spring.demo.entity.ChildOf;
import com.arangodb.spring.demo.repository.CharacterRepository;
import com.arangodb.spring.demo.repository.ChildOfRepository;

@ComponentScan("com.arangodb.spring.demo")
public class RelationsRunner implements CommandLineRunner {

  @Autowired
  private CharacterRepository characterRepo;
  @Autowired
  private ChildOfRepository childOfRepo;

  @Override
  public void run(final String... args) throws Exception {
    System.out.println("# Relations");
    characterRepo.save(CrudRunner.createCharacters());

    // first create some relations for the Starks and Lannisters
    characterRepo.findByNameAndSurname("Ned", "Stark").ifPresent(ned -> {
      characterRepo.findByNameAndSurname("Catelyn", "Stark").ifPresent(catelyn -> {
        characterRepo.findByNameAndSurname("Robb", "Stark").ifPresent(robb -> {
          childOfRepo.save(Arrays.asList(new ChildOf(robb, ned), new ChildOf(robb, catelyn)));
        });
        characterRepo.findByNameAndSurname("Sansa", "Stark").ifPresent(sansa -> {
          childOfRepo.save(Arrays.asList(new ChildOf(sansa, ned), new ChildOf(sansa, catelyn)));
        });
        characterRepo.findByNameAndSurname("Arya", "Stark").ifPresent(arya -> {
          childOfRepo.save(Arrays.asList(new ChildOf(arya, ned), new ChildOf(arya, catelyn)));
        });
        characterRepo.findByNameAndSurname("Bran", "Stark").ifPresent(bran -> {
          childOfRepo.save(Arrays.asList(new ChildOf(bran, ned), new ChildOf(bran, catelyn)));
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
            childOfRepo.save(Arrays.asList(new ChildOf(joffrey, jaime), new ChildOf(joffrey, cersei)));
          });
        });
      });
      characterRepo.findByNameAndSurname("Tyrion", "Lannister")
          .ifPresent(tyrion -> childOfRepo.save(new ChildOf(tyrion, tywin)));
    });

  }

}
```

## Read relations within an entity

After we add `@Relations(edges = ChildOf.class, lazy = true) Collection<Character> childs;` in `Character` we can now load all children of a character when we fetch the character from the database. Lets again use the method `findByNameAndSurname(String, String)` to find one specific character.

Add the following lines of code to the `run()` method of `RelationsRunner`.

``` java
characterRepo.findByNameAndSurname("Ned", "Stark").ifPresent(nedStark -> {
  System.out.println(String.format("## These are the children of %s:", nedStark));
  nedStark.getChilds().forEach(System.out::println);
});
```

After executing the demo again we can see the following console output.

```
## These are the children of Character [id=characters/273652, name=Ned, surname=Stark, alive=false, age=41]:
Character [id=characters/273604, name=Arya, surname=Stark, alive=true, age=11]
Character [id=characters/273607, name=Bran, surname=Stark, alive=true, age=10]
Character [id=characters/273664, name=Robb, surname=Stark, alive=false, age=null]
Character [id=characters/273661, name=Jon, surname=Snow, alive=true, age=16]
Character [id=characters/273603, name=Sansa, surname=Stark, alive=true, age=13]
```

## findBy including relations

The field `childs` is not persisted in the character entity itself, it is represent through the edge `ChildOf`. Nevertheless we can write a derived method which includes properties of all connected `Character`.

With the following two methods - added in `CharacterRepository` - we can query for `Character` which have a child with a given `name` and `Character` which have a child in an `age` between two given integer.

``` java
Iterable<Character> findByChildsName(String name);

Iterable<Character> findByChildsAgeBetween(int lowerBound, int upperBound);
```

Now we add the method calls in `RelationsRunnter` and search for all parents of 'Sansa' and all parents which have a child between 16 and 20 years.

``` java
System.out.println("## These are the parents of 'Sansa'");
Iterable<Character> parentsOfSansa = characterRepo.findByChildsName("Sansa");
parentsOfSansa.forEach(System.out::println);

System.out.println("## These parents have a child which is between 16 and 20 years old");
Iterable<Character> childsBetween16a20 = characterRepo.findByChildsAgeBetween(16, 20);
childsBetween16a20.forEach(System.out::println);
```

The console output shows us that Ned and Catelyn are the parents of Sansa and that Ned, Jamie and Cersei have at least one child in the age between 16 and 20 years.

```
## These are the parents of 'Sansa'
Character [id=characters/273652, name=Ned, surname=Stark, alive=false, age=41]
Character [id=characters/273655, name=Catelyn, surname=Stark, alive=false, age=40]
## These parents have a child which is between 16 and 20 years old
Character [id=characters/273652, name=Ned, surname=Stark, alive=false, age=41]
Character [id=characters/273654, name=Jaime, surname=Lannister, alive=true, age=36]
Character [id=characters/273656, name=Cersei, surname=Lannister, alive=true, age=36]
```

# Query methods

When it comes to more complexe use cases where a derived method would become way to long and unreadable queries - using [ArangoDB Query Language (AQL)](https://docs.arangodb.com/3.2/AQL/) - can be supplied with the `@Query` annotation on methods in our repositories.

AQL supports the usage of [bind parameters](https://docs.arangodb.com/current/AQL/Fundamentals/BindParameters.html), thus allowing to separate the query text from literal values used in the query. There are three ways of passing bind parameters to the query in the `@Query` annotation.

## Number matching

Using number matching, arguments will be substituted into the query in the order they are passed to the query method.

Lets show this by a very simple query. We want to query all characters which are older than a given value and sort them in descending order. Like every time before we add a new method to `CharacterRepository`. This time the method name does not matter, the functionality comes with the query in the `@Query` annotation. The method parameter `int value` will be passed as the bind parameter `@0` in the query. A second parameter would be passed as `@1` and so on.

``` java
@Query("FOR c IN characters FILTER c.age > @0 SORT c.age DESC RETURN c")
Iterable<Character> getOlderThan(int value);
```

As you can see we used the collection name `"characters"` and not `"character"` in our query. Normally a collection would be named like the corresponding entity class. But as you probably remember we used `@Document("characters")` in `Character` which set the collection name to `"characters"`.

Now we create a new `CommandLineRunner` add it to our `DemoApplication`.

``` java
package com.arangodb.spring.demo.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import com.arangodb.spring.demo.entity.Character;
import com.arangodb.spring.demo.entity.ChildOf;
import com.arangodb.spring.demo.repository.CharacterRepository;

public class AQLRunner implements CommandLineRunner {

  @Autowired
  private CharacterRepository repository;

  @Override
  public void run(final String... args) throws Exception {
    System.out.println("# AQL queries");

    System.out.println("## Find all characters which are older than 21 (sort descending)");
    final Iterable<Character> older = repository.getOlderThan(21);
    older.forEach(System.out::println);

  }

}
```

`DemoApplication`:

``` java
Object[] runner = new Object[] { CrudRunner.class, ByExampleRunner.class, DerivedQueryRunner.class, RelationsRunner.class, AQLRunner.class };
```

We called our new method with a value of `21`. Many of our characters have a `age` of value `null`, these also doesn't fit in our filter condition. Accordingly our console output should look:

```
# AQL queries
## Find all characters which are older than 21 (sort descending)
Character [id=characters/273672, name=Davos, surname=Seaworth, alive=true, age=49]
Character [id=characters/273652, name=Ned, surname=Stark, alive=false, age=41]
Character [id=characters/273655, name=Catelyn, surname=Stark, alive=false, age=40]
Character [id=characters/273654, name=Jaime, surname=Lannister, alive=true, age=36]
Character [id=characters/273656, name=Cersei, surname=Lannister, alive=true, age=36]
Character [id=characters/273669, name=Tyrion, surname=Lannister, alive=true, age=32]
Character [id=characters/273686, name=Brienne, surname=Tarth, alive=true, age=32]
```

## Param annotation

Another way to pass bind parameter to our query is the `@Param` annotation. With the `@Param` annotation, the argument will be placed in the query at the place corresponding to the value passed to the `@Param` annotation.

To demonstrate this we add another method to `CharacterRepository`:

``` java
@Query("FOR c IN characters FILTER c.surname == @surname SORT c.age ASC RETURN c")
Iterable<Character> getWithSurname(@Param("surname") String value);
```

Here we named our bind parameter `surname` and annotated our method parameter `value` with `@Param("surname")`. Only the value in `@Param` annotation has to match with our bind parameter, the method parameter name does not matter.

The usage is the same as with number matching query methods. Add the following lines to `AQLRunner`.

``` java
System.out.println("## Find all characters with surname 'Lannister' (sort by age ascending)");
Iterable<Character> lannisters = repository.getWithSurname("Lannister");
lannisters.forEach(System.out::println);
```

The console output should give you all characters with `surname` Lannister.

```
## Find all characters with surname 'Lannister' (sort by age ascending)
Character [id=characters/273671, name=Tywin, surname=Lannister, alive=false, age=null]
Character [id=characters/273669, name=Tyrion, surname=Lannister, alive=true, age=32]
Character [id=characters/273654, name=Jaime, surname=Lannister, alive=true, age=36]
Character [id=characters/273656, name=Cersei, surname=Lannister, alive=true, age=36]
```

## BindVars annotation

In addition to number matching and annotation `@Param` we can use a method parameter of type `Map<String, Object>` annotated with `@BindVars` as our bind barameters. We can then fill the map with any parameter used in the query.

Add to `CharacterRepository`:
``` java
@Query("FOR c IN @@col FILTER c.surname == @surname AND c.age > @age RETURN c")
Iterable<Character> getWithSurnameOlderThan(@Param("age") int value, @BindVars Map<String, Object> bindvars);
```

In this query we used three bind parameter `@@col`, `@surname` and `@age`. As you probably recognize one of our bind parameter is written with two `@`. This is a special type of bind parameter exists for injecting collection names. This type of bind parameter has a name prefixed with an additional `@` symbol.

Furthermore we can see that we used `@Param` for our bind parameter `@age` but not for `@@col` and `@surname`. These bind parameter have to be passed through the map annotated with `@BindVars`. It is also possible to use both annotations within one query method.

The method call looks as expected. We pass an integer for the bind parameter `age` and a map with the keys `surname` and `@col` to our new method.

``` java
System.out.println("## Find all characters with surname 'Lanister' which are older than 35");
Map<String, Object> bindvars = new HashMap<>();
bindvars.put("surname", "Lannister");
bindvars.put("@col", Character.class);
Iterable<Character> oldLannisters = repository.getWithSurnameOlderThan(35, bindvars);
oldLannisters.forEach(System.out::println);
```

One additional special handling for collection bind parameter is that we do not have to pass the collection name as a `String` to our method. We can pass the type - `Character.class` - to our method. Spring Data ArangoDB will then determine the collection name. This is very convenient if you used an alternative collection name within the annotations `@Document` or `@Edge`.

The console output should be:

```
## Find all characters with surname 'Lanister' which are older than 35
Character [id=characters/273654, name=Jaime, surname=Lannister, alive=true, age=36]
Character [id=characters/273656, name=Cersei, surname=Lannister, alive=true, age=36]
```

## QueryOptions annotation

Sometimes you want to be able to configure the query execution on a technical level. For this Spring Data ArangoDB provides the `@QueryOptions` annotation. With this annotation you are able to set something like a batch size to control the number of results to be transferred from the database server in one roundtrip and some other things.

For our example we want to return the number of found results. To achieve that we have to change the return type - of our before created method `getWithSurnameOlderThan(int, Map)` - from `Iterable<Charactre>` to `ArangoCursor<Character>`. `ArangoCursor` provides a method `getCount()` which gives us the number of found results. But this value is only returned from the database when we set the flag `count` in our query options to `true`, so we also have to add the `QueryOptions` annotation to our method with `count = true`.

``` java
@Query("FOR c IN @@col FILTER c.surname == @surname AND c.age > @age RETURN c")
@QueryOptions(count = true)
ArangoCursor<Character> getWithSurnameOlderThan(@Param("age") int value, @BindVars Map<String, Object> bindvars);
```

Now, when we change the type of our local variable `oldLannisters` in `AQLRunner` to `ArangoCursor` we can get the count value from it.

``` java
ArangoCursor<Character> oldLannisters = repository.getWithSurnameOlderThan(35, bindvars);
System.out.println(String.format("Found %s documents", oldLannisters.getCount()));
```

Our new console output should then look:

```
## Find all characters with surname 'Lannister' which are older than 35
Found 2 documents
Character [id=characters/273654, name=Jaime, surname=Lannister, alive=true, age=36]
Character [id=characters/273656, name=Cersei, surname=Lannister, alive=true, age=36]
```

## Graph traversal

Lets finish the query method topic of our demo with a [graph traversal](https://docs.arangodb.com/current/AQL/Graphs/Traversals.html) written in AQL where our edge `ChildOf` is involved.

The following query searches for every `Character` connected - through `ChildOf` - with the character the passed `id` belongs to. This time we have specify the edge collection within the query which we pass as a bind parameter with the `@Param` annotation.

`CharacterRepository`:

``` java
@Query("FOR v IN 1..2 INBOUND @id @@edgeCol SORT v.age DESC RETURN DISTINCT v")
Set<Character> getAllChildsAndGrandchilds(@Param("id") String id, @Param("@edgeCol") Class<?> edgeCollection);
```

Like we did before with `Character.class` in our map we use the type of `ChildOf` as parameter value. Because we want to find all childs and grantchilds of Tywin Lannister we first have to find him to get his id which we can then pass to our query method.

`AQLRunner`:

``` java
System.out.println("## Find all childs and grantchilds of 'Tywin Lannister' (sort by age descending)");
repository.findByNameAndSurname("Tywin", "Lannister").ifPresent(tywin -> {
Set<Character> childs = repository.getAllChildsAndGrandchilds(tywin.getId(), ChildOf.class);
  childs.forEach(System.out::println);
});
```

After executing the demo again we can see the following console output.

```
## Find all childs and grantchilds of 'Tywin Lannister' (sort by age descending)
Character [id=characters/280697, name=Jaime, surname=Lannister, alive=true, age=36]
Character [id=characters/280699, name=Cersei, surname=Lannister, alive=true, age=36]
Character [id=characters/280712, name=Tyrion, surname=Lannister, alive=true, age=32]
Character [id=characters/280710, name=Joffrey, surname=Baratheon, alive=false, age=19]
```

# Geospacial queries

Geospatial queries are a subsection of derived queries. To use a geospatial query on a collection, a geo index must exist on that collection. A geo index can be created on a field which is a two element array, corresponding to latitude and longitude coordinates.

As a subsection of derived queries, geospatial queries support all the same return types, but also support the three return types `GeoPage`, `GeoResult` and `Georesults`. These types must be used in order to get the distance of each document as generated by the query.

## Data modeling

To demonstrate geospatial queries we create a new entity class `Location` with a field `location` of type `double[]`. We also have to create a geo index on this field. We can do so by annotatin the field with `@GeoIndexed`. As you probably remeber we already used an index in our `Character` class but we annotated the type and not the affected fields. Spring Data ArangoDB offers two ways of defining an index. With `@<IndexType>Indexed` annotations indexes for single fields can be defined. If the index should include multiple fields the `@<IndexType>Index` annotations can be used on the type instead. Take a look [here](https://github.com/arangodb/spring-data#index-and-indexed-annotations) for more informations.

Create a new class `Location`:

``` java
package com.arangodb.spring.demo.entity;

import java.util.Arrays;

import org.springframework.data.annotation.Id;
import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.GeoIndexed;

@Document("locations")
public class Location {

  @Id
  private String id;
  private final String name;
  @GeoIndexed
  private final double[] location;

  public Location(final String name, final double[] location) {
    super();
    this.name = name;
    this.location = location;
  }

  // getter & setter

  @Override
  public String toString() {
    return "Location [id=" + id + ", name=" + name + ", location=" + Arrays.toString(location) + "]";
  }

}
```

and the corresponding repository `LocationRepository`:

``` java
package com.arangodb.spring.demo.repository;

import com.arangodb.spring.demo.entity.Location;
import com.arangodb.springframework.repository.ArangoRepository;

public interface LocationRepository extends ArangoRepository<Location> {

}
```

After that we create a new `CommandLineRunner`, add it to our `DemoApplication` and perform some insert operations with some popular locations from Game of Thrones with the coordinates of their real counterparts.

``` java
package com.arangodb.spring.demo.runner;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import com.arangodb.spring.demo.entity.Location;
import com.arangodb.spring.demo.repository.LocationRepository;

public class GeospacialRunner implements CommandLineRunner {

  @Autowired
  private LocationRepository repository;

  @Override
  public void run(final String... args) throws Exception {
    System.out.println("# Geospacial");

    repository.save(Arrays.asList(new Location("Dragonstone", new double[] { 55.167801, -6.815096 }),
      new Location("King's Landing", new double[] { 42.639752, 18.110189 }),
      new Location("The Red Keep", new double[] { 35.896447, 14.446442 }),
      new Location("Yunkai", new double[] { 31.046642, -7.129532 }),
      new Location("Astapor", new double[] { 31.50974, -9.774249 }),
      new Location("Winterfell", new double[] { 54.368321, -5.581312 }),
      new Location("Vaes Dothrak", new double[] { 54.16776, -6.096125 }),
      new Location("Beyond the wall", new double[] { 64.265473, -21.094093 })));

  }

}
```

`DemoApplication`:
``` java
Object[] runner = new Object[] { CrudRunner.class, ByExampleRunner.class, DerivedQueryRunner.class,
  RelationsRunner.class, AQLRunner.class, GeospacialRunner.class };
```

There are two kinds of geospatial query, Near and Within. , while within both sorts and filters documents, returning those within the given distance range or shape.

## Near

Near sorts entities by distance from the given point. The result can be restricted with paging.

``` java
GeoPage<Location> findByLocationNear(Point location, Pageable pageable);
```

``` java
System.out.println("## Find the first 5 locations near 'Winterfell'");
GeoPage<Location> first5 = repository.findByLocationNear(new Point(-5.581312, 54.368321),
  new PageRequest(0, 5));
first5.forEach(System.out::println);

System.out.println("## Find the next 5 locations near 'Winterfell' (only 3 locations left)");
GeoPage<Location> next5 = repository.findByLocationNear(new Point(-5.581312, 54.368321),
  new PageRequest(1, 5));
next5.forEach(System.out::println);
```

## Within

TODO