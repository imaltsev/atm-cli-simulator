package dev.maltsev.atm.service.impl;

import dev.maltsev.atm.domain.Customer;
import dev.maltsev.atm.domain.Transaction;
import dev.maltsev.atm.service.BankService;
import dev.maltsev.atm.service.exceptions.BankServiceException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class BankServiceImpl implements BankService {

    private final Map<String, Customer> customers = new HashMap<>();
    private Customer currentCustomer;

    @Override
    @NotNull
    public Customer login(@NotNull String loginName) throws BankServiceException {
        if (currentCustomer == null) {
            if (!customers.containsKey(loginName)) {
                customers.putIfAbsent(loginName, new Customer(loginName));
            }
            currentCustomer = customers.get(loginName);
            return currentCustomer;
        } else {
            throw new BankServiceException("login name is empty");
        }
    }

    @NotNull
    @Override
    public Customer getCurrentCustomer() throws BankServiceException {
        if (currentCustomer == null) {
            throw new BankServiceException("there is no authenticated customer");
        } else {
            return currentCustomer;
        }
    }

    @Override
    @NotNull
    public List<Transaction> deposit(@NotNull BigDecimal amount) throws BankServiceException {
        if (amount.doubleValue() <= 0) {
            throw new BankServiceException(amount + " should be greater than zero");
        }

        if (currentCustomer == null) {
            throw new BankServiceException("there is no authenticated customer");
        }

        return currentCustomer.deposit(amount);
    }

    @Override
    @NotNull
    public Transaction withdraw(@NotNull BigDecimal amount) throws BankServiceException {
        if (amount.doubleValue() <= 0) {
            throw new BankServiceException(amount + " should be greater than zero");
        }

        if (currentCustomer == null) {
            throw new BankServiceException("there is no authenticated customer");
        }

        return currentCustomer.withdraw(amount);
    }

    @Override
    @NotNull
    public List<Transaction> transfer(@NotNull String to, @NotNull BigDecimal amount) throws BankServiceException {
        if (amount.doubleValue() <= 0) {
            throw new BankServiceException(amount + " should be greater than zero");
        }

        if (currentCustomer == null) {
            throw new BankServiceException("there is no authenticated customer");
        }

        if (!customers.containsKey(to)) {
            throw new BankServiceException(String.format("no customer with login name %s found", to));
        }

        if (to.equals(currentCustomer.getLoginName())) {
            throw new BankServiceException("you can't transfer money to yourself");

        }

        return currentCustomer.transfer(customers.get(to), amount);
    }

    @Override
    @NotNull
    public Customer logout() throws BankServiceException {
        if (currentCustomer != null) {
            Customer loggedOutCustomer = currentCustomer;
            currentCustomer = null;
            return loggedOutCustomer;
        } else {
            throw new BankServiceException("there is no authenticated customer");
        }
    }
}
