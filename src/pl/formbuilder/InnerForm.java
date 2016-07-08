package pl.formbuilder;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;

/**
 * @author radek
 */
@Retention(RUNTIME)
@Repeatable(InnerForms.class)
public @interface InnerForm {

	FormName formName() default FormName.NONE;

	FormName innerFormName() default FormName.NONE;

	int position() default 1;
}
