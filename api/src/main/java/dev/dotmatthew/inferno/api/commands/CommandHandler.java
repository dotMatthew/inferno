package dev.dotmatthew.inferno.api.commands;

import dev.dotmatthew.inferno.api.exceptions.NoCommandMethodsException;
import dev.dotmatthew.inferno.api.exceptions.OnlyOneParentException;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
     * @param clazz The class to check for methods
     */
    public void registerCommand(final @NotNull Class<?> clazz) {

        final Method[] methods = clazz.getMethods();

        // iterates over all methods of this class
        Arrays.stream(methods).forEach(method -> {
            // check if one of this methods has the @Command annotation
            final AtomicInteger annotationCounter = new AtomicInteger();
            Arrays.stream(clazz.getMethods()).forEach(m -> {
                if(m.isAnnotationPresent(Command.class)) {
                    annotationCounter.getAndIncrement();
                }
            });

            // if there is no annotation it will throw an error
            if(annotationCounter.get() == 0) {
                throw new NoCommandMethodsException(
                        "There are no command methods registered in this clazz (" +  clazz.getName() + ")");
            }

            // check if this specific method in the stream has the annotation @Command
            if(!method.isAnnotationPresent(Command.class)) return;

            /* check if one of this methods with the @Command Annotation has a Parent Segment or not
             if it has one its a subcommand if not its the parent command */
            if(method.getAnnotation(Command.class).parent().isBlank()) {
                registerParentCommand(method, clazz);
            } else {
                registerSubCommand(method, clazz);
            }
        });

    }

    private void registerParentCommand(final @NotNull Method method, final Class<?> clazz) {
        final Command command = method.getAnnotation(Command.class);

        // check if there is another parent command in the same class
        parentCommandMethods.forEach((ch) -> {
            // check if there is already another parent method for this command in the same class
            if(ch.getClazz() == clazz) {
                throw new OnlyOneParentException(
                        "Its not allowed that a class holdes 2 parent commands! (Class where the error is "
                                + clazz.getName()+")");
            }

            // check if there is already another parent method for this command in another class
            if(ch.getLabel().equalsIgnoreCase(command.label())) {
                throw new OnlyOneParentException(
                        "Its not allowed to register 2 methods as a parent for the same command (Classes: " +
                                clazz.getName() + " and " + ch.getClazz().getName()+")");
            }

            final ClassHolder holder = ClassHolder.builder()
                    .clazz(clazz)
                    .label(command.label())
                    .method(method)
                    .build();

            this.parentCommandMethods.add(holder);

        });
    }

    private void registerSubCommand(final @NotNull Method method, final @NotNull Class<?> clazz) {

        final Command command = method.getAnnotation(Command.class);
        final String parentCommandLabel = command.parent();

        final ClassHolder holder = ClassHolder
                .builder()
                .label(command.label())
                .clazz(clazz)
                .method(method)
                .build();

        this.commandMethods.put(parentCommandLabel, holder);

    }

}
