package dev.maltsev.atm.domain;

import dev.maltsev.atm.util.Loggable;
import dev.maltsev.atm.service.exceptions.BankServiceException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.*;


/**
 * A bank's customer
 */
@RequiredArgsConstructor
public class Customer implements Loggable {

    private final String loginName;

    private final Account account = new Account();

    private final Map<Customer, Account> creditAccountMap = new LinkedHashMap<>();

    private final Map<Customer, Account> debitAccountMap = new LinkedHashMap<>();

    public BigDecimal getAccountBalance() {
        return account.getBalance().subtract(BigDecimal.ZERO);
    }

    @NotNull
    public List<Transaction> deposit(@NotNull BigDecimal amount) {
        List<Transaction> transactions = new ArrayList<>();

        // pay debts
        for (Map.Entry<Customer, Account> entry : new ArrayList<>(creditAccountMap.entrySet())) {
            Customer creditor = entry.getKey();
            Account creditAccount = entry.getValue();

            BigDecimal paymentAmount = creditAccount.getBalance().min(amount);
            if (paymentAmount.doubleValue() > 0) {
                Transaction transaction = payTo(creditor, paymentAmount);
                transactions.add(transaction);
                creditor.deposit(transaction.getAmount());
                amount = amount.subtract(transaction.getAmount());
            } else {
                break;
            }
        }

        // do deposit
        if (amount.doubleValue() > 0) {
            account.add(amount);
            Transaction transaction = new Transaction(null, loginName, amount);
            transactions.add(transaction);
        }

        return transactions;
    }

    @NotNull
    public Transaction withdraw(@NotNull BigDecimal amount) throws BankServiceException {
        if (account.getBalance().subtract(amount).doubleValue() < 0) {
            throw new BankServiceException("not enough money");
        } else {
            account.subtract(amount);
            return new Transaction(loginName, null, amount);
        }
    }

    @NotNull
    public List<Transaction> transfer(@NotNull Customer to, @NotNull BigDecimal amount) {
        List<Transaction> transactions = new ArrayList<>();

        // owe from destination
        if (to.isOwedTo(this)) {
            to.payTo(this, amount);
            amount = amount.subtract(amount);
        }

        // make transfer to destination
        if (amount.doubleValue() > 0 && account.getBalance().doubleValue() > 0) {
            BigDecimal transferAmount = account.getBalance().min(amount);
            to.account.add(transferAmount);
            account.subtract(transferAmount);

            Transaction transaction = new Transaction(loginName, to.getLoginName(), transferAmount);
            transactions.add(transaction);

            amount = amount.subtract(transferAmount);
        }

        // owe to destination
        if (amount.doubleValue() > 0) {
            oweTo(to, amount);
        }

        return transactions;
    }

    private boolean isOwedTo(@NotNull Customer creditor) {
        return this.getCreditAccountMap().containsKey(creditor);
    }

    @NotNull
    private Transaction payTo(@NotNull Customer creditor, @NotNull BigDecimal amount) {
        creditAccountMap.putIfAbsent(creditor, new Account());
        Account creditAccount = creditAccountMap.get(creditor);
        creditAccount.subtract(amount);

        if (creditAccount.isEmpty()) {
            creditAccountMap.remove(creditor);
        }

        creditor.debitAccountMap.putIfAbsent(this, new Account());
        Account debitAccount = creditor.debitAccountMap.get(this);
        debitAccount.subtract(amount);

        if (debitAccount.isEmpty()) {
            creditor.debitAccountMap.remove(this);
        }

        return new Transaction(loginName, creditor.getLoginName(), amount);
    }

    @NotNull
    public String getLoginName() {
        return loginName;
    }

    private void oweTo(@NotNull Customer creditor, @NotNull BigDecimal amount) {
        creditAccountMap.putIfAbsent(creditor, new Account());
        Account creditAccount = creditAccountMap.get(creditor);
        creditAccount.add(amount);

        creditor.debitAccountMap.putIfAbsent(this, new Account());
        Account debitAccount = creditor.debitAccountMap.get(this);
        debitAccount.add(amount);
    }

    @NotNull
    public Map<Customer, Account> getCreditAccountMap() {
        return new LinkedHashMap<>(creditAccountMap);
    }

    @NotNull
    public String getCreditStatement() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<Customer, Account> entry : getCreditAccountMap().entrySet()) {
            Customer creditor = entry.getKey();
            BigDecimal amount = entry.getValue().getBalance();
            stringBuilder.append("Owed $")
                    .append(amount)
                    .append(" to ")
                    .append(creditor.getLoginName());
        }

        return stringBuilder.toString();
    }

    @NotNull
    public String getDebitStatement() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<Customer, Account> entry : getDebitAccountMap().entrySet()) {
            Customer debtor = entry.getKey();
            BigDecimal amount = entry.getValue().getBalance();
            stringBuilder.append("Owed $")
                    .append(amount)
                    .append(" from ")
                    .append(debtor.getLoginName());
        }

        return stringBuilder.toString();
    }

    @NotNull
    public Map<Customer, Account> getDebitAccountMap() {
        return new LinkedHashMap<>(debitAccountMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loginName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Customer customer = (Customer) o;
        return loginName.equals(customer.loginName);
    }
}
