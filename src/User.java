import java.util.ArrayList;
import java.util.List;

public class User {
    private final String email;
    private final String id;
    private final List<Movie> shoppingCart;
    private final List<Sale> sales;

    public User(String id, String email) {
        this.id = id;
        this.email = email;
        this.shoppingCart = new ArrayList<Movie>();
        this.sales = new ArrayList<Sale>();
    }

    public String getId() {
        return id;
    }

    public void addToCart(Movie movie) {
        this.shoppingCart.add(movie);
    }

    public List<Movie> getCart() {
        return this.shoppingCart;
    }

    public int cartIndexOf(String id) {
        List<Movie> cart = getCart();
        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public void removeFromCart(Movie movie) {
        this.shoppingCart.remove(movie);
    }

    public void addSale(Sale sale) {
        this.sales.add(sale);
    }

    public List<Sale> getSales() {
        return this.sales;
    }
}