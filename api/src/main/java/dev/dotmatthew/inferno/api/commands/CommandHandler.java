package dev.dotmatthew.inferno.api.commands;

import dev.dotmatthew.inferno.api.exceptions.NoCommandMethodsException;
import dev.dotmatthew.inferno.api.exceptions.OnlyOneParentException;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 12.05.21
 */

@NoArgsConstructor
public class CommandHandler {

    private final HashMap<String, ClassHolder> commandMethods = new HashMap<>();
    private final List<ClassHolder> parentCommandMethods = new ArrayList<>();

    /**
     *
     * This methods register all commands at the start of the server
     * Only call at the beginning
     *
     * @param clazz The clazz to check for methods
     */
    public void registerCommand(final @NotNull Class<?> clazz) {

        final Method[] methods = clazz.getMethods();

        // iterates over all methods of this class
        Arrays.stream(methods).forEach(method -> {
            // check if one of this methods has the @Command annotation
            if(method.isAnnotationPresent(Command.class)) {
                throw new NoCommandMethodsException(
                        "There are no command methods registered in this clazz (" +  clazz.getName() + ")");
            }

            // check if one of this methods with the @Command Annotation has a Parent Segment or not
            // if it has one its a subcommand if not its the parent command
            if(method.getAnnotation(Command.class).parent().isBlank()) {
                // check if there is another parent command in the same class
                parentCommandMethods.forEach((ch) -> {
                    // check if there is already another parent method for this command in the same class
                    if(ch.getClazz() == clazz) {
                        throw new OnlyOneParentException("Its not allowed that a class holdes 2 parent commands! (Class where the error is " + clazz.getName()+")");
                    }

                    // check if there is already another parent method for this command in another class
                    if(ch.getLabel().equalsIgnoreCase(method.getAnnotation(Command.class).label())) {
                        throw new OnlyOneParentException(
                        "Its not allowed to register 2 methods as a parent for the same command (Classes: " +
                         clazz.getName() + " and " + ch.getClazz().getName()+")");
                    }

                    final ClassHolder holder = ClassHolder.builder()
                            .clazz(clazz)
                            .label(method.getAnnotation(Command.class).label())
                            .method(method)
                            .build();

                    this.parentCommandMethods.add(holder);

                });
            }
        });

    }

}
