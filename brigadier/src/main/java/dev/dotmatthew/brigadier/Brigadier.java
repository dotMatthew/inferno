package dev.dotmatthew.brigadier;

import dev.dotmatthew.brigadier.command.Command;
import dev.dotmatthew.brigadier.command.CommandHolder;
import dev.dotmatthew.brigadier.command.Parent;
import dev.dotmatthew.brigadier.exceptions.MethodIsNoCommandException;
import dev.dotmatthew.brigadier.exceptions.NoParentCommandException;
import dev.dotmatthew.brigadier.exceptions.RegisterCommandException;
import dev.dotmatthew.brigadier.exceptions.TooManyParentCommandsException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 17.05.21
 */

public class Brigadier {

    private static final List<CommandHolder> commands = new ArrayList<>();
    private static Brigadier instance;

    public static Brigadier getInstance() {
        if(instance == null) {
            instance = new Brigadier();
        }
        return instance;
    }

    public void registerCommand(final @NotNull Class<?> clazz, final @NotNull Object instance) {
        if(clazz.getName() != instance.getClass().getName()) {
            throw new RegisterCommandException("The Class is not the same as the object instance!");
        }

        final List<Method> declaredMethods = Arrays.asList(clazz.getDeclaredMethods());

        final int parentCommands = (int) declaredMethods.stream().filter(method -> method.isAnnotationPresent(Parent.class)).count();

        if(parentCommands < 1) {
            throw new NoParentCommandException("In the class was no parent command found! (" + clazz.getName() + ")");
        }

        if(parentCommands > 1) {
            throw new TooManyParentCommandsException("In the class are too many parent commands! (" + clazz.getName() + ")");
        }

        final Method parentMethod = declaredMethods.stream().findFirst().filter(m -> m.isAnnotationPresent(Parent.class)).get();

        if(!parentMethod.isAnnotationPresent(Command.class)) {
            throw new MethodIsNoCommandException("The parent method has no command annotation");
        }

        final Command parentCommand = parentMethod.getAnnotation(Command.class);



    }

    private void unregisterCommand() {

    }

    private void unregisterCommands() {

    }

    private void executeCommand() {

    }

}
