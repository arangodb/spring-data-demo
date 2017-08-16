package demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

import java.io.PrintStream;

public abstract class AbstractRunner implements CommandLineRunner {


    public static final MyLoggerImpl LOGGER = new MyLoggerImpl(LoggingLevel.ERROR);
    public final String BREAK = "-------------------------------";

    @Override
    public abstract void run(String... args) throws Exception;

    public interface MyLogger {
        void log();
        void log(Object object);
    }

    public static class MyLoggerImpl implements MyLogger {

        private LoggingLevel loggingLevel;
        private static final Logger LOGGER = LoggerFactory.getLogger(MyLoggerImpl.class);

        public MyLoggerImpl(LoggingLevel loggingLevel) {
            this.loggingLevel = loggingLevel;
        }

        public void log () {
            log('\n');
        }

        public void log (Object o) {
//            System.err.println(o); if (true) { return; }
            if (o == null) { o = "null"; }
            switch (loggingLevel) {
                case INFO:
                    LOGGER.info(o.toString());
                    break;
                case WARN:
                    LOGGER.warn(o.toString());
                    break;
                case DEBUG:
                    LOGGER.debug(o.toString());
                    break;
                case ERROR:
                    LOGGER.error(o.toString());
                    break;
                case TRACE:
                    LOGGER.trace(o.toString());
                    break;
            }
        }
    }

    private enum LoggingLevel {

        TRACE, DEBUG, INFO, WARN, ERROR

    }
}
