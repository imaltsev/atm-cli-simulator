package dev.maltsev.atm.command;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class CommandFactoryTests {

    private CommandFactory commandFactory = new CommandFactory();

    @Test
    public void testCreateCommand_LowerCase_Ok() {
        Command factory = commandFactory.create("login", "username");
        Assertions.assertNotNull(factory);
    }

    @Test
    public void testCreateCommand_UpperCase_Exception() {
        assertThrows(IllegalArgumentException.class, () -> commandFactory.create("LOGIN", "username"));
    }

    @Test
    public void testCreateCommand_UnknownCommand_Exception() {
        assertThrows(IllegalArgumentException.class, () -> commandFactory.create("hello", "world!"));
    }
}
