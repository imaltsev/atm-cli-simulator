package dev.maltsev.atm.service;

import dev.maltsev.atm.domain.Transaction;
import dev.maltsev.atm.domain.Account;
import dev.maltsev.atm.domain.Customer;
import dev.maltsev.atm.service.exceptions.BankServiceException;
import dev.maltsev.atm.service.impl.BankServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.*;


public class BankServiceTests {

    private BankService bankService;

    @BeforeEach
    void setUp() {
        bankService = new BankServiceImpl();
    }

    /**
     * login tests
     */
    @Test
    void testLogin_Ok() throws BankServiceException {
        String loginName = "user";
        Customer customer = bankService.login(loginName);
        assertNotNull(customer);
        assertEquals(loginName, customer.getLoginName());
        assertEquals(0, customer.getAccountBalance().doubleValue());
        assertEquals(0, customer.getCreditAccountMap().size());
        assertEquals(0, customer.getDebitAccountMap().size());
    }

    @Test
    void testLogin_ExceptionThrown_IfSameCustomerHasAlreadyLoggedIn() throws BankServiceException {
        bankService.login("user");
        assertThrows(BankServiceException.class, () -> bankService.login("user"));
    }

    @Test
    void testLogin_ExceptionThrown_IfAnotherCustomerHasLoggedIn() throws BankServiceException {
        bankService.login("user");
        assertThrows(BankServiceException.class, () -> bankService.login("another_user"));
    }


    /**
     * getCurrentCustomer() tests
     */
    @Test
    void testGetCurrentCustomer_ExceptionThrown_IfNoCustomerLoggedIn() {
        assertThrows(BankServiceException.class, () -> bankService.getCurrentCustomer());
    }

    @Test
    void testGetCurrentCustomer_Ok() throws BankServiceException {
        String loginName = "user";
        bankService.login(loginName);
        Customer customer = bankService.getCurrentCustomer();
        assertNotNull(customer);
        assertEquals(loginName, customer.getLoginName());
        assertEquals(0, customer.getAccountBalance().doubleValue());
        assertEquals(0, customer.getCreditAccountMap().size());
        assertEquals(0, customer.getDebitAccountMap().size());
    }

    /**
     * logout tests
     */
    @Test
    void testLogout_ExceptionThrown_IfNoCustomerLoggedIn() {
        assertThrows(BankServiceException.class, () -> bankService.logout());
    }

    @Test
    void testLogout_Ok() throws BankServiceException {
        String loginName = "user";
        bankService.login(loginName);
        Customer customer = bankService.logout();

        assertNotNull(customer);
        assertEquals(loginName, customer.getLoginName());
        assertEquals(0, customer.getAccountBalance().doubleValue());
        assertEquals(0, customer.getCreditAccountMap().size());
        assertEquals(0, customer.getDebitAccountMap().size());

        bankService.login(loginName);
    }

    @Test
    void testLogout_ExceptionThrown_IfCustomerHasAlreadyLoggedOut() throws BankServiceException {
        bankService.login("user");
        bankService.logout();
        assertThrows(BankServiceException.class, () -> bankService.logout());
    }

    /**
     * deposit tests
     */
    @Test
    void testDeposit_ExceptionThrown_IfNoCustomerLoggedIn() {
        assertThrows(BankServiceException.class, () -> bankService.deposit(valueOf(10)));
    }

    @Test
    void testDeposit_ExceptionThrown_If_NegativeAmount() throws BankServiceException {
        bankService.login("user");
        assertThrows(BankServiceException.class, () -> bankService.deposit(valueOf(-1)));
    }

    @Test
    void testDeposit_ExceptionThrown_IfZeroAmount() throws BankServiceException {
        bankService.login("user");
        assertThrows(BankServiceException.class, () -> bankService.deposit(valueOf(0)));
    }

    @Test
    void testDeposit_IfNoDebt() throws BankServiceException {
        String loginName = "user";
        Customer customer = bankService.login(loginName);
        BigDecimal amount = valueOf(100);

        List<Transaction> transactions = bankService.deposit(amount);

        assertEquals(amount, customer.getAccountBalance());
        assertEquals(0, customer.getCreditAccountMap().size());
        assertEquals(0, customer.getDebitAccountMap().size());
        assertEquals(1, transactions.size());
        assertEquals(new Transaction(null, loginName, amount), transactions.get(0));
    }

