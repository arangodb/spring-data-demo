package demo.example.crud;

import com.arangodb.springframework.core.repository.ArangoRepository;
import demo.AbstractRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;

import java.util.Collection;
import java.util.LinkedList;

@ComponentScan("demo")
public class Runner extends AbstractRunner implements CommandLineRunner {

    @Autowired
    UserRepository repository;

    @Override
    public void run(String... args) throws Exception {

        repository.deleteAll();
        PRINT_STREAM.println("Cleared 'users' collection");
        PRINT_STREAM.println(BREAK);

        User admin = new User("admin");
        User root = new User("root");
        User user = new User("user");

        Collection<User> users = new LinkedList<>();
        users.add(admin);
        users.add(root);
        users.add(user);

        PRINT_STREAM.println("Initial users:");
        PRINT_STREAM.println(BREAK);
        for (User u : users) { PRINT_STREAM.println(u); }
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

        PRINT_STREAM.println("Expecting 'admin' user:");
        PRINT_STREAM.println(BREAK);
        User present = repository.findOne(admin.getId());
        PRINT_STREAM.println(present);
        PRINT_STREAM.println();

        PRINT_STREAM.println("Expecting 'null' because no user with id '-' exists:");
        PRINT_STREAM.println(BREAK);
        User absent = repository.findOne("-");
        PRINT_STREAM.println(absent);
        PRINT_STREAM.println();

        PRINT_STREAM.println("Expecting all 3 users:");
        PRINT_STREAM.println(BREAK);
        for (User u : repository.findAll()) { PRINT_STREAM.println(u); }
        PRINT_STREAM.println();

        Collection<String> ids = new LinkedList<>();
        ids.add(root.getId());
        ids.add(user.getId());

        PRINT_STREAM.println("Expecting 'root' and 'user':");
        PRINT_STREAM.println(BREAK);
        for (User u : repository.findAll(ids)) { PRINT_STREAM.println(u); }
        PRINT_STREAM.println();

    }
}
