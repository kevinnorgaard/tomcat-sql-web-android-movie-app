public class Movie {
    private final String id;
    private final String title;
    private final double price;
    private int quantity;

    public Movie(String id, String title, double price, int quantity) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
    }

    public String getId() {
        return this.id;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getTitle() {
        return this.title;
    }

    public double getPrice() {
        return this.price;
    }
}
