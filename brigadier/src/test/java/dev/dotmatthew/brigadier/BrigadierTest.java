package dev.dotmatthew.brigadier;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 17.05.21
 */

public class BrigadierTest {

    public static void main(String[] args) {

        Brigadier brigadier = new Brigadier();

        brigadier.registerCommand(SumCommand.class, new SumCommand());

        brigadier.executeCommand("sum 1 2 3");

    }

}
