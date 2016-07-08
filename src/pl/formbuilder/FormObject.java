package pl.formbuilder;

import java.util.List;

/**
 * @author radek
 */
public class FormObject {

	public FormObject(String fieldName, String caption, int position, boolean disable, boolean visible,
			FieldType type, Class<?> componentClass,int width) {
		super();
		this.fieldName = fieldName;
		this.caption = caption;
		this.position = position;
		this.disable = disable;
		this.visible = visible;
		this.setType(type);
		this.componentClass=componentClass;
		this.width=width;
	}

	public FormObject() {
	}

	private FieldType type;
	String fieldName;
	String caption;
	private int position;
	private boolean disable;
	private boolean visible;
	private List<FormObject> innerForm;
	private Boolean nullable = null;
	private String nullMsg;
	private String regex;
	private String regexMsg;
	private Class<?> componentClass;
	private int width;
	
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public int compareTo(FormObject o) {
		return this.position - o.position;
	}

	public int hashCode() {
		return position;
	}

	public boolean equals(FormObject o) {

		return o.position == position;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public boolean isDisable() {
		return disable;
	}

	public void setDisable(boolean disable) {
		this.disable = disable;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public List<FormObject> getInnerForm() {
		return innerForm;
	}

	public void setInnerForm(List<FormObject> innerForm) {
		this.innerForm = innerForm;
	}

	public Boolean getNullable() {
		return nullable;
	}

	public void setNullable(Boolean nullable) {
		this.nullable = nullable;
	}

	public String getNullMsg() {
		return nullMsg;
	}

	public void setNullMsg(String nullMsg) {
		this.nullMsg = nullMsg;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getRegexMsg() {
		return regexMsg;
	}

	public void setRegexMsg(String regexMsg) {
		this.regexMsg = regexMsg;
	}

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
	}

	public Class<?> getComponentClass() {
		return componentClass;
	}

	public void setComponentClass(Class<?> componentClass) {
		this.componentClass = componentClass;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

}
