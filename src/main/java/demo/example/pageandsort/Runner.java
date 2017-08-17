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
        LOGGER.log("deleteAll() - cleared 'product' collection");
        LOGGER.log(BREAK);

        Collection<Product> products = new LinkedList<>();
        products.add(new Product("phone", 300));
        products.add(new Product("laptop", 1000));
        products.add(new Product("headphones", 30));
        products.add(new Product("earphones", 30));
        products.add(new Product("charger", 20));

        repository.save(products);
        LOGGER.log("All 5 products:");
        LOGGER.log(BREAK);
        LOGGER.log(products);
        LOGGER.log();

        Sort sort = new Sort(Arrays.asList(new Sort.Order[] {
                new Sort.Order(Sort.Direction.DESC, "price"),
                new Sort.Order(Sort.Direction.ASC, "name")
        }));

        LOGGER.log("findAll(Sort sort) - expecting products sorted by price (desc) and name (asc):");
        LOGGER.log(BREAK);
        LOGGER.log(repository.findAll(sort));
        LOGGER.log();

        LOGGER.log("findAll(Pageable pageable) - expecting 'earphones' and 'headphones':");
        LOGGER.log(BREAK);
        Page page = repository.findAll(new PageRequest(1, 2, sort));
        LOGGER.log(page);
        LOGGER.log(page.getContent());
        LOGGER.log("Total elements: " + page.getTotalElements());
        LOGGER.log("Total pages: " + page.getTotalPages());
        LOGGER.log();
    }
}
