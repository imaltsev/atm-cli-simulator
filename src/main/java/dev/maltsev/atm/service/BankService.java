package dev.maltsev.atm.service;

import dev.maltsev.atm.domain.Customer;
import dev.maltsev.atm.domain.Transaction;
import dev.maltsev.atm.service.exceptions.BankServiceException;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;


/**
 * A service to implement bank accounting functionality
 */
public interface BankService {

    /**
     * Authenticate customer to system with provided login name and create it if not exist
     *
     * @param loginName
     * @return a customer instance
     * @throws BankServiceException if given customer is already authenticated
     */
    @NotNull
    Customer login(@NotNull String loginName) throws BankServiceException;

    /**
     * Get current authenticated customer
     *
     * @return a customer instance
     * @throws BankServiceException if there is no authenticated customer
     */
    @NotNull
    Customer getCurrentCustomer() throws BankServiceException;

    /**
     * Deposit given amount of money to current authenticated customer's account
     *
     * @param amount of money to deposit
     * @return a list of transactions made
     * @throws BankServiceException if there is no authenticated customer
     * @throws BankServiceException if negative or zero amount of money passed
     */
    @NotNull
    List<Transaction> deposit(@NotNull BigDecimal amount) throws BankServiceException;

    /**
     * Withdraw given amount of money from current authenticated customer's account
     *
     * @param amount an amount of money to withdraw
     * @return a list of transactions made
     * @throws BankServiceException if there is no authenticated customer
     * @throws BankServiceException if negative or zero amount of money passed
     * @throws BankServiceException if there is not enough money on customer's account
     */
    @NotNull
    Transaction withdraw(@NotNull BigDecimal amount) throws BankServiceException;

    /**
     * Transfer given amount of money from current authenticated customer's account to the given customer's account
     *
     * @param toLoginName a destination customer
     * @param amount      an amount of money to transfer
     * @return a list of transactions made
     * @throws BankServiceException if there is no authenticated customer
     * @throws BankServiceException if negative or zero amount of money passed
     * @throws BankServiceException if there is no destination customer with name provided
     * @throws BankServiceException if destination customer is the same as current customer
     */
    @NotNull
    List<Transaction> transfer(@NotNull String toLoginName, @NotNull BigDecimal amount) throws BankServiceException;

    /**
     * Logout current authenticated customer
     *
     * @return a customer instance
     * @throws BankServiceException if there is no authenticated customer
     */
    @NotNull
    Customer logout() throws BankServiceException;
}
