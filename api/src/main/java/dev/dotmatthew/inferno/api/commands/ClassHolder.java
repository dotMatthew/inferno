package dev.dotmatthew.inferno.api.commands;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 12.05.21
 */

@Data
@Builder
public class ClassHolder {

    private Class<?> clazz;

    private String label;

    private Method method;

}
