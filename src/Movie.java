public class Movie {
    private final String title;
    private final double price;
    private int quantity;

    public Movie(String title, double price, int quantity) {
        this.title = title;
        this.price = price;
        this.quantity = quantity;
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
