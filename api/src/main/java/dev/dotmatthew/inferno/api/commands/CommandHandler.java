package dev.dotmatthew.inferno.api.commands;

import dev.dotmatthew.inferno.api.exceptions.NoCommandMethodsException;
import dev.dotmatthew.inferno.api.exceptions.OnlyOneParentException;
import dev.dotmatthew.inferno.api.exceptions.WrongParameterException;
import dev.dotmatthew.inferno.api.parameters.ParameterSet;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 12.05.21
 */

@NoArgsConstructor
public class CommandHandler {

    private final HashMap<String, ClassHolder> parentCommandMethods = new HashMap<>();

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
            if(clazzContainsCommandAnnotationInverted(clazz)) {
                throw new NoCommandMethodsException(
                        "There are no command methods registered in this clazz (" +  clazz.getName() + ")");
            }

            // check if this specific method in the stream has the annotation @Command
            if(!method.isAnnotationPresent(Command.class)) return;

            // checks if the given method has an ParameterSet as argument or something else
            if(hasMethodCorrectParameters(method)) {
                throw new WrongParameterException("The method has wrong argument(s). The only allowed argument is an ParameterSet (" + method.getName() + ")");
            }

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
            if(clazzContainsCommandAnnotationInverted(clazz)) {
                throw new NoCommandMethodsException(
                        "There are no command methods registered in this clazz (" +  clazz.getName() + ")");
            }

            if(!method.isAnnotationPresent(Command.class)) return;

            // checks if the given method has an ParameterSet as argument or something else
            if(hasMethodCorrectParameters(method)) {
                throw new WrongParameterException("The method has wrong argument(s). The only allowed argument is an ParameterSet (" + method.getName() + ")");
            }

            final Command command = method.getAnnotation(Command.class);

            if(command.parent().isBlank()) {
                unregisterParentCommand(clazz, method);
            } else {
                final String parentLabel = command.parent();
                unregisterSubCommand(parentLabel, command);
            }

        });

    }

    /**
     *
     * Executes a command from the cli
     *
     * @param command the whole line which was written to the cli
     */
    public void executeCommand(final @NotNull String command) {

        final String[] commandArray = command.split(" ");
        final String parentCommand = commandArray[0];

        AtomicReference<ClassHolder> parentCommandHolder = new AtomicReference<>();
        AtomicReference<ClassHolder> subCommandHolder = new AtomicReference<>();
        AtomicBoolean hasSubCommands = new AtomicBoolean(false);

        parentCommandMethods.forEach((parenLabel, classHolder) -> {
            if(!classHolder.getLabel().equalsIgnoreCase(parentCommand)) return;
            hasSubCommands.set(!classHolder.getCommandList().isEmpty());
            parentCommandHolder.set(classHolder);
        });

        if(hasSubCommands.get()) {
            final String subCommand = commandArray[1];

            parentCommandHolder.get().getCommandList().forEach(classHolder -> {
                if(!classHolder.getLabel().equalsIgnoreCase(subCommand)) return;
                subCommandHolder.set(classHolder);
            });

            final Method method = subCommandHolder.get().getMethod();

            try {
                Method m = Class.forName(method.getDeclaringClass().getName()).getDeclaredMethod(method.getName());
                m.invoke(null, new ParameterSet());
            } catch (final @NotNull Exception exception) {
                exception.printStackTrace();
            }

        } else {
            // todo work with the arguments
            return;
        }

    }

    /* --- internal methods --- */

    private void registerParentCommand(final @NotNull Class<?> clazz, final @NotNull Method method) {

        final Command command = method.getAnnotation(Command.class);

        // check if there is another parent command in the same class
        parentCommandMethods.forEach((parentLabel, ch) -> {
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

        });
        final ClassHolder holder = ClassHolder.builder()
                .clazz(clazz)
                .label(command.label())
                .method(method)
                .commandList(new ArrayList<>())
                .command(command)
                .build();

        this.parentCommandMethods.put(command.label(),holder);

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

        final ClassHolder parentClassHolder = parentCommandMethods.get(parentCommandLabel);
        parentClassHolder.getCommandList().add(holder);
        this.parentCommandMethods.put(parentCommandLabel, parentClassHolder);

    }

    private void unregisterParentCommand(final @NotNull Class<?> clazz, final @NotNull Method method) {
        parentCommandMethods.forEach((parentLabel, classHolder) -> {
            if(classHolder.getClazz() != clazz) return;
            if(classHolder.getMethod() != method) return;
            parentCommandMethods.remove(parentLabel);
        });
    }

    private void unregisterSubCommand(final @NotNull String parentLabel, final @NotNull Command command) {
        parentCommandMethods.forEach((parentLabelId, parentClassHolder) -> {
            if(!parentClassHolder.getLabel().equalsIgnoreCase(parentLabel)) return;

            final Iterator<ClassHolder> iterator = parentClassHolder.getCommandList().iterator();

            while(iterator.hasNext()) {
                if(iterator.next().getCommand() != command) continue;
                iterator.remove();
            }

        });
    }

    private boolean clazzContainsCommandAnnotationInverted(final @NotNull Class<?> clazz) {
        final AtomicInteger annotationCounter = new AtomicInteger();
        Arrays.stream(clazz.getDeclaredMethods()).forEach(m -> {
            if(m.isAnnotationPresent(Command.class)) {
                annotationCounter.getAndIncrement();
            }
        });

        // if there is no annotation we will return false
        return annotationCounter.get() == 0;
    }

    private void removeCommand(final @NotNull ClassHolder holder, final @NotNull ClassHolder classHolder) {
        if(holder.getCommandList().isEmpty()) return;
        holder.getCommandList().remove(classHolder);
    }

    private boolean hasMethodCorrectParameters(final @NotNull Method method) {
        if(method.getParameterCount() != 1) return true;
        final Parameter[] parameters = method.getParameters();
        return !parameters[0].getType().equals(ParameterSet.class);
    }

}
