package edu.css.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Catalin Dumitru
 * Universitatea Alexandru Ioan Cuza
 */
@Retention(RUNTIME)
@Target(ElementType.FIELD)
public @interface Id {
}
