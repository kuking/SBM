package uk.co.sbm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;

@Data
@AllArgsConstructor
public class Order {
    public enum Type {BUY, SELL}

    static public Comparator<Order> NATURAL_ORDER =
            Comparator.comparing(Order::getType)
                    .thenComparing(Order::getPrice)
                    .thenComparing(Order::getQuantity)
                    .thenComparing(Order::getUserId);

    @NonNull
    final private String userId;
    @NonNull
    final private BigDecimal quantity;
    @NonNull
    final private BigDecimal price;
    @NonNull
    final private Type type;

    /**
     * normalizes quantity and price to two decimal places rounded half even.
     * Lombok does provides many features but not provide a post-builder/constructor callback.
     */
    public Order normalized() {
        return new Order(userId,
                quantity.setScale(2, RoundingMode.HALF_EVEN),
                price.setScale(2, RoundingMode.HALF_EVEN),
                type);
    }

}
