package com.example.application.UI;

import java.util.List;

import com.example.application.backend.entity.Company;
import com.example.application.backend.entity.Contact;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

public class ContactForm extends FormLayout {
	
	  /**
	 * 
	 */
	private static final long serialVersionUID = -5408748708528938541L;
	
	TextField firstName = new TextField("First name"); 
	TextField lastName = new TextField("Last name");
    EmailField email = new EmailField("Email");
	ComboBox<Contact.Status> status = new ComboBox<>("Status");
	ComboBox<Company> company = new ComboBox<>("Company");
	
	Button save = new Button("Save"); 
	Button delete = new Button("Delete");
	Button close = new Button("Cancel");  
	Binder<Contact> binder = new BeanValidationBinder<>(Contact.class);
	
	private Contact contact;
	
	public ContactForm(List<Company> companies) {
		addClassName("contact-form");
		
		// auto binder
		binder.bindInstanceFields(this);
		
		status.setItems(Contact.Status.values());
		company.setItems(companies);
		
		//display Field CompanyName
		company.setItemLabelGenerator(Company::getName);
		
		add(firstName,
		        lastName,
		        email,
		        company,
		        status,
		        createButtonsLayout());
		
	}

	private Component createButtonsLayout() {
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY); 
	    delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
	    close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

	    save.addClickShortcut(Key.ENTER); 
	    close.addClickShortcut(Key.ESCAPE);
	    
	    save.addClickListener(event -> validateAndSave()); 
	    delete.addClickListener(event -> fireEvent(new DeleteEvent(this, contact))); 
	    close.addClickListener(event -> fireEvent(new CloseEvent(this))); 

	    //Validates the form every time it changes. If it is invalid, it disables the save button to avoid invalid submissions.
	    binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
		return new HorizontalLayout(save, delete, close);
	}
	
	private void validateAndSave() {
		  try {
		    binder.writeBean(contact); 
		    fireEvent(new SaveEvent(this, contact)); 
		  } catch (ValidationException e) {
		    e.printStackTrace();
		  }
		}
	
	// set bidner data of contact
	public void setContact(Contact contact) {
		this.contact= contact;
		binder.setBean(contact);
	}
	

	//Events CLASSes => chaque type d'event on a la source (instance contact form) et l'objet data Contact
	public static abstract class ContactFormEvent extends ComponentEvent<ContactForm> {
		
		private Contact contact;
		
		protected ContactFormEvent(ContactForm source, Contact contact) {
			super(source, false);			
			this.contact = contact;
		}
		
		public Contact getContact() {
			return contact;
		}
		
	}
	

	public static class SaveEvent extends ContactFormEvent {
	  SaveEvent(ContactForm source, Contact contact) {
	    super(source, contact);
	  }
	}
	
	public static class DeleteEvent extends ContactFormEvent {
	  DeleteEvent(ContactForm source, Contact contact) {
	    super(source, contact);
	  }
	
	}
	
	public static class CloseEvent extends ContactFormEvent {
	  CloseEvent(ContactForm source) {
	    super(source, null);
	  }
	}
		 
	//register to those Events
	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {
		return getEventBus().addListener(eventType, listener);
	}
	
}
