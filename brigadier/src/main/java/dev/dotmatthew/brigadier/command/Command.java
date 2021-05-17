package dev.dotmatthew.brigadier.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 17.05.21
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

    /**
     * @return the name of the command e.g. write
     */
    String label();

    /**
     * @return an Array with the aliases of the label
     */
    String[] aliases() default {""};

    /**
     * @return the Parent Command label
     */
    String parent() default "";

    /**
     * @return a description for a command
     */
    String desc() default "";

    /**
     * @return the usage of a command e.g. write <client> <message>
     */
    String usage() default "";

}