    @Test
    void testDeposit_SingleCreditor_IfDepositGreaterThanDebt() throws BankServiceException {
        String creditorLoginName = "creditor_user";
        Customer creditor = bankService.login(creditorLoginName);
        bankService.logout();

        String debtorLoginName = "user";
        Customer debtor = bankService.login(debtorLoginName);
        bankService.transfer(creditorLoginName, valueOf(60));

        List<Transaction> transactions = bankService.deposit(valueOf(100));

        assertEquals(valueOf(40), debtor.getAccountBalance());
        assertEquals(valueOf(60), creditor.getAccountBalance());

        assertEquals(0, debtor.getCreditAccountMap().size());
        assertEquals(0, debtor.getDebitAccountMap().size());

        assertEquals(0, creditor.getCreditAccountMap().size());
        assertEquals(0, creditor.getDebitAccountMap().size());

        assertEquals(2, transactions.size());
        assertEquals(new Transaction(debtorLoginName, creditorLoginName, valueOf(60)), transactions.get(0));
        assertEquals(new Transaction(null, debtorLoginName, valueOf(40)), transactions.get(1));
    }

    @Test
    void testDeposit_SingleCreditor_IfDepositEqualsToDebt() throws BankServiceException {
        String creditorLoginName = "creditor_user";
        Customer creditor = bankService.login(creditorLoginName);
        bankService.logout();

        String debtorLoginName = "user";
        Customer debtor = bankService.login(debtorLoginName);
        bankService.transfer(creditorLoginName, valueOf(60));

        List<Transaction> transactions = bankService.deposit(valueOf(60));

        assertEquals(valueOf(0), debtor.getAccountBalance());
        assertEquals(valueOf(60), creditor.getAccountBalance());

        assertEquals(0, debtor.getCreditAccountMap().size());
        assertEquals(0, debtor.getDebitAccountMap().size());

        assertEquals(0, creditor.getCreditAccountMap().size());
        assertEquals(0, creditor.getDebitAccountMap().size());

        assertEquals(1, transactions.size());
        assertEquals(new Transaction(debtorLoginName, creditorLoginName, valueOf(60)), transactions.get(0));
    }

    @Test
    void testDeposit_SingleCreditor_IfDepositLessThanDebt() throws BankServiceException {
        String creditorLoginName = "creditor_user";
        Customer creditor = bankService.login(creditorLoginName);
        bankService.logout();

        String debtorLoginName = "user";
        Customer debtor = bankService.login(debtorLoginName);
        bankService.transfer(creditorLoginName, valueOf(60));

        List<Transaction> transactions = bankService.deposit(valueOf(40));

        assertEquals(valueOf(0), debtor.getAccountBalance());
        assertEquals(valueOf(40), creditor.getAccountBalance());

        assertEquals(1, debtor.getCreditAccountMap().size());
        Account creditAccount = debtor.getCreditAccountMap().get(creditor);
        assertNotNull(creditAccount);
        assertEquals(20, creditAccount.getBalance().doubleValue());
        assertEquals(0, debtor.getDebitAccountMap().size());

        assertEquals(0, creditor.getCreditAccountMap().size());
        assertEquals(1, creditor.getDebitAccountMap().size());
        Account debitAccount = creditor.getDebitAccountMap().get(debtor);
        assertNotNull(debitAccount);
        assertEquals(20, debitAccount.getBalance().doubleValue());

        assertEquals(1, transactions.size());
        assertEquals(new Transaction(debtorLoginName, creditorLoginName, valueOf(40)), transactions.get(0));
    }

    @Test
    void testDeposit_IfSeveralCreditors() throws BankServiceException {
        String creditorLoginName1 = "creditor_user1";
        Customer creditor1 = bankService.login(creditorLoginName1);
        bankService.logout();

        String creditorLoginName2 = "creditor_user2";
        Customer creditor2 = bankService.login(creditorLoginName2);
        bankService.logout();

        String debtorLoginName = "user";
        Customer debtor = bankService.login(debtorLoginName);
        bankService.transfer(creditorLoginName1, valueOf(25));
        bankService.transfer(creditorLoginName2, valueOf(35));

        List<Transaction> transactions = bankService.deposit(valueOf(100));

        assertEquals(0, debtor.getCreditAccountMap().size());
        assertEquals(0, debtor.getDebitAccountMap().size());
        assertEquals(0, creditor1.getCreditAccountMap().size());
        assertEquals(0, creditor1.getDebitAccountMap().size());
        assertEquals(0, creditor2.getCreditAccountMap().size());
        assertEquals(0, creditor2.getDebitAccountMap().size());

        assertEquals(valueOf(40), debtor.getAccountBalance());
        assertEquals(valueOf(25), creditor1.getAccountBalance());
        assertEquals(valueOf(35), creditor2.getAccountBalance());

        assertEquals(3, transactions.size());
        assertEquals(new Transaction(debtorLoginName, creditorLoginName1, valueOf(25)), transactions.get(0));
        assertEquals(new Transaction(debtorLoginName, creditorLoginName2, valueOf(35)), transactions.get(1));
        assertEquals(new Transaction(null, debtorLoginName, valueOf(40)), transactions.get(2));
    }

