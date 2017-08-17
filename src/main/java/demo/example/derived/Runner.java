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
        LOGGER.log("Cleared 'character' collection");
        LOGGER.log(BREAK);

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
        LOGGER.log("Add to character collection: ");
        LOGGER.log(BREAK);
        for (Character c: characters) { LOGGER.log(c); }
        LOGGER.log();

        LOGGER.log("Run countByAliveTrue, expecting 3");
        LOGGER.log(BREAK);
        Integer countAlive = repository.countByAliveTrue();
        LOGGER.log("Result: " + countAlive);
        LOGGER.log();

        LOGGER.log("Run findTop2DistinctBySurnameIgnoreCaseOrderByAgeDesc, expecting Rob then Sansa");
        LOGGER.log(BREAK);
        Collection<Character> result = repository.findTop2DistinctBySurnameIgnoreCaseOrderByAgeDesc("stark");
        for (Character c: result) { LOGGER.log(c); }
        LOGGER.log();

        LOGGER.log("Run findBySurnameEndsWithAndAgeBetweenAndNameIn, expecting Bran and Sansa");
        LOGGER.log(BREAK);
        result = repository.findBySurnameEndsWithAndAgeBetweenAndNameInAllIgnoreCase("ark", 16, 30,
                new String[] {"Bran", "Sansa"});
        for (Character c: result) { LOGGER.log(c); }
        LOGGER.log();

        LOGGER.log("Run removeBySurnameNotLikeOrAliveFalse then findAll, expecting only Bran and Sansa");
        LOGGER.log(BREAK);
        repository.removeBySurnameNotLikeOrAliveFalse("Stark");
        Iterable<Character> remaining = repository.findAll();
        for (Character c: remaining) { LOGGER.log(c); }
        LOGGER.log();

    }
}
