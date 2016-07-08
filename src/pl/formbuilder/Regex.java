package pl.formbuilder;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/**
 * @author radek
 */
@Retention(RUNTIME)
public @interface Regex {

	String message() default "";

	String regex();
}