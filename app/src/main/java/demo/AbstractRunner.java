package demo;

import org.springframework.boot.CommandLineRunner;

import java.io.PrintStream;

public abstract class AbstractRunner implements CommandLineRunner {

    public final PrintStream PRINT_STREAM = System.err;
    public final String BREAK = "-------------------------------";

    @Override
    public abstract void run(String... args) throws Exception;
}
