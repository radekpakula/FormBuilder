# FormBuilder
Tools addon for Vaadin

Addon use annotation for fast build form.
Provide simple validation.
Can build couple forms basic on single object or related objects.
It supports any vaadin components implement interface AbstractComponent like Native or GroupSelect

Extension use default vaadin itembinder to bind values

# Simple usage
FormBuilder<User> fb = new FormBuilder<User>(User.class,userObject);
content.addComponent(fb.buildForm);

#FormField example usage
	@FormField("My label)
	private String myName;
	
	//this field be build only when call fb.buildForm(FormName.COL1)
	@FormField(value="My label",position=1,width=250,formName=FormName.COL1) 
	private String myName2;
	
	@FormField(value="Password field",type=FieldType.PASSWORD)
	private String password;
	
	@FormField(value="DisableField",disable=true)
	private String disableField;
	
	@Nullable
	@FormField(value="Bank number")
	private String canNotBeEmpty;
	
	@FormField(value="UserGroup select",componentClass=MyComponent.class)
	private Group group;
	
	---Getter and Setter for all fields are required!--
	
#Form field description
	-name() default "";  - field label
	-value() default ""; - field label (value=name)
	-formName() default FormName.NONE ; -form group. One field can have many @FormField
	-position() default 1; - component position in layout
	-display() default true; - determines whether the component is to be created or not
	-disable() default false; - create field whit disable value
	-type() default FieldType.DEFAULT; 
		only for string field. Available value is:
		DEFAULT, TEXTAREA, PASSWORD;
	-componentClass() default FormField.class; - could by any component created by developer. SelectList, combobox etc.
	-width() default -1; component width. Default is auto.

# Relase note
1.0.5
  - Add new construct to class FormBuilder with param boolean called superClass.
    This provides creating form fields from parent class.
  - Fixed bug with creating InnerForm in layer deeper than 2

1.0.4:
  -Fixed widgestes compilation bug from version 1.0.3.
  -Use name for component from annotation as primary