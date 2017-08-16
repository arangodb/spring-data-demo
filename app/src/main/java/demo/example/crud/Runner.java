package demo.example.crud;

import demo.AbstractRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

@ComponentScan("demo")
public class Runner extends AbstractRunner {

    @Autowired
    UserRepository repository;

    @Override
    public void run(String... args) throws Exception {

        repository.deleteAll();
        LOGGER.log("deleteAll() - cleared 'users' collection");
        LOGGER.log(BREAK);

        User admin = new User("admin", 1);
        User root = new User("root", 0);
        User user = new User("user", 2);

        Collection<User> users = Arrays.asList(new User[] {admin, root, user});

        LOGGER.log("Initial users:");
        LOGGER.log(BREAK);
        LOGGER.log(users);
        LOGGER.log();

        repository.save(admin);
        LOGGER.log("User 'admin' after saving:");
        LOGGER.log(BREAK);
        LOGGER.log(admin);
        LOGGER.log();

        repository.save(users);

        LOGGER.log("All 3 users after saving (notice that 'admin' id did not change and new user was not created):");
        LOGGER.log(BREAK);
        LOGGER.log(users);
        LOGGER.log();

        LOGGER.log("findOne(\"" + admin.getId() + "\") - expecting 'admin' user:");
        LOGGER.log(BREAK);
        User present = repository.findOne(admin.getId());
        LOGGER.log(present);
        LOGGER.log();

        LOGGER.log("exists(\"" + admin.getId() + "\") - expecting 'true' for 'admin':");
        LOGGER.log(BREAK);
        boolean exists = repository.exists(admin.getId());
        LOGGER.log(exists);
        LOGGER.log();

        LOGGER.log("findOne(\"-\") - expecting 'null':");
        LOGGER.log(BREAK);
        User absent = repository.findOne("-");
        LOGGER.log(absent);
        LOGGER.log();

        LOGGER.log("exists(\"-\") - expecting 'false':");
        LOGGER.log(BREAK);
        exists = repository.exists("-");
        LOGGER.log(exists);
        LOGGER.log();

        LOGGER.log("findAll() - expecting all 3 users:");
        LOGGER.log(BREAK);
        LOGGER.log(repository.findAll());
        LOGGER.log();

        Collection<String> ids = new LinkedList<>();
        ids.add(root.getId());
        ids.add(user.getId());

        LOGGER.log("findAll([\"" + root.getId() + "\", \"" + user.getId() + "\"]) - expecting 'root' and 'user':");
        LOGGER.log(BREAK);
        LOGGER.log(repository.findAll(ids));
        LOGGER.log();

        LOGGER.log("count() - expecting 3:");
        LOGGER.log(BREAK);
        long count = repository.count();
        LOGGER.log(count);
        LOGGER.log();

        LOGGER.log("delete(admin) - delete(String id) is supported as well:");
        LOGGER.log(BREAK);
        repository.delete(admin);

        LOGGER.log("deleteAll([user]):");
        LOGGER.log(BREAK);
        repository.delete(Arrays.asList(new User[] {user}));

        LOGGER.log("check the database - only 'root' is left in 'users' collection with renamed attribute 'lvl'");
    }
}
