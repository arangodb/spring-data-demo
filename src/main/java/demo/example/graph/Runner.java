package demo.example.graph;

import com.arangodb.springframework.core.ArangoOperations;
import demo.AbstractRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by markmccormick on 24/08/2017.
 */
@ComponentScan(basePackages = {"demo"})
public class Runner extends AbstractRunner {

    @Autowired
    private OwnerRepository repository;

    //Instance of template to save relations
    @Autowired
    private ArangoOperations operations;

    @Override
    public void run(String... args) throws Exception {

        Collection<Owner> result;

        repository.deleteAll();
        LOGGER.log("deleteAll() - cleared 'product' collection");
        LOGGER.log(BREAK);
        LOGGER.log();

        //Creating owners
        Collection<Owner> owners = new LinkedList<>();
        Owner matt = new Owner("Matt", 44);
        Owner tom = new Owner("Tom", 23);
        Owner sarah = new Owner("Sarah", 29);
        owners.add(matt); owners.add(tom); owners.add(sarah);

        Collection<House> houses = new LinkedList<>();
        House big = new House("somewhere", 100000);
        House small = new House("out", 1000);
        House mansion = new House("there", 500000);
        houses.add(big); houses.add(small); houses.add(mansion);

        Collection<Room> rooms = new LinkedList<>();
        Room kitchen = new Room("kitchen");
        Room bedroom = new Room("bedroom");
        Room sauna = new Room("sauna");
        rooms.add(kitchen); rooms.add(bedroom); rooms.add(sauna);

        LOGGER.log("Saving owners Matt, Tom and Sarah");
        LOGGER.log();

        // save owners to database
        repository.save(owners);

        // insert houses and rooms, going to different collection than the repository
        // so template is used
        operations.insert(houses, House.class);
        operations.insert(rooms, Room.class);

        // save owns relations using template
        operations.insert(new Owns(matt, mansion));
        operations.insert(new Owns(tom, small));
        operations.insert(new Owns(sarah, big));

        // save contains relations using template
        operations.insert(new Contains(mansion, sauna));
        operations.insert(new Contains(mansion, kitchen));
        operations.insert(new Contains(mansion, bedroom));
        operations.insert(new Contains(big, bedroom));
        operations.insert(new Contains(big, kitchen));
        operations.insert(new Contains(small, bedroom));

        result = repository.findByHouseValueGreaterThan(80000);
        LOGGER.log("Calling findByHouseValueGreaterThan with 80000, expecting Matt and Sarah: ");
        LOGGER.log(BREAK);
        for (Owner o: result) { LOGGER.log(o); }
        LOGGER.log();

        result = repository.findByHouseRoomsName("bedroom");
        LOGGER.log("Calling findByHouseContainsName with 'bedroom', expecting all 3 owners: ");
        LOGGER.log(BREAK);
        for (Owner o: result) { LOGGER.log(o); }
        LOGGER.log();
    }

}
