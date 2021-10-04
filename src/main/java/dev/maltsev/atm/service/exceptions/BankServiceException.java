package dev.maltsev.atm.service.exceptions;

public class BankServiceException extends Exception {

    public BankServiceException(String message) {
        super(message);
    }

    public BankServiceException(Exception e) {
        super(e);
    }
}
