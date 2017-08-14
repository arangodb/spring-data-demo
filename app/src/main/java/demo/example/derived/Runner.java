package demo.example.derived;

import demo.AbstractRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

import java.util.Collection;
import java.util.HashSet;

@ComponentScan("demo")
public class Runner extends AbstractRunner {

    @Autowired
    private CharacterRepository repository;

    @Override
    public void run(String... args) throws Exception {

        repository.deleteAll();
        PRINT_STREAM.println("Cleared 'character' collection");
        PRINT_STREAM.println(BREAK);

        Character jon = new Character("Jon", "Snow", 25, true);
        Character bran = new Character("Bran", "Stark", 17, true);
        Character sansa = new Character("Sansa", "Stark", 21, true);
        Character rob  = new Character("Rob", "Stark", 27, false);
        Character rickon = new Character("Rickon", "Stark", 15, false);

        Collection<Character> characters = new HashSet<>();
        characters.add(jon);
        characters.add(bran);
        characters.add(sansa);
        characters.add(rob);
        characters.add(rickon);

        repository.save(characters);
        PRINT_STREAM.println("Add to character collection: ");
        PRINT_STREAM.println(BREAK);
        for (Character c: characters) { PRINT_STREAM.println(c); }
        PRINT_STREAM.println();

        PRINT_STREAM.println("Run countByAliveTrue, expecting 3");
        PRINT_STREAM.println(BREAK);
        Integer countAlive = repository.countByAliveTrue();
        PRINT_STREAM.println("Result: " + countAlive);
        PRINT_STREAM.println();

        PRINT_STREAM.println("Run findTop2DistinctBySurnameIgnoreCaseOrderByAgeDesc, expecting Rob then Sansa");
        PRINT_STREAM.println(BREAK);
        Collection<Character> result = repository.findTop2DistinctBySurnameIgnoreCaseOrderByAgeDesc("stark");
        for (Character c: result) { PRINT_STREAM.println(c); }
        PRINT_STREAM.println();

        PRINT_STREAM.println("Run findBySurnameEndsWithAndAgeBetweenAndNameIn, expecting Bran and Sansa");
        PRINT_STREAM.println(BREAK);
        result = repository.findBySurnameEndsWithAndAgeBetweenAndNameInAllIgnoreCase("ark", 16, 30,
                new String[] {"Bran", "Sansa"});
        for (Character c: result) { PRINT_STREAM.println(c); }
        PRINT_STREAM.println();

        PRINT_STREAM.println("Run removeBySurnameNotLikeOrAliveFalse then findAll, expecting only Bran and Sansa");
        PRINT_STREAM.println(BREAK);
        repository.removeBySurnameNotLikeOrAliveFalse("Stark");
        Iterable<Character> remaining = repository.findAll();
        for (Character c: remaining) { PRINT_STREAM.println(c); }
        PRINT_STREAM.println();

    }
}
