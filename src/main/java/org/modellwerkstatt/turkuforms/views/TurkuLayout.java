package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.theme.lumo.Lumo;


public class TurkuLayout extends AppLayout {

    private Label sysInfoLabel;
    private Label userInfoLabel;
    private VerticalLayout drawerLayout;

    public TurkuLayout() {

    }

    public void init(String appNavbarTitle) {
        DrawerToggle toggle = new DrawerToggle();
        setPrimarySection(Section.NAVBAR);
        setDrawerOpened(false);

        H1 title = new H1(appNavbarTitle);
        title.setWidthFull();
        title.addClassName("TurkuLayoutNavbarTitle");
        addToNavbar(toggle, title);


        // default drawer
        Div verticalExpansion = new Div();
        verticalExpansion.setHeightFull();

        Button darkToggle = new Button(new Icon(VaadinIcon.ADJUST), event -> {

            ThemeList themeList = UI.getCurrent().getElement().getThemeList();

            if (themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
            } else {
                themeList.add(Lumo.DARK);
            }
        });
        darkToggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        darkToggle.setSizeUndefined();

        Button logout = new Button(new Icon(VaadinIcon.valueOf("POWER_OFF")), event -> { logoutFromApp(); });
        logout.setSizeUndefined();

        userInfoLabel = new Label("-");
        userInfoLabel.setWidthFull();

        sysInfoLabel = new Label("-");

        HorizontalLayout drawerBottom = new HorizontalLayout(userInfoLabel, darkToggle, logout);
        drawerBottom.setWidthFull();
        drawerBottom.setAlignSelf(FlexComponent.Alignment.CENTER, userInfoLabel);

        drawerLayout = new VerticalLayout(verticalExpansion, sysInfoLabel, drawerBottom);
        drawerLayout.setSizeFull();
        addToDrawer(drawerLayout);
    }

    public void setUserInfo(String info) {
        userInfoLabel.setText(info);
    }

    public void setSysInfo(String info){
        sysInfoLabel.setText(info);
    }

    public void addMainMenu(){
        MenuBar bar = new MenuBar();
        bar.setWidthFull();
        bar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);


        MenuItem start = bar.addItem(new Icon(VaadinIcon.CHEVRON_DOWN));
        start.add(new Text("Start"));

        MenuItem extras = bar.addItem(new Icon(VaadinIcon.CHEVRON_DOWN));
        extras.add(new Text("Extras"));

        MenuItem help = bar.addItem(new Icon(VaadinIcon.CHEVRON_DOWN));
        help.add(new Text("Help"));

        SubMenu startSubMenu = start.getSubMenu();
        startSubMenu.addItem("Command 1");
        startSubMenu.addItem("Quit");

        addToNavbar(bar);
    }

    public void addDraweMenu(){
        Button l1 = new Button("Link 1 Command");
        Button l2 = new Button("Dark Mode", event -> {

            ThemeList themeList = UI.getCurrent().getElement().getThemeList();

            if (themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
            } else {
                themeList.add(Lumo.DARK);
            }
        });

        l1.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        l1.setWidthFull();

        l2.setWidthFull();
        l2.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        drawerLayout.addComponentAtIndex(0,l1);
        drawerLayout.addComponentAtIndex(1,l2);
    }


    private void buildMainMenu(){


    }

    private void logoutFromApp() {

    }


}