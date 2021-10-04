package dev.maltsev.atm.command;

import dev.maltsev.atm.domain.Customer;
import dev.maltsev.atm.domain.Transaction;
import dev.maltsev.atm.service.BankService;
import dev.maltsev.atm.service.exceptions.BankServiceException;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;


public class TransferCommand extends AbstractCommand {

    public TransferCommand(@NotNull CommandArguments commandArguments) {
        super(commandArguments);
    }

    @Override
    @NotNull
    public String execute(BankService service) throws BankServiceException {
        if (args.length != 2) {
            throw new IllegalArgumentException(
                    "Wrong argument count.\n Correct command format 'transfer [to_login_name] [amount]'");
        }
        String toLoginName = args[0];
        BigDecimal amount = new BigDecimal(args[1]);
        List<Transaction> transactions = service.transfer(toLoginName, amount);
        Customer customer = service.getCurrentCustomer();

        return new StringBuilder()
                .append(printTransactionStatement(transactions, customer))
                .append(printBalanceStatement(customer))
                .append(printCreditStatement(customer))
                .append(printDebitStatement(customer))
                .toString();
    }
}
