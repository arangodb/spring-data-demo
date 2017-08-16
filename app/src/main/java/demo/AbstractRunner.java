package demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

import java.io.PrintStream;

public abstract class AbstractRunner implements CommandLineRunner {

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractRunner.class);

    public final PrintStream PRINT_STREAM = System.err;
    public final String BREAK = "-------------------------------";

    @Override
    public abstract void run(String... args) throws Exception;
}
