/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package uk.co.sbm;

import io.vavr.collection.List;
import org.assertj.core.data.Percentage;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


public class LiveOrderBoardServiceTest {

    private LiveOrderBoardService underTest;

    @Before
    public void before() {
        underTest = new LiveOrderBoardService();
    }

    @Test
    public void register_simplest() {
        Order order = givenBuyOrder("100", "3.14");

        assertThat(underTest.register(order)).isTrue();
    }

    @Test
    public void register_shouldNotAcceptDuplicates() {
        // this might be questionable, but decided as is. OrderNo could be added to order object for uniqueness.
        Order order = givenBuyOrder("100", "3.14");
        underTest.register(order);

        assertThat(underTest.register(order)).isFalse();
    }

    @Test
    public void register_itNormalizesOrdersFirst() {
        underTest.register(givenBuyOrder("1000.0", "3.1415"));

        assertThat(underTest.register(givenBuyOrder("1000", "3.14"))).isFalse();
        // effectively the same price/quantity after normalization, therefore should fail
    }

    @Test
    public void getSummary_simplest() {
        Order b1 = givenBuyOrder("100", "3.1415");
        underTest.register(b1);

        List<SummaryEntry> book = underTest.summary(Order.Type.BUY);

        assertThat(book).contains(new SummaryEntry(new BigDecimal(100), new BigDecimal(3.14)).normalized());
    }

    @Test
    public void getSummary_buysAndSellsAreNotMixed() {
        underTest.register(givenBuyOrder("100", "3.14"));
        underTest.register(givenBuyOrder("100", "2.71"));
        underTest.register(givenSellOrder("200", "40"));

        List<SummaryEntry> sellBook = underTest.summary(Order.Type.SELL);
        List<SummaryEntry> buyBook = underTest.summary(Order.Type.BUY);

        assertThat(sellBook).isNotEqualTo(buyBook);
        assertThat(buyBook.size()).isEqualTo(2);
        assertThat(sellBook.size()).isEqualTo(1);
    }

    @Test
    public void getSummary_mergeSimple() {
        underTest.register(givenBuyOrder("user-1", "100", "3.14"));
        underTest.register(givenBuyOrder("user-2", "50", "3.1415"));
        underTest.register(givenBuyOrder("user-3", "5", "3.1415"));

        List<SummaryEntry> book = underTest.summary(Order.Type.BUY);

        assertThat(book).size().isEqualTo(1);
        assertThat(book.last().getQuantity()).isCloseTo(new BigDecimal("155"), Percentage.withPercentage(1));
        assertThat(book.last().getPrice()).isCloseTo(new BigDecimal("3.14"), Percentage.withPercentage(1));
    }

    @Test
    public void getSummary_mergeComplex() {
        for (int i = 0; i < 100; i++) {
            String iAsS = "" + i;
            underTest.register(givenBuyOrder("user 1", iAsS, iAsS));
            underTest.register(givenBuyOrder("user 2", iAsS, iAsS));
            underTest.register(givenSellOrder(iAsS, iAsS));
        }

        List<SummaryEntry> buys = underTest.summary(Order.Type.BUY);
        List<SummaryEntry> sells = underTest.summary(Order.Type.SELL);

        assertThat(buys).size().isEqualTo(100); // buys are joined
        assertThat(sells).size().isEqualTo(100);
        for (int i = 0; i < 100; i++) {
            assertThat(buys).contains(new SummaryEntry(new BigDecimal(i * 2), new BigDecimal(i)).normalized());
            assertThat(sells).contains(new SummaryEntry(new BigDecimal(i), new BigDecimal(i)).normalized());
        }
    }

    @Test
    public void cancel_nil() {
        assertThat(underTest.cancel(givenBuyOrder("123", "234"))).isFalse();
    }

    @Test
    public void cancel_simple() {
        Order o = givenBuyOrder("123", "234");
        underTest.register(o);

        assertThat(underTest.cancel(o)).isTrue();
        assertThat(underTest.summary(Order.Type.BUY)).isEmpty();

        assertThat(underTest.cancel(o)).isFalse();
    }

    // ------------------------------------------------------------------------------------------------------

    private Order givenBuyOrder(String user, String quantity, String price) {
        return new Order(user, new BigDecimal(quantity), new BigDecimal(price), Order.Type.BUY);
    }

    private Order givenBuyOrder(String quantity, String price) {
        return givenBuyOrder("user1", quantity, price);
    }

    private Order givenSellOrder(String quantity, String price) {
        return new Order("user1", new BigDecimal(quantity), new BigDecimal(price), Order.Type.SELL);
    }

}
