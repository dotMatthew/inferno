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

    private final List<ClassHolder> parentCommandMethods = new ArrayList<>();

    /**
     *
     * This methods register all commands at the start of the server
     * Only call at the beginning
     *
     * @param clazz The class to check for methods
     */
    public void registerCommand(final @NotNull Class<?> clazz) {

        // iterates over all methods of this class
        Arrays.stream(clazz.getDeclaredMethods()).forEach(method -> {
            // check if one of this methods has the @Command annotation
            if(!clazzContainsCommandAnnotation(clazz)) {
                throw new NoCommandMethodsException(
                        "There are no command methods registered in this clazz (" +  clazz.getName() + ")");
            }

            // check if this specific method in the stream has the annotation @Command
            if(!method.isAnnotationPresent(Command.class)) return;

            /* check if one of this methods with the @Command Annotation has a Parent Segment or not
             if it has one its a subcommand if not its the parent command */
            if(method.getAnnotation(Command.class).parent().isBlank()) {
                registerParentCommand(clazz, method);
            } else {
                registerSubCommand(clazz, method);
            }
        });

    }

    public void unregisterCommand(final @NotNull Class<?> clazz) {

        // iterates over all methods of this class
        Arrays.stream(clazz.getDeclaredMethods()).forEach(method -> {

            // check if one of this methods has the @Command annotation
            if(!clazzContainsCommandAnnotation(clazz)) {
                throw new NoCommandMethodsException(
                        "There are no command methods registered in this clazz (" +  clazz.getName() + ")");
            }

            if(!method.isAnnotationPresent(Command.class)) return;

            final Command command = method.getAnnotation(Command.class);

            if(command.parent().isBlank()) {
                unregisterParentCommand(clazz, method);
            } else {
                final String parentLabel = command.parent();
                unregisterSubCommand(parentLabel, command);
            }

        });

    }

    private void registerParentCommand(final @NotNull Class<?> clazz, final @NotNull Method method) {

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
                    .command(command)
                    .build();

            this.parentCommandMethods.add(holder);

        });
    }

    private void registerSubCommand(final @NotNull Class<?> clazz, final @NotNull Method method) {

        final Command command = method.getAnnotation(Command.class);
        final String parentCommandLabel = command.parent();

        final ClassHolder holder = ClassHolder
                .builder()
                .label(command.label())
                .clazz(clazz)
                .method(method)
                .command(command)
                .build();

        Optional<ClassHolder> classHolderOptional = parentCommandMethods
                .stream()
                .findFirst()
                .filter(classHolder -> classHolder.getLabel().equalsIgnoreCase(parentCommandLabel));

        classHolderOptional.ifPresent(classHolder -> classHolder.getCommandList().add(holder));

    }

    private void unregisterParentCommand(final @NotNull Class<?> clazz, final @NotNull Method method) {
        parentCommandMethods.forEach(classHolder -> {
            if(classHolder.getClazz() != clazz) return;
            if(classHolder.getMethod() != method) return;
            parentCommandMethods.remove(classHolder);
        });
    }

    private void unregisterSubCommand(final @NotNull String parentLabel, final @NotNull Command command) {
        parentCommandMethods.forEach(parentClassHolder -> {
            if(!parentClassHolder.getLabel().equalsIgnoreCase(parentLabel)) return;

            parentClassHolder.getCommandList().forEach(subCommandClassHolder -> {
                if(subCommandClassHolder.getCommand() != command) return;
                removeCommand(parentClassHolder, subCommandClassHolder);
            });

        });
    }

    private boolean clazzContainsCommandAnnotation(final @NotNull Class<?> clazz) {
        final AtomicInteger annotationCounter = new AtomicInteger();
        Arrays.stream(clazz.getDeclaredMethods()).forEach(m -> {
            if(m.isAnnotationPresent(Command.class)) {
                annotationCounter.getAndIncrement();
            }
        });

        // if there is no annotation we will return false
        return annotationCounter.get() != 0;
    }

    private void removeCommand(final @NotNull ClassHolder holder, final @NotNull ClassHolder classHolder) {
        if(holder.getCommandList().isEmpty()) return;
        holder.getCommandList().remove(classHolder);
    }

}
