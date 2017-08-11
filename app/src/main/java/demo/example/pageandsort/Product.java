package demo.example.pageandsort;

public class Product {

    private String name;

    private long price;

    public Product(String name, long price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("Product: {name: '%s', price: '%d'}", name, price);
    }
}
