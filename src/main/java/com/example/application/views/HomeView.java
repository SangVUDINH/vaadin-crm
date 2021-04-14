package com.example.application.views;

import com.example.application.UI.ContactForm;
import com.example.application.backend.entity.Company;
import com.example.application.backend.entity.Contact;
import com.example.application.backend.service.CompanyService;
import com.example.application.backend.service.ContactService;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;


@Route(value = "home", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Home")

public class HomeView extends VerticalLayout{

	/**
	 * 
	 */
	private static final long serialVersionUID = 830975840347808805L;

	Grid<Contact> grid = new Grid<>(Contact.class);
	private ContactService contactService;
	private TextField filterText;
	private ContactForm  form;
	
	// spring va autowire
	public HomeView(ContactService contactService, CompanyService companyService) {
		
		addClassName("home-view");
		
		this.contactService= contactService;		
		setSizeFull();
		filterText = new TextField();
		configureGrid();

		
		form = new ContactForm(companyService.findAll());
		//HANDLER for each type of event
		form.addListener(ContactForm.SaveEvent.class, this::saveContact); 
	    form.addListener(ContactForm.DeleteEvent.class, this::deleteContact); 
	    form.addListener(ContactForm.CloseEvent.class, e -> closeEditor());
		
		Div content = new Div(grid,form);
		content.addClassName("content");
		content.setSizeFull();
		
		 add(getToolbar(), content);
		
		closeEditor();
		updateList();
	}
	
	
	private HorizontalLayout getToolbar() { 
	    filterText.setPlaceholder("Filter by name...");
	    filterText.setClearButtonVisible(true);
	    filterText.setValueChangeMode(ValueChangeMode.LAZY);
	    filterText.addValueChangeListener(e -> updateList());

	    Button addContactButton = new Button("Add contact");
	    addContactButton.addClickListener(click -> addContact()); 

	    HorizontalLayout toolbar = new HorizontalLayout(filterText, addContactButton); 
	    toolbar.addClassName("toolbar");
	    return toolbar;
	}
	
	void addContact() {
	    grid.asSingleSelect().clear(); 
	    editContact(new Contact()); 
	}
	
	private void saveContact(ContactForm.SaveEvent event) {
	    contactService.save(event.getContact());
	    updateList();
	    closeEditor();
	}

	private void deleteContact(ContactForm.DeleteEvent event) {
	    contactService.delete(event.getContact());
	    updateList();
	    closeEditor();
	}

	// custom colum to get field of object
	private void configureGrid() {
		grid.addClassName("contact-grid");
		grid.setSizeFull();
		
		grid.removeColumnByKey("company");
		grid.setColumns("firstName","lastName","email","status");
		grid.addColumn(contact->{
			Company company = contact.getCompany();
			return company == null? "-" : company.getName();
		}).setHeader("Company");
		
		// auto width column
		grid.getColumns().forEach(col-> col.setAutoWidth(true));
		
		grid.asSingleSelect().addValueChangeListener(event -> 
	      editContact(event.getValue()));
	}
	
	public void editContact(Contact contact) { 
	    if (contact == null) {
	        closeEditor();
	    } else {
	        form.setContact(contact);
	        form.setVisible(true);
	        addClassName("editing");
	    }
	}

	private void closeEditor() {
	    form.setContact(null);
	    form.setVisible(false);
	    removeClassName("editing");
	}
	
	private void updateList() {
		grid.setItems(contactService.findAll(filterText.getValue()));
	}


}
