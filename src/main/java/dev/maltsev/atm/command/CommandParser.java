package dev.maltsev.atm.command;

import com.google.common.base.Splitter;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;


public class CommandParser {

    private final Scanner scanner;

    private final CommandFactory commandFactory;

    public CommandParser(InputStream in, CommandFactory commandFactory) {
        this.scanner = new Scanner(in);
        this.commandFactory = commandFactory;
    }

    public boolean hasNext() {
        return scanner.hasNextLine();
    }

    @NotNull
    public Optional<Command> next() {
        String[] tokens =
                Splitter.on(" ")
                        .trimResults()
                        .omitEmptyStrings()
                        .splitToList(scanner.nextLine())
                        .toArray(new String[0]);

        if (tokens.length == 0) {
            return Optional.empty();
        }

        return Optional.of(commandFactory.create(tokens));
    }
}