    /**
     * withdraw tests
     */
    @Test
    void testWithdraw_ExceptionThrown_IfNoCustomerLoggedIn() {
        assertThrows(BankServiceException.class, () -> bankService.withdraw(new BigDecimal(10)));
    }

    @Test
    void testWithdraw_ExceptionThrown_IfNegativeAmount() throws BankServiceException {
        bankService.login("user");
        assertThrows(BankServiceException.class, () -> bankService.withdraw(valueOf(-1)));
    }

    @Test
    void testWithdraw_ExceptionThrown_IfZeroAmount() throws BankServiceException {
        bankService.login("user");
        assertThrows(BankServiceException.class, () -> bankService.withdraw(valueOf(0)));
    }

    @Test
    void testWithdraw_ExceptionThrown_IfNotEnoughBalance() throws BankServiceException {
        String loginName = "user";
        bankService.login(loginName);
        assertThrows(BankServiceException.class, () -> bankService.withdraw(new BigDecimal(10)));
    }

    @Test
    void testWithdraw_Ok_IfAmountLessThanDeposit() throws BankServiceException {
        String loginName = "user";
        Customer customer = bankService.login(loginName);
        bankService.deposit(valueOf(100));

        Transaction transaction = bankService.withdraw(valueOf(10));

        assertEquals(0, customer.getCreditAccountMap().size());
        assertEquals(0, customer.getDebitAccountMap().size());

        assertNotNull(transaction);
        assertEquals(new Transaction(loginName, null, valueOf(10)), transaction);
    }

    @Test
    void testWithdraw_Ok_IfAmountEqualsToDeposit() throws BankServiceException {
        String loginName = "user";
        Customer customer = bankService.login(loginName);
        bankService.deposit(valueOf(10));

        Transaction transaction = bankService.withdraw(valueOf(10));

        assertEquals(0, customer.getCreditAccountMap().size());
        assertEquals(0, customer.getDebitAccountMap().size());

        assertNotNull(transaction);
        assertEquals(new Transaction(loginName, null, valueOf(10)), transaction);
    }

    /**
     * transfer tests
     */
    @Test
    void testTransfer_ExceptionThrown_IfNoCustomerLoggedIn() {
        assertThrows(BankServiceException.class, () -> bankService.transfer("user", valueOf(10)));
    }

    @Test
    void testTransfer_ExceptionThrown_IfNegativeAmount() throws BankServiceException {
        String toLoginName = "another_user";
        bankService.login(toLoginName);
        bankService.logout();
        bankService.login("user");
        assertThrows(BankServiceException.class, () -> bankService.transfer(toLoginName, valueOf(-1)));
    }

    @Test
    void testTransfer_ExceptionThrown_IfZeroAmount() throws BankServiceException {
        String toLoginName = "another_user";
        bankService.login(toLoginName);
        bankService.logout();
        bankService.login("user");
        assertThrows(BankServiceException.class, () -> bankService.transfer(toLoginName, valueOf(0)));
    }

    @Test
    void testTransfer_ExceptionThrown_IfSelfTransfer() throws BankServiceException {
        bankService.login("user");
        assertThrows(BankServiceException.class, () -> bankService.transfer("user", valueOf(100)));
    }

    @Test
    void testTransfer_ExceptionThrown_IfNoToCustomerExists() throws BankServiceException {
        String from = "user";
        bankService.login(from);
        bankService.deposit(valueOf(100));
        assertThrows(BankServiceException.class, () -> bankService.transfer("another_user", valueOf(10)));
    }

