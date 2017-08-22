package demo.example.pageandsort;

public class Product {

    private String name;

    private int price;

    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("\nProduct: {name: '%s', price: '%d'}", name, price);
    }
}
