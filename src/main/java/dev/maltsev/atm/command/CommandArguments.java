package dev.maltsev.atm.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public class CommandArguments {

    private final String[] args;

}
