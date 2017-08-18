package demo;

import com.arangodb.ArangoDB;
import com.arangodb.springframework.annotation.EnableArangoRepositories;
import com.arangodb.springframework.core.config.AbstractArangoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * Created by user on 10/08/17.
 */
@Configuration
@EnableArangoRepositories(basePackages = { "demo" })
public class SimpleConfiguration extends AbstractArangoConfiguration {

    @Override
    public ArangoDB.Builder arango() {
        return new ArangoDB.Builder()/*.host(String).user(String).port(int)... - could be added to override defaults*/;
    }

    @Override
    public String database() { return "spring-test-db"; }
}
