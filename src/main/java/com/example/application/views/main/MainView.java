package com.example.application.views.main;

import java.util.Optional;

import com.example.application.views.DashboardView;
import com.example.application.views.HomeView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;

/**
 * The main view is a top-level placeholder for other views.
 */
@PWA(name = "Vaadin CRM", shortName = "Vaadin CRM", enableInstallPrompt = false)
@JsModule("./styles/shared-styles.js")
@CssImport("./views/shared-styles.css")
public class MainView extends AppLayout {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6845702965780995053L;
	private final Tabs menu;
    private H1 viewTitle;

    public MainView() {
    	viewTitle = new H1();
        menu = createMenu();
        addToNavbar(createHeaderContent());
  
    }

    private Component createHeaderContent() {
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setClassName("header");
        headerLayout.setHeight("80px");
        
        headerLayout.getThemeList().set("dark", true);
        headerLayout.setWidthFull();
        headerLayout.setSpacing(false);
        headerLayout.setAlignItems(Alignment.CENTER);

        headerLayout.add(new H1("Vaadin CRM"));
        headerLayout.add(createMenu());
        return headerLayout;
    }


    private Tabs createMenu() {
        final Tabs tabs = new Tabs();
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.add(createMenuItems());
        tabs.add(LogoutMenuItem());
        return tabs;
    }

    private Component[] createMenuItems() {
        return new Tab[]{createTab("Home", HomeView.class), createTab("Dashboard",DashboardView.class)};
    }
    
    private Component LogoutMenuItem() {
    	Tab logout = new Tab();
        logout.add(new Anchor("logout", "Logout")); 
        return logout;
    }

    private static Tab createTab(String text, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();
        tab.add(new RouterLink(text, navigationTarget));
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }


    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);
        viewTitle.setText(getCurrentPageTitle());
    }

    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren().filter(tab -> ComponentUtil.getData(tab, Class.class).equals(component.getClass()))
                .findFirst().map(Tab.class::cast);
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
