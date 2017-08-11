package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Main {

	private static final Map<String, Class<?>> EXAMPLES = new HashMap<>();

	public static void main(String[] args) {
		SpringApplication.run(EXAMPLES.get("crud"), args);
		System.exit(1);
	}

	static {
		EXAMPLES.put("basic", demo.example.basic.Runner.class);

		EXAMPLES.put("aql", demo.example.aql.Runner.class);
		EXAMPLES.put("by-example", demo.example.byexample.Runner.class);
		EXAMPLES.put("case-sensitivity", demo.example.casesensitivity.Runner.class);
		EXAMPLES.put("configuration", demo.example.configuration.Runner.class);
		EXAMPLES.put("crud", demo.example.crud.Runner.class);
		EXAMPLES.put("derived", demo.example.derived.Runner.class);
		EXAMPLES.put("geospatial", demo.example.geospatial.Runner.class);
		EXAMPLES.put("index", demo.example.index.Runner.class);
		EXAMPLES.put("page-and-sort", demo.example.pageandsort.Runner.class);
		EXAMPLES.put("template", demo.example.template.Runner.class);
	}
}
