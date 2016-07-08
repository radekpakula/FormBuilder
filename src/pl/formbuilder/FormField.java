package pl.formbuilder;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;

/**
 * @author radek
 */
@Retention(RUNTIME)
@Repeatable(FormFields.class)
public @interface FormField {

	/**
	 * Display field name name=value
	 * @return
	 */
	String name() default "";
	String value() default "";

	/**
	 * Form name. One field can belong to many form
	 * @return
	 */
	FormName formName() default FormName.NONE ;

	/**
	 * Order field
	 * @return
	 */
	int position() default 1;
	
	/**
	 * Display or not. Default true
	 * @return
	 */
	boolean display() default true;
	
	/**
	 * Disable or not. Default is false
	 * @return
	 */
	boolean disable() default false;

	/**
	 * Field type for string variable. COuld be TEXTAREA, PASSWORD
	 * @return
	 */
	FieldType type() default FieldType.DEFAULT;
	/**
	 * Extend component class. Must extend com.vaadin.ui.AbstractComponent
	 * @return
	 */
	Class<?> componentClass() default FormField.class;
	/**
	 * Field width. Default auto size
	 * @return
	 */
	int width() default -1;

}
