package dev.dotmatthew.inferno.api.commands;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 12.05.21
 */

@Data
@Builder
@NoArgsConstructor
public class ClassHolder {

    private Class<?> clazz;

    private String label;

    private Method method;

    private List<ClassHolder> commandList;

    private Command command;

}
