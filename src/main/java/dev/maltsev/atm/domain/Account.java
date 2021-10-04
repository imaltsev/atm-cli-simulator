package dev.maltsev.atm.domain;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;


/**
 * A customer account holding balance
 */
public class Account {

    protected BigDecimal balance = BigDecimal.ZERO;

    public boolean isEmpty() {
        return balance.compareTo(BigDecimal.ZERO) == 0;
    }

    public void add(@NotNull BigDecimal amount) {
        balance = balance.add(amount);
    }

    public BigDecimal getBalance() {
        return balance.subtract(BigDecimal.ZERO);
    }

    public void subtract(@NotNull BigDecimal amount) {
        if (balance.subtract(amount).doubleValue() < 0) {
            throw new IllegalArgumentException("Not enough balance");
        }

        balance = balance.subtract(amount);
    }
}
