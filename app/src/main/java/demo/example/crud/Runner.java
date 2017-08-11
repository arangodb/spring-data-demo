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
        PRINT_STREAM.println("deleteAll() - cleared 'users' collection");
        PRINT_STREAM.println(BREAK);

        User admin = new User("admin", 1);
        User root = new User("root", 0);
        User user = new User("user", 2);

        Collection<User> users = Arrays.asList(new User[] {admin, root, user});

        PRINT_STREAM.println("Initial users:");
        PRINT_STREAM.println(BREAK);
        PRINT_STREAM.println(users);
        PRINT_STREAM.println();

        repository.save(admin);
        PRINT_STREAM.println("User 'admin' after saving:");
        PRINT_STREAM.println(BREAK);
        PRINT_STREAM.println(admin);
        PRINT_STREAM.println();

        repository.save(users);

        PRINT_STREAM.println("All 3 users after saving (notice that 'admin' id did not change and new user was not created):");
        PRINT_STREAM.println(BREAK);
        for (User u : users) { PRINT_STREAM.println(u); }
        PRINT_STREAM.println();

        PRINT_STREAM.println("findOne(String id) - expecting 'admin' user:");
        PRINT_STREAM.println(BREAK);
        User present = repository.findOne(admin.getId());
        PRINT_STREAM.println(present);
        PRINT_STREAM.println();

        PRINT_STREAM.println("exists(String id) - expecting 'true' for 'admin':");
        PRINT_STREAM.println(BREAK);
        boolean exists = repository.exists(admin.getId());
        PRINT_STREAM.println(exists);
        PRINT_STREAM.println();

        PRINT_STREAM.println("findOne(String id) - expecting 'null' because no user with id '-' exists:");
        PRINT_STREAM.println(BREAK);
        User absent = repository.findOne("-");
        PRINT_STREAM.println(absent);
        PRINT_STREAM.println();

        PRINT_STREAM.println("exists(String id) - expecting 'false' for id='-':");
        PRINT_STREAM.println(BREAK);
        exists = repository.exists("-");
        PRINT_STREAM.println(exists);
        PRINT_STREAM.println();

        PRINT_STREAM.println("findAll() - expecting all 3 users:");
        PRINT_STREAM.println(BREAK);
        PRINT_STREAM.println(repository.findAll());
        PRINT_STREAM.println();

        Collection<String> ids = new LinkedList<>();
        ids.add(root.getId());
        ids.add(user.getId());

        PRINT_STREAM.println("findAll(Iterable<String> ids) - expecting 'root' and 'user':");
        PRINT_STREAM.println(BREAK);
        PRINT_STREAM.println(repository.findAll(ids));
        PRINT_STREAM.println();

        PRINT_STREAM.println("count() - expecting 3:");
        PRINT_STREAM.println(BREAK);
        long count = repository.count();
        PRINT_STREAM.println(count);
        PRINT_STREAM.println();

        PRINT_STREAM.println("delete(User user) - deleting 'admin' (delete(String id) is supported as well):");
        PRINT_STREAM.println(BREAK);
        repository.delete(admin);

        PRINT_STREAM.println("deleteAll(Iterable<User> users) - deleting ['user']:");
        PRINT_STREAM.println(BREAK);
        repository.delete(Arrays.asList(new User[] {user}));

        PRINT_STREAM.println("check the database - only 'root' is left in 'users' collection with renamed attribute 'lvl'");
    }
}
