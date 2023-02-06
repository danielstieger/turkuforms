package org.modellwerkstatt.turkuforms.app;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.theme.lumo.Lumo;


public class LandingView extends AppLayout {

    public LandingView() {
        DrawerToggle toggle = new DrawerToggle();

        H1 title = new H1("mo DiLaKa 1.8");
        title.setWidthFull();
        title.addClassName("NavbarTitle");


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

        addToNavbar(toggle, title, bar);
        setPrimarySection(Section.NAVBAR);


        VerticalLayout drawer = new VerticalLayout();

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

        drawer.add(l1, l2);

        addToDrawer(drawer);
        setDrawerOpened(false);


        setContent(getTiles());
    }


    private FlexLayout getTiles() {
        FlexLayout tilesGrid = new FlexLayout();
        tilesGrid.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        tilesGrid.addClassName("TilesGrid");
        tilesGrid.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        tilesGrid.setWidthFull();
        tilesGrid.setAlignContent(FlexLayout.ContentAlignment.SPACE_AROUND);

        String st = "_";
        for(int i=0; i < 10; i++) {
            Button tile = new Button("Start Command "+ st + i + " here. ");
            tile.setTooltipText("This is the tooltip for this command.. <br> This is a rather large explanation.");
            tile.setMinHeight("200px");
            tile.setMinWidth("200px");
            tile.addClassName("TileButton");

            tilesGrid.setFlexGrow(0d, tile);
            tilesGrid.setFlexShrink(0d, tile);
            tilesGrid.setFlexBasis("30%", tile);

            st += "_";
            tilesGrid.add(tile);
        }

        return tilesGrid;
    }

    private Tabs getTabs() {
        Tabs tabs = new Tabs();
        tabs.add(createTab(VaadinIcon.DASHBOARD, "Dashboard"),
                createTab(VaadinIcon.CART, "Orders"),
                createTab(VaadinIcon.USER_HEART, "Customers"),
                createTab(VaadinIcon.PACKAGE, "Products"),
                createTab(VaadinIcon.RECORDS, "Documents"),
                createTab(VaadinIcon.LIST, "Tasks"),
                createTab(VaadinIcon.CHART, "Analytics"));
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        return tabs;
    }

    private Tab createTab(VaadinIcon viewIcon, String viewName) {
        Icon icon = viewIcon.create();
        icon.getStyle().set("box-sizing", "border-box")
                .set("margin-inline-end", "var(--lumo-space-m)")
                .set("padding", "var(--lumo-space-xs)");

        RouterLink link = new RouterLink();
        link.add(icon, new Span(viewName));
        // Demo has no routes
        // link.setRoute(viewClass.java);
        link.setTabIndex(-1);

        return new Tab(link);
    }


}