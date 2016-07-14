package pl.formbuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
/**
 * 
 * @author radek
 *
 * @param <T>
 */
public class FormBuilder<T> implements Serializable{

	private static final long serialVersionUID = 1L;
	private final Class<T> clazz;
	private String nullRepresentation = "";
	private final BeanFieldGroup<T> binder;
	private List<FormObject> formData;
	private int width=0;
	private Map<String,Component> componentMap;
	private boolean ignoreDisable=false;
	private List<String> disableFields=new ArrayList<String>();
	private List<String> invisibleField=new ArrayList<String>();
	private Unit unit=Unit.PIXELS;
	private boolean monitorState=false;
	private BlurListener blurListener;
	private FocusListener focusListener;
	private boolean activeForm=false;
	private boolean formatCheckbox=true;
	private DefaultFieldGroupFieldFactory fieldFactory;
	private boolean superClass=false;
	public FormBuilder(Class<T> z,BeanFieldGroup<T> binder){
		this.clazz=z;
		this.binder=binder;
	}
	public FormBuilder(Class<T> z,T item){
		this.clazz=z;
		this.binder=new BeanFieldGroup<T>(z);
		if(item==null){
			try {
				item = clazz.getConstructor().newInstance(new Object[]{});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		disableFields=new ArrayList<String>();
		binder.setItemDataSource(item);
	}
	public FormBuilder(Class<T> z,T item,boolean superClass){
		this.clazz=z;
		this.binder=new BeanFieldGroup<T>(z);
		this.superClass=superClass;
		if(item==null){
			try {
				item = clazz.getConstructor().newInstance(new Object[]{});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		disableFields=new ArrayList<String>();
		binder.setItemDataSource(item);
	}
	public AbstractLayout buildForm() {
		return buildForm(new FormLayout(),null);
	}
	public AbstractLayout buildForm(AbstractLayout l) {
		return buildForm(l,null);
	}
	public AbstractLayout buildForm(FormName formName) {
		return buildForm(new FormLayout(),formName);
	}
	public AbstractLayout buildForm(AbstractLayout l,FormName formName) {
		if(componentMap==null){
			componentMap=new HashMap<String,Component>();
		}
		if(formData==null){
			formData=new ArrayList<FormObject>();
		}
		List<FormObject> formDataNew = FormReader.readForm(clazz,formName,superClass);
		iterateOverFormObject(l, formDataNew,"");
		formData.addAll(formDataNew);
		refreshDisableField();
		refreshInvisibleField();
		return l;
	}
	
	private void iterateOverFormObject(AbstractLayout l, List<FormObject> formDataNew,String prefix) {
		for(FormObject ff : formDataNew){
			if(ff.getInnerForm()==null){
				if(!ff.isVisible()){
					continue;
				}
				buildSingleComponent(l, ff,prefix);
			}else if(!ff.getInnerForm().isEmpty()){
				iterateOverFormObject(l,ff.getInnerForm(),prefix+ff.getFieldName()+".");
			}
		}
	}
	private Field<?> buildSingleComponent(AbstractLayout l, FormObject ff,String prefix) {
		Field<?> c=null;
		if(AbstractComponent.class.isAssignableFrom(ff.getComponentClass())){
			try {
				c = (Field<?>) ff.getComponentClass().newInstance();
				binder.bind(c,prefix+ff.getFieldName());
			} catch (Exception e) {
				e.printStackTrace();
				c = createDefaultField(ff, prefix);
			}
		}else{
			c = createDefaultField(ff, prefix);
		}
		c.setCaption(ff.getFieldName());
		if(ff.getWidth()>0){
			c.setWidth(ff.getWidth(),Unit.PIXELS);
		}
		if(nullRepresentation!=null){
			setIfNullRepresentation(c);
		}
		if(!ignoreDisable && ff.isDisable()){
			disableFields.add(prefix+ff.getFieldName());
		}
		if(width!=0){
			if(c instanceof CheckBox){
				if(formatCheckbox)
				c.setWidth(width,unit);
			}else{
				c.setWidth(width,unit);
			}
		}
		if(ff.getNullable()!=null){
			c.addValidator(new NullValidator(ff.getNullMsg(),ff.getNullable()));
		}
		if(ff.getRegex()!=null){
			c.addValidator(new RegexpValidator(ff.getRegex(),ff.getRegexMsg()));
		}
		componentMap.put(prefix+ff.getFieldName(), c);
		l.addComponent(c);
		if(monitorState){
			addMonitorListener(c);
		}
		return c;
	}
	private Field<?> createDefaultField(FormObject ff, String prefix) throws IllegalArgumentException{
		Field<?> c;
		if(ff.getType().equals(FieldType.TEXTAREA)){
			c = binder.buildAndBind(ff.getCaption(),prefix+ff.getFieldName(),TextArea.class);
		}else if(ff.getType().equals(FieldType.PASSWORD)){
			c = binder.buildAndBind(ff.getCaption(),prefix+ff.getFieldName(),PasswordField.class);
		}else{
			c = binder.buildAndBind(ff.getCaption(),prefix+ff.getFieldName());
		}
		return c;
	}

	private void addMonitorListener(Field<?> c) {
		if(c instanceof TextField){
			((TextField) c).addBlurListener(blurListener());
			((TextField) c).addFocusListener(focusListener());
		}
		
	}
	private FocusListener focusListener() {
		if(focusListener==null){
			focusListener=new FocusListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void focus(FocusEvent event) {
					activeForm=true;
				}
			};
		}
		return focusListener;
	}
	private BlurListener blurListener() {
		if(blurListener==null){
			blurListener=new BlurListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void blur(BlurEvent event) {
					activeForm=false;
				}
			};
		}
		return blurListener;
	}
	private void refreshInvisibleField() {
		for(String fieldName : invisibleField){
			Field<?> ff =binder.getField(fieldName);
			if(ff!=null){
				ff.setVisible(false);
			}
		}
		
	}
	public Field<?> bind(Field<?> field , Object propertyId){
		getBinder().bind(field, propertyId);
		if(width!=0){
			field.setWidth(width,unit);
		}
		return field;
	}
	public Field<?> bind(Field<?> field , Object propertyId, FormLayout fl,int position) {
		Field<?> f = this.bind(field, propertyId);
		fl.addComponent(f,position);
		return f;
	}
	public Field<?> bind(Field<?> field , Object propertyId, FormLayout fl) {
		Field<?> f = this.bind(field, propertyId);
		fl.addComponent(f);
		return f;
	}
	@SuppressWarnings({ "rawtypes", "hiding" })
	public <T extends Field> T buildAndBind(String caption, Object propertyId, Class<T> fieldType) {
		T t = getBinder().buildAndBind(caption,propertyId, fieldType);
		if(width!=0){
			t.setWidth(width,unit);
		}
		setIfNullRepresentation(t);
		return t;
	}
	private void setIfNullRepresentation(Field<?> c) {
		if(c instanceof TextField){
			((TextField) c).setNullRepresentation("");
		}
		if(c instanceof TextArea){
			((TextArea) c).setNullRepresentation("");
		}
	}
	public String getNullRepresentation() {
		return nullRepresentation;
	}
	public void setNullRepresentation(String nullRepresentation) {
		this.nullRepresentation = nullRepresentation;
	}
	public BeanFieldGroup<T> getBinder() {
		return binder;
	}
	public boolean isValid(){
		return binder.isValid();
	}
	public boolean validAndCommit(){
		boolean s=isValid();
		if(s){
			try {
				commit();
			} catch (CommitException e) {
				e.printStackTrace();
				return false;
			}
		}
		return s;
	}
	public void commit() throws CommitException{
		binder.commit();
		refreshDisableField();
		refreshInvisibleField();
	}
	private void refreshDisableField() {
		for(String fieldName : disableFields){
			Field<?> ff =binder.getField(fieldName);
			if(ff!=null){
				ff.setReadOnly(true);
				ff.setStyleName("disable-field");
				if(ff instanceof TextField){
					TextField d = (TextField)ff;
					d.setDescription(d.getValue());
				}
			}
		}
	}
	/**
	 * Cofa zmiany formularza
	 * Odświerza pola formularza
	 */
	public void refreshForm(){
		binder.discard();
		refreshDisableField();
		refreshInvisibleField();
	}
	public void setItemDataSource(T t){
		binder.setItemDataSource(t);
		refreshDisableField();
		refreshInvisibleField();
	}
	public T getItemDataSource(){
		return binder.getItemDataSource().getBean();
	}
	/**
	 * Ustawia szerokość dla wszystkoch komponentów tworzonych na formularzu
	 * Parametr musi być ustawiony przed wywołaniem metody build
	 * @param i
	 * @return
	 */
	public FormBuilder<T> setFieldWidth(int i) {
		this.width=i;
		this.unit=Unit.PIXELS;
		return this;
	}
	public FormBuilder<T> setFieldWidth(int i, Unit unit) {
		this.width=i;
		this.unit=unit;
		return this;
	}
	/**
	 * Ustawia szerokość dla wybranego komponentu na formularzu
	 * @param i - szerokośc w px
	 * @param fieldId - id pola
	 */
	public void setWidth(int i, String fieldId) {
		getFieldById(fieldId).setWidth(i,unit);
	}
	public Field<?> getField(Object propertyId){
		return binder.getField(propertyId);
	}
	private Component getFieldById(String fieldId) {
		if(componentMap==null){
			throw new NullPointerException("Najpierw należy zbudowac formularz za pomoca metody buildForm()");
		}
		if(componentMap.get(fieldId)==null){
			throw new NullPointerException("Brak pola o podanym id");
		}
		return componentMap.get(fieldId);
	}
	/**
	 * Ustawia wysokość dla wybranego komponentu na formularzu
	 * @param i - wysokość w px
	 * @param fieldId - id pola
	 */
	public void setHeight(int i, String fieldId) {
		getFieldById(fieldId).setHeight(i,Unit.PIXELS);
	}
	/**
	 * Ignoruje parametry blokujący pole formularza i umożliwia edycję wszystkich pól
	 * @return
	 */
	public FormBuilder<T> ignoreDisable() {
		this.ignoreDisable=true;
		return this;
	}
	/**
	 * Dodaj doatkowe pola które mają być zablokowane na formularzu
	 * @param list
	 */
	public void setDisableField(List<String> list) {
		if(list==null){
			list=new ArrayList<String>();
		}
		disableFields=list;
	}
	public void setInvisibleField(String[] invisible) {
		invisibleField.addAll(Arrays.asList(invisible));
		
	}
	public void addDisableField(String[] disableField) {
		disableFields.addAll(Arrays.asList(disableField));
	}
	public List<String> getFormFieldName() {
		return FormReader.readFieldName(clazz);
	}
	public List<String> getDisableField() {
		return disableFields;
	}
	public void discard() {
		getBinder().discard();
	}
	public boolean isMonitorState() {
		return monitorState;
	}
	public void setMonitorState(boolean monitorState) {
		this.monitorState = monitorState;
	}
	public boolean isActiveForm() {
		return activeForm;
	}
	public void setFormatCheckbox(boolean formatCheckbox) {
		this.formatCheckbox = formatCheckbox;
	}
	public DefaultFieldGroupFieldFactory getFieldFactory() {
		return fieldFactory;
	}
	public void setFieldFactory(DefaultFieldGroupFieldFactory fieldFactory) {
		this.fieldFactory = fieldFactory;
		binder.setFieldFactory(fieldFactory);
	}
}