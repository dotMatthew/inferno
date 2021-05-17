package dev.dotmatthew.brigadier;

import dev.dotmatthew.brigadier.command.Command;
import dev.dotmatthew.brigadier.command.Parent;
import dev.dotmatthew.brigadier.parameter.ParameterSet;

import java.util.Arrays;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 17.05.21
 */

public class SumCommand {

    @Parent
    @Command(label = "sum")
    public void sumCommand(ParameterSet parameterSet) {
        System.out.println(Arrays.toString(parameterSet.getArgs()));
    }

}
