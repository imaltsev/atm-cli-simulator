package dev.maltsev.atm.command;

import dev.maltsev.atm.domain.Customer;
import dev.maltsev.atm.service.BankService;
import dev.maltsev.atm.service.exceptions.BankServiceException;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;


public class WithdrawCommand extends AbstractCommand {

    public WithdrawCommand(@NotNull CommandArguments commandArguments) {
        super(commandArguments);
    }

    @Override
    @NotNull
    public String execute(BankService service) throws BankServiceException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Wrong argument count.\n Correct command format 'withdraw [amount]'");
        }
        BigDecimal amount = new BigDecimal(args[0]);
        service.withdraw(amount);
        Customer customer = service.getCurrentCustomer();
        return new StringBuilder()
                .append(printBalanceStatement(customer))
                .append(printCreditStatement(customer))
                .append(printDebitStatement(customer))
                .toString();
    }
}
