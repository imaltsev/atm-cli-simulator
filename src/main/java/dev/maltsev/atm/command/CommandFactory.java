package dev.maltsev.atm.command;

import com.google.common.collect.ImmutableMap;
import dev.maltsev.atm.util.Loggable;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;


@Component
public class CommandFactory implements Loggable {

    private final ImmutableMap<String, Class<? extends Command>> commandMap =
            new ImmutableMap.Builder<String, Class<? extends Command>>()
                    .put("login", LoginCommand.class)
                    .put("deposit", DepositCommand.class)
                    .put("withdraw", WithdrawCommand.class)
                    .put("transfer", TransferCommand.class)
                    .put("logout", LogoutCommand.class)
                    .put("exit", ExitCommand.class)
                    .put("help", HelpCommand.class)
                    .build();

    @NotNull
    public Command create(@NotNull String... args) {
        try {
            String commandName = args[0];
            String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
            Class<? extends Command> commandClass = commandMap.get(commandName);
            if (commandClass == null) {
                throw new IllegalArgumentException("Unknown command '" + commandName + "'");
            }
            Constructor<? extends Command> constructor = commandClass.getConstructor(CommandArguments.class);
            return constructor.newInstance(new CommandArguments(commandArgs));
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
