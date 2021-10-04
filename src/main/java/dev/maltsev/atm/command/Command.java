package dev.maltsev.atm.command;

import dev.maltsev.atm.domain.Customer;
import dev.maltsev.atm.domain.Transaction;
import dev.maltsev.atm.service.BankService;
import dev.maltsev.atm.service.exceptions.BankServiceException;
import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * A "Command" pattern interface
 */
public interface Command {

    @NotNull
    String execute(BankService service) throws BankServiceException;

    @NotNull
    default String printTransactionStatement(List<Transaction> transactions, Customer customer) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Transaction transaction : transactions) {
            String to = transaction.getTo();
            if (!to.equals(customer.getLoginName())) {
                stringBuilder
                        .append(transaction.getStatement())
                        .append("\n");
            }
        }

        return stringBuilder.toString();
    }

    @NotNull
    default String printBalanceStatement(Customer customer) {
        return new StringBuilder()
                .append("Your balance is $")
                .append(customer.getAccountBalance().setScale(0))
                .append("\n")
                .toString();
    }

    @NotNull
    default String printCreditStatement(Customer customer) {
        String statement = customer.getCreditStatement();
        StringBuilder stringBuilder = new StringBuilder();

        if (!statement.isEmpty()) {
            stringBuilder
                    .append(statement)
                    .append("\n");
        }

        return stringBuilder.toString();
    }

    @NotNull
    default String printDebitStatement(Customer customer) {
        String statement = customer.getDebitStatement();
        StringBuilder stringBuilder = new StringBuilder();

        if (!statement.isEmpty()) {
            stringBuilder
                    .append(statement)
                    .append("\n");
        }

        return stringBuilder.toString();
    }
}
