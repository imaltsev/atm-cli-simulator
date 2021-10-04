package dev.maltsev.atm.command;

import dev.maltsev.atm.domain.Customer;
import dev.maltsev.atm.service.exceptions.BankServiceException;
import dev.maltsev.atm.service.BankService;
import org.jetbrains.annotations.NotNull;


public class LoginCommand extends AbstractCommand {

    public LoginCommand(@NotNull CommandArguments commandArguments) {
        super(commandArguments);
    }

    @Override
    @NotNull
    public String execute(BankService service) throws BankServiceException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Wrong argument count.\n Correct command format 'login [login_name]'");
        }

        String loginName = args[0];
        Customer customer = service.login(loginName);

        return new StringBuilder()
                .append("Hello, ")
                .append(customer.getLoginName())
                .append("!")
                .append("\n")
                .append(printBalanceStatement(customer))
                .append(printCreditStatement(customer))
                .append(printDebitStatement(customer))
                .toString();
    }
}
