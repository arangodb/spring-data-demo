package demo.example.pageandsort;

import demo.AbstractRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

@ComponentScan("demo")
public class Runner extends AbstractRunner {

    @Autowired
    ProductRepository repository;

    @Override
    public void run(String... args) throws Exception {

        repository.deleteAll();
        PRINT_STREAM.println("deleteAll() - cleared 'product' collection");
        PRINT_STREAM.println(BREAK);

        Collection<Product> products = new LinkedList<>();
        products.add(new Product("phone", 300));
        products.add(new Product("laptop", 1000));
        products.add(new Product("earphones", 30));
        products.add(new Product("headphones", 30));
        products.add(new Product("charger", 20));

        repository.save(products);
        PRINT_STREAM.println("All 5 products:");
        PRINT_STREAM.println(BREAK);
        PRINT_STREAM.println(products);
        PRINT_STREAM.println();

        Sort sort = new Sort(Arrays.asList(new Sort.Order[] {
                new Sort.Order(Sort.Direction.ASC, "price"),
                new Sort.Order(Sort.Direction.DESC, "name")
        }));

        PRINT_STREAM.println("findAll(Sort sort) - expecting products sorted by price (asc) and name (desc):");
        PRINT_STREAM.println(BREAK);
        Iterable<Product> sorted = repository.findAll(sort);
        PRINT_STREAM.println(sorted);
        PRINT_STREAM.println();
    }
}
