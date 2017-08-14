package demo.example.pageandsort;

import demo.AbstractRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        products.add(new Product("headphones", 30));
        products.add(new Product("earphones", 30));
        products.add(new Product("charger", 20));

        repository.save(products);
        PRINT_STREAM.println("All 5 products:");
        PRINT_STREAM.println(BREAK);
        PRINT_STREAM.println(products);
        PRINT_STREAM.println();

        Sort sort = new Sort(Arrays.asList(new Sort.Order[] {
                new Sort.Order(Sort.Direction.DESC, "price"),
                new Sort.Order(Sort.Direction.ASC, "name")
        }));

        PRINT_STREAM.println("findAll(Sort sort) - expecting products sorted by price (desc) and name (asc):");
        PRINT_STREAM.println(BREAK);
        PRINT_STREAM.println(repository.findAll(sort));
        PRINT_STREAM.println();

        PRINT_STREAM.println("findAll(Pageable pageable) - expecting 'earphones' and 'headphones':");
        PRINT_STREAM.println(BREAK);
        Page page = repository.findAll(new PageRequest(1, 2, sort));
        PRINT_STREAM.println(page);
        PRINT_STREAM.println(page.getContent());
        PRINT_STREAM.println("Total elements: " + page.getTotalElements());
        PRINT_STREAM.println("Total pages: " + page.getTotalPages());
        PRINT_STREAM.println();
    }
}
