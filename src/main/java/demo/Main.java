package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Main {

	private static final Map<String, Class<?>> EXAMPLES = new HashMap<>();

	public static void main(String[] args) {
		System.exit(SpringApplication.exit(SpringApplication.run(EXAMPLES.get("graph"), args)));
	}

	static {
		EXAMPLES.put("aql", demo.example.aql.Runner.class);
		EXAMPLES.put("by-example", demo.example.byexample.Runner.class);
		EXAMPLES.put("by-ref", demo.example.byreference.Runner.class);
		EXAMPLES.put("crud", demo.example.crud.Runner.class);
		EXAMPLES.put("derived", demo.example.derived.Runner.class);
		EXAMPLES.put("geospatial", demo.example.geospatial.Runner.class);
		EXAMPLES.put("graph", demo.example.graph.Runner.class);
		EXAMPLES.put("page-and-sort", demo.example.pageandsort.Runner.class);
		EXAMPLES.put("template", demo.example.template.Runner.class);
	}
}
