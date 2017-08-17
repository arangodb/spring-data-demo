package demo.example.byexample;

import demo.AbstractRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import java.util.Collection;
import java.util.HashSet;

@ComponentScan("demo")
public class Runner extends AbstractRunner {

    @Autowired
    private PersonRepository repository;

    @Override
    public void run(String... strings) throws Exception {

        repository.deleteAll();
        LOGGER.log("Cleared 'people' collection");
        LOGGER.log(BREAK);

        Person jon = new Person("Jon", "Snow", 25);
        Person bran = new Person("Bran", "Stark", 17);
        Person sansa = new Person("Sansa", "Stark", 21);

        //Can use any collection
        Collection<Person> people = new HashSet<>();
        people.add(jon);
        people.add(bran);
        people.add(sansa);

        repository.save(people);

        LOGGER.log("Add to people collection: ");
        LOGGER.log(BREAK);
        for (Person p: people) { LOGGER.log(p); }
        LOGGER.log();

        Example jonExample = Example.of(jon);
        LOGGER.log("Find by Jon Snow example");
        LOGGER.log(BREAK);
        Person retrieved = repository.findOne(jonExample);
        LOGGER.log(retrieved);
        LOGGER.log();

        Example starkExample = Example.of(new Person(null, "stark", 0),
                        ExampleMatcher.matchingAny()
                        .withMatcher("surname", match -> match.exact())
                        .withIgnoreCase("surname")
                        .withIgnoreNullValues()
        );
        LOGGER.log("Find by surname 'stark' example with case insensitivity, ignoring null values");
        LOGGER.log(BREAK);
        Iterable<Person> starkList = repository.findAll(starkExample);
        for (Person p: starkList) { LOGGER.log(p); }
        LOGGER.log();

        Example ageTransformExample = Example.of(new Person("Bran", "ark", 17),
                        ExampleMatcher.matchingAll()
                                .withMatcher("surname", match -> match.endsWith())
                                .withTransformer("age", age -> ((long) age) + 4)
                                .withIgnorePaths("name")
        );
        LOGGER.log("Find by surname ending 'ark', with age 17 transformed +4, ignoring first name");
        LOGGER.log(BREAK);
        starkList = repository.findAll(ageTransformExample);
        for (Person p: starkList) { LOGGER.log(p); }
    }
}
