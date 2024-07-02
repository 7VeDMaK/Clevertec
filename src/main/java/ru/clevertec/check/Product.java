package main.java.ru.clevertec.check;

public class Product {
    private final int id;
    private final String description;
    private final double price;
    private final int quantityInStock;
    private final boolean wholesale;

    public Product(int id, String description, double price, int quantityInStock, boolean wholesale) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.quantityInStock = quantityInStock;
        this.wholesale = wholesale;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public boolean isWholesale() {
        return wholesale;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", quantityInStock=" + quantityInStock +
                ", wholesale=" + wholesale +
                '}';
    }
}