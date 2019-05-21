package uk.co.sbm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@AllArgsConstructor
public class SummaryEntry {
    @NonNull
    final private BigDecimal quantity;
    @NonNull
    final private BigDecimal price;

    public SummaryEntry normalized() {
        return new SummaryEntry(
                quantity.setScale(2, RoundingMode.HALF_EVEN),
                price.setScale(2, RoundingMode.HALF_EVEN));
    }
}
