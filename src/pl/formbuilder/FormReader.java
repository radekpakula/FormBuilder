package pl.formbuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author radek
 */
public class FormReader {

	public static List<FormObject> readForm(Class<?> clazz, FormName formName) {
		return readForm(clazz.getDeclaredFields(), formName, false);
	}

	public static List<FormObject> readForm(Class<?> clazz, boolean withHidden) {
		return readForm(clazz.getDeclaredFields(), null, withHidden);
	}

	public static List<FormObject> readForm(Field[] fields, boolean withHidden) {
		return readForm(fields, null, withHidden);
	}

	public static List<FormObject> readForm(Field[] fields, FormName formName, boolean withHidden) {
		List<FormObject> formData = new ArrayList<FormObject>();
		for (Field field : fields) {
			FormField[] f = field.getAnnotationsByType(FormField.class);
			Name aName = field.getAnnotation(Name.class);
			Nullable nl = field.getAnnotation(Nullable.class);
			Regex rg = field.getAnnotation(Regex.class);
			if (f.length > 0) {
				for (FormField ff : f) {
					if (formName == null) {
						if (ff.formName().equals(FormName.NONE)) {
							formData.add(addField(field, ff, aName, nl, rg));
							break;
						}
					} else if (formName.equals(ff.formName()) && (ff.display() || withHidden)) {
						formData.add(addField(field, ff, aName, nl, rg));
						break;
					}
				}

			} else {
				InnerForm[] ins = field.getAnnotationsByType(InnerForm.class);
				if (ins.length > 0) {
					for (InnerForm in : ins) {
						if (formName == null) {
							if (in.formName().equals(FormName.NONE)) {
								buildInForm(formName, withHidden, formData, field, in);
							}
						} else {
							if (formName.equals(in.formName())) {
								buildInForm(formName, withHidden, formData, field, in);
							}
						}
					}
				}
			}
		}
		formData.sort(new Comparator<FormObject>() {
			@Override
			public int compare(FormObject o1, FormObject o2) {
				return o1.getPosition() - o2.getPosition();
			}
		});
		return formData;
	}

	private static void buildInForm(FormName formName, boolean withHidden, List<FormObject> formData, Field field,
			InnerForm in) {
		List<FormObject> inner = readForm(field.getType().getDeclaredFields(), in.innerFormName(), withHidden);
		FormObject object = new FormObject();
		object.setFieldName(field.getName());
		object.setInnerForm(inner);
		object.setPosition(in.position());
		formData.add(object);
	}

	private static FormObject addField(Field field, FormField f, Name aName, Nullable nl, Regex rg) {
		String caption = (aName != null ? aName.value() : f.name());
		caption=caption.isEmpty() ? f.value() : caption;
		caption=caption.isEmpty() ? field.getName() : caption;
		int position = f.position();
		FormObject formObject = new FormObject(field.getName(), caption, position, f.disable(), f.display(), 
				f.type(),f.componentClass(),f.width());
		if (nl != null) {
			formObject.setNullable(nl.value());
			formObject.setNullMsg(nl.message());
		}
		if (rg != null) {
			formObject.setRegex(rg.regex());
			formObject.setRegexMsg(rg.message().isEmpty() ? "not set" : rg.message());
		}
		return formObject;
	}

	public static List<String> readFieldName(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		List<String> fieldsName = new ArrayList<String>();
		for (Field field : fields) {
			if (field.getAnnotation(FormField.class) != null) {
				fieldsName.add(field.getName());
			} else if (field.getAnnotation(InnerForm.class) != null) {
				List<String> fs = readFieldName(field.getType());
				for (String s : fs) {
					fieldsName.add(field.getName() + "." + s);
				}
			}
		}
		return fieldsName;
	}

}