    @Test
    void testTransfer_IfAmountLessThanDeposit() throws BankServiceException {
        String toLoginName = "another_user";
        Customer to = bankService.login(toLoginName);
        bankService.logout();

        String fromLoginName = "user";
        Customer from = bankService.login(fromLoginName);
        bankService.deposit(valueOf(100));

        BigDecimal transferAmount = valueOf(60);

        List<Transaction> transactions = bankService.transfer(toLoginName, transferAmount);

        assertEquals(0, from.getCreditAccountMap().size());
        assertEquals(0, from.getDebitAccountMap().size());

        assertEquals(0, to.getCreditAccountMap().size());
        assertEquals(0, to.getDebitAccountMap().size());

        assertEquals(valueOf(40), from.getAccountBalance());
        assertEquals(valueOf(60), to.getAccountBalance());

        assertEquals(1, transactions.size());
        assertEquals(new Transaction(fromLoginName, toLoginName, transferAmount), transactions.get(0));
    }

    @Test
    void testTransfer_IfAmountEqualsToDeposit() throws BankServiceException {
        String toLoginName = "another_user";
        Customer to = bankService.login(toLoginName);
        bankService.logout();

        String fromLoginName = "user";
        Customer from = bankService.login(fromLoginName);
        bankService.deposit(valueOf(60));

        BigDecimal transferAmount = valueOf(60);

        List<Transaction> transactions = bankService.transfer(toLoginName, transferAmount);

        assertEquals(0, from.getCreditAccountMap().size());
        assertEquals(0, from.getDebitAccountMap().size());

        assertEquals(0, to.getCreditAccountMap().size());
        assertEquals(0, to.getDebitAccountMap().size());

        assertEquals(valueOf(0), from.getAccountBalance());
        assertEquals(valueOf(60), to.getAccountBalance());

        assertEquals(1, transactions.size());
        assertEquals(new Transaction(fromLoginName, toLoginName, transferAmount), transactions.get(0));
    }

    @Test
    void testTransfer_DepositLessThanTransfer() throws BankServiceException {
        String toLoginName = "another_user";
        Customer to = bankService.login(toLoginName);
        bankService.logout();

        String fromLoginName = "user";
        Customer from = bankService.login(fromLoginName);
        bankService.deposit(valueOf(60));

        List<Transaction> transactions = bankService.transfer(toLoginName, valueOf(100));

        assertEquals(1, from.getCreditAccountMap().size());
        assertEquals(0, from.getDebitAccountMap().size());
        Account creditAccount = from.getCreditAccountMap().get(to);
        assertNotNull(creditAccount);
        assertEquals(40, creditAccount.getBalance().doubleValue());

        assertEquals(0, to.getCreditAccountMap().size());
        assertEquals(1, to.getDebitAccountMap().size());
        Account debitAccount = to.getDebitAccountMap().get(from);
        assertNotNull(debitAccount);
        assertEquals(40, debitAccount.getBalance().doubleValue());

        assertEquals(valueOf(0), from.getAccountBalance());
        assertEquals(valueOf(60), to.getAccountBalance());

        assertEquals(1, transactions.size());
        assertEquals(new Transaction(fromLoginName, toLoginName, valueOf(60)), transactions.get(0));
    }

    @Test
    void testTransfer_SingleCreditor_AmountGreaterThanDebt() throws BankServiceException {
        String fromLoginName = "user";
        Customer from = bankService.login(fromLoginName);
        bankService.logout();

        String toLoginName = "another_user";
        Customer to = bankService.login(toLoginName);
        bankService.transfer(fromLoginName, valueOf(60));
        bankService.logout();

        bankService.login(fromLoginName);
        bankService.deposit(valueOf(100));

        List<Transaction> transactions = bankService.transfer(toLoginName, valueOf(40));

        assertEquals(0, from.getCreditAccountMap().size());
        assertEquals(1, from.getDebitAccountMap().size());
        Account debitAccount = from.getDebitAccountMap().get(to);
        assertNotNull(debitAccount);
        assertEquals(20, debitAccount.getBalance().doubleValue());

        assertEquals(1, to.getCreditAccountMap().size());
        assertEquals(0, to.getDebitAccountMap().size());
        Account creditAccount = to.getCreditAccountMap().get(from);
        assertNotNull(creditAccount);
        assertEquals(20, creditAccount.getBalance().doubleValue());

        assertEquals(valueOf(100), from.getAccountBalance());
        assertEquals(valueOf(0), to.getAccountBalance());

        assertEquals(0, transactions.size());
    }
}
