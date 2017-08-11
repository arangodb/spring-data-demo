package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Main {

	private static final Map<String, Class<?>> EXAMPLES = new HashMap<>();

	public static void main(String[] args) {
		SpringApplication.run(EXAMPLES.get("basic"), args);
		System.exit(1);
	}

	static {
		EXAMPLES.put("basic", demo.example.basic.Runner.class);
	}
}
