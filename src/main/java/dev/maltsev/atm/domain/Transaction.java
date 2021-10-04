package dev.maltsev.atm.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;


/**
 * A transaction between two customers
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class Transaction {

    private final String from;
    private final String to;
    private final BigDecimal amount;

    public BigDecimal getAmount() {
        return amount.subtract(BigDecimal.ZERO);
    }

    @NotNull
    public String getStatement() {
        return new StringBuilder()
                .append("Transferred $")
                .append(amount.setScale(0))
                .append(" to ")
                .append(to)
                .toString();
    }
}
