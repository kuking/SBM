package uk.co.sbm;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderTest {

    private Order left, right;

    @Before
    public void before() {
        left = new Order("user1", new BigDecimal("1000"), new BigDecimal("3.1415"), Order.Type.BUY);
        right = new Order("user1", new BigDecimal("1000.0000"), new BigDecimal("3.14"), Order.Type.BUY);
    }

    @Test
    public void bigDecimalIntricacies_differentDecimalPlacesInBigDecimal() {
        assertThat(left).isNotEqualTo(right);
    }

    @Test
    public void bigDecimalsCanBeComparedAfterNormalized() {
        assertThat(left.normalized()).isEqualTo(right.normalized());
    }

}
