package com.company;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Hello world!
 *
 */
public class App {

    @Autowired
    private UserRepository userRepository;

    public void run() {
        User user = new User("Jon", "Snow");
        //userRepository.save(user);
    }

    public static void main(String[] args) {
        System.out.println("Hello, World!");
        App app = new App();
        //app.run();
    }
}
