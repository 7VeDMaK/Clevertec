package main.java.ru.clevertec.check;

public record Product(int id, String description, double price, int quantityInStock, boolean wholesale) {

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