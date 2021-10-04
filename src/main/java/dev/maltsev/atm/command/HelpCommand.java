package dev.maltsev.atm.command;

import dev.maltsev.atm.service.BankService;
import org.jetbrains.annotations.NotNull;


public class HelpCommand extends AbstractCommand {

    public HelpCommand(@NotNull CommandArguments commandArguments) {
        super(commandArguments);
    }

    @Override
    @NotNull
    public String execute(BankService service) {
        if (args.length != 0) {
            throw new IllegalArgumentException("Wrong argument count.\n Correct command format 'help'");
        }
        return "\nlogin [name] - authenticates customer to system with provided login name and create it if not exist\n" +
                "logout - performs logout current authenticated customer\n" +
                "deposit [amount] - deposits given amount of money to current authenticated customer's account\n" +
                "withdraw [amount] - withdraws given amount of money from current authenticated customer's account\n" +
                "transfer [target] [amount] - transfers given amount of money from current authenticated customer's " +
                "account to the given customer's account\n" +
                "exit - stops the program\n" +
                "help - prints help\n";
    }
}
