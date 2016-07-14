# FormBuilder
Tools addon for Vaadin

Addon use annotation for fast build form.
Provide simple validation.
Can build couple forms basic on single object or related objects.
It supports any vaadin components implement interface AbstractComponent like Native or GroupSelect

Extension use default vaadin itembinder to bind values




# Relase note
1.0.5
  - Add new construct to class FormBuilder with param boolean called superClass.
    This provides creating form fields from parent class.
  - Fixed bug with creating InnerForm in layer deeper than 2

1.0.4:
  -Fixed widgestes compilation bug from version 1.0.3.
  -Use name for component from annotation as primary