package pl.formbuilder;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/**
 * @author radek
 */
@Retention(RUNTIME)
public @interface Nullable {

	String message() default "Nie może być puste";

	boolean value() default false;
}
