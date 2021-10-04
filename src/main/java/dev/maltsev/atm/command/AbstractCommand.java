package dev.maltsev.atm.command;

import org.jetbrains.annotations.NotNull;


public abstract class AbstractCommand implements Command {

    protected final String[] args;

    public AbstractCommand(@NotNull CommandArguments commandArguments) {
        this.args = commandArguments.getArgs();
    }
}
