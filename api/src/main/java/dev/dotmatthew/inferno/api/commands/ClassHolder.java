package dev.dotmatthew.inferno.api.commands;

import lombok.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 12.05.21
 */

@Getter
@Setter
@Builder
public class ClassHolder {

    private Class<?> clazz;

    private String label;

    private Method method;

    private ArrayList<ClassHolder> commandList;

    private Command command;

}
