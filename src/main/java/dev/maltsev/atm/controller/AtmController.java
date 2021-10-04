package dev.maltsev.atm.controller;

import dev.maltsev.atm.command.CommandFactory;
import dev.maltsev.atm.util.Loggable;
import dev.maltsev.atm.command.Command;
import dev.maltsev.atm.command.CommandParser;
import dev.maltsev.atm.command.ExitCommand;
import dev.maltsev.atm.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Optional;


/**
 * A "Front controller" pattern implementation
 */
@Controller
public class AtmController implements Loggable {

    private final CommandFactory commandFactory;

    private final BankService service;

    @Autowired
    public AtmController(CommandFactory commandFactory, BankService service) {
        this.commandFactory = commandFactory;
        this.service = service;
    }

    public void doProcess(InputStream in, OutputStream out) {
        logger().info("ATM started successfully");

        PrintStream outStream = new PrintStream(out);
        outStream.println("ATM started. All inputs are case-sensitive. Use 'help' to see available commands.");
        outStream.println();

        for (CommandParser commandParser = new CommandParser(in, commandFactory); ; ) {
            try {
                if (commandParser.hasNext()) {
                    Optional<Command> commandOptional = commandParser.next();

                    if (commandOptional.isPresent()) {
                        Command command = commandOptional.get();
                        String result = command.execute(service);
                        outStream.println(result);

                        if (command instanceof ExitCommand) {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                logger().error("Error occurred: ", e);
                outStream.println("Error occurred: " + e.getMessage());
                outStream.println();
            }
        }
    }
}
