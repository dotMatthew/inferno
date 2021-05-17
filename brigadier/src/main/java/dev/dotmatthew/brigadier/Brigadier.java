package dev.dotmatthew.brigadier;

import dev.dotmatthew.brigadier.command.Command;
import dev.dotmatthew.brigadier.command.CommandHolder;
import dev.dotmatthew.brigadier.command.Parent;
import dev.dotmatthew.brigadier.command.SubCommandHolder;
import dev.dotmatthew.brigadier.exceptions.*;
import dev.dotmatthew.brigadier.parameter.ParameterSet;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        if(!clazz.getName().equals(instance.getClass().getName())) {
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

        if(parentMethod.getParameters().length != 1) {
            throw new ParameterException("There is only on argument allowed!");
        }

        if(hasMethodNotCorrectParameters(parentMethod)) {
            throw new ParameterException("The given method parameter is invalid. Only allowed are ParameterSets ("+clazz.getName()+")");
        }

        final Command parentCommand = parentMethod.getAnnotation(Command.class);

        final CommandHolder holder = CommandHolder
                .builder()
                .label(parentCommand.label())
                .aliases(parentCommand.aliases())
                .description(parentCommand.desc())
                .usage(parentCommand.usage())
                .clazz(clazz)
                .instance(instance)
                .method(parentMethod)
                .build();

        declaredMethods.forEach(method -> {
            if(method.isAnnotationPresent(Parent.class)) return;
            if(!method.isAnnotationPresent(Command.class)) return;

            if(method.getParameters().length != 1) {
                throw new ParameterException("There is only on parameter allowed!");
            }

            if(hasMethodNotCorrectParameters(method)) {
                throw new ParameterException("The given method parameter is invalid. Only allowed are ParameterSets ("+clazz.getName()+")");
            }

            final Command subCommand = method.getAnnotation(Command.class);

            final SubCommandHolder subCommandHolder = SubCommandHolder
                    .builder()
                    .parent(holder)
                    .aliases(subCommand.aliases())
                    .description(subCommand.desc())
                    .usage(subCommand.usage())
                    .label(subCommand.label())
                    .method(method)
                    .build();

            holder.addSubCommand(subCommandHolder);

        });

        commands.add(holder);

    }

    public boolean unregisterCommand(final @NotNull String label) {
        final Optional<CommandHolder> holder = commands.stream().findFirst().filter(commandHolder -> commandHolder.getLabel().equalsIgnoreCase(label));
        if(holder.isEmpty()) {
            throw new CommandNotFoundException("There was no command found with this name");
        }
        return commands.remove(holder.get());
    }

    public void unregisterCommands() {
        commands.clear();
    }

    public void executeCommand(final @NotNull String command) {
        final String[] commandArray = command.split(" ");

        final Optional<CommandHolder> holderOptional = commands.stream().findFirst().filter(commandHolder -> commandHolder.getLabel().equalsIgnoreCase(commandArray[0]));

        if(holderOptional.isEmpty()) {
            throw new CommandNotFoundException("There was no command found with that name");
        }

        final CommandHolder holder = holderOptional.get();

        final Optional<SubCommandHolder> optionalSubCommandHolders =
                holder.getSubCommands().stream().findFirst().filter(subCommandHolder -> subCommandHolder.getLabel().equalsIgnoreCase(commandArray[1]));

        if(optionalSubCommandHolders.isEmpty()) {
            try {
                holder.getMethod().invoke(holder.getInstance(), new ParameterSet(Arrays.copyOfRange(commandArray, 1, commandArray.length)));
            } catch (final @NotNull Exception exception) {
                exception.printStackTrace();
            }
        } else {
            final SubCommandHolder subCommandHolder = optionalSubCommandHolders.get();
            try {
                subCommandHolder.getMethod().invoke(holder.getInstance(), new ParameterSet(Arrays.copyOfRange(commandArray, 2, commandArray.length)));
            } catch (final @NotNull Exception exception) {
                exception.printStackTrace();
            }
        }

    }

    public void sayCommands() {
        System.out.println(commands);
    }

    private boolean hasMethodNotCorrectParameters(final @NotNull Method method) {
        if(method.getParameterCount() != 1) return true;
        final Parameter[] parameters = method.getParameters();
        return !parameters[0].getType().equals(ParameterSet.class);
    }

}
