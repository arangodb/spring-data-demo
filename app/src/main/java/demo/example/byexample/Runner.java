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
        PRINT_STREAM.println("Cleared 'people' collection");
        PRINT_STREAM.println(BREAK);

        Person jon = new Person("Jon", "Snow", 25);
        Person bran = new Person("Bran", "Stark", 17);
        Person sansa = new Person("Sansa", "Stark", 21);

        //Can use any collection
        Collection<Person> people = new HashSet<>();
        people.add(jon);
        people.add(bran);
        people.add(sansa);

        repository.save(people);

        PRINT_STREAM.println("Add to people collection: ");
        PRINT_STREAM.println(BREAK);
        for (Person p: people) { PRINT_STREAM.println(p); }
        PRINT_STREAM.println();

        Example jonExample = Example.of(jon);
        PRINT_STREAM.println("Find by Jon Snow example");
        PRINT_STREAM.println(BREAK);
        Person retrieved = repository.findOne(jonExample);
        PRINT_STREAM.println(retrieved);
        PRINT_STREAM.println();

        Example starkExample = Example.of(new Person(null, "stark", 0),
                        ExampleMatcher.matchingAny()
                        .withMatcher("surname", match -> match.exact())
                        .withIgnoreCase("surname")
                        .withIgnoreNullValues()
        );
        PRINT_STREAM.println("Find by surname 'stark' example with case insensitivity, ignoring null values");
        PRINT_STREAM.println(BREAK);
        Iterable<Person> starkList = repository.findAll(starkExample);
        for (Person p: starkList) { PRINT_STREAM.println(p); }
        PRINT_STREAM.println();

        Example ageTransformExample = Example.of(new Person("Bran", "ark", 17),
                        ExampleMatcher.matchingAll()
                                .withMatcher("surname", match -> match.endsWith())
                                .withTransformer("age", age -> ((long) age) + 4)
                                .withIgnorePaths("name")
        );
        PRINT_STREAM.println("Find by surname ending 'ark', with age 17 transformed +4, ignoring first name");
        PRINT_STREAM.println(BREAK);
        starkList = repository.findAll(ageTransformExample);
        for (Person p: starkList) { PRINT_STREAM.println(p); }
    }
}
