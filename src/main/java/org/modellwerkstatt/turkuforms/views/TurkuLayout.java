package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.Lumo;
import org.modellwerkstatt.dataux.runtime.genspecifications.MenuActionGlue;
import org.modellwerkstatt.dataux.runtime.genspecifications.MenuSub;
import org.modellwerkstatt.turkuforms.app.ITurkuFactory;
import org.modellwerkstatt.turkuforms.util.TurkuMenuItemGlue;
import org.modellwerkstatt.turkuforms.util.Workarounds;


import java.util.List;


public class TurkuLayout extends AppLayout {

    private Label sysInfoLabel;
    private Label userInfoLabel;
    private VerticalLayout drawerLayout;
    private H1 navbarTitle;
    private MenuBar mainmenuBar;
    protected ITurkuFactory turkuFactory;

    public TurkuLayout() {
    }

    protected void init(ITurkuFactory factory, String appNavbarTitle) {
        turkuFactory = factory;

        DrawerToggle toggle = new DrawerToggle();
        setPrimarySection(Section.NAVBAR);
        setDrawerOpened(false);

        navbarTitle = new H1("");
        navbarTitle.setWidthFull();
        navbarTitle.addClassName("TurkuLayoutNavbarTitle");
        addToNavbar(toggle, navbarTitle);


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

        Button logout = new Button(new Icon(VaadinIcon.POWER_OFF), event -> { exitRequestedFromMenu(); });
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

    protected void setNavbarTitle(String title) {
        navbarTitle.setText(title);
    }

    protected void setUserInfo(String info) {
        userInfoLabel.setText(info);
    }

    protected void setSysInfo(String info){
        sysInfoLabel.setText(info);
    }

    protected SubMenu addToMainMenu(MenuSub menu, String menuName){

        if (mainmenuBar == null) {
            mainmenuBar = new MenuBar();
            mainmenuBar.setOpenOnHover(true);
            mainmenuBar.setWidthFull();
            mainmenuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
            addToNavbar(mainmenuBar);
        }


        MenuItem root = mainmenuBar.addItem(new Icon(VaadinIcon.CHEVRON_DOWN));
        root.add(new Text(menuName));
        SubMenu rootSubMenu = root.getSubMenu();
        return createMainMenuStructure(rootSubMenu, menu.items);
    }

    private SubMenu createMainMenuStructure(SubMenu parent, List<org.modellwerkstatt.dataux.runtime.genspecifications.MenuItem> menuItemList) {

        for (org.modellwerkstatt.dataux.runtime.genspecifications.MenuItem currentItem : menuItemList) {
            if (currentItem instanceof MenuActionGlue) {
                MenuActionGlue glue =  (MenuActionGlue) currentItem;

                MenuItem created = parent.addItem(glue.labelText, event -> {
                    event.getSource().setEnabled(false);
                    glue.startCommand(); });

                glue.attachButton1(new TurkuMenuItemGlue(created));

                Tooltip t = Tooltip.forComponent(created);
                t.setText(Workarounds.mlToolTipText(glue.getToolTip()));


            } else {
                if (currentItem.labelText == null) {
                    // null is separator
                    parent.add(new Hr());

                } else {
                    MenuItem created = parent.addItem(currentItem.labelText);
                    SubMenu createdSub = created.getSubMenu();
                    createMainMenuStructure(createdSub,((MenuSub) currentItem).items);
                }

            }
        }
        return parent;
    }

    protected void addDrawerMenu(List<org.modellwerkstatt.dataux.runtime.genspecifications.MenuItem> menuItemList){
        Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);

        for (org.modellwerkstatt.dataux.runtime.genspecifications.MenuItem currentItem : menuItemList) {
            if (currentItem instanceof MenuActionGlue) {
                MenuActionGlue glue =  (MenuActionGlue) currentItem;

                Icon icon = VaadinIcon.CHEVRON_CIRCLE_RIGHT.create();
                icon.getStyle().set("box-sizing", "border-box")
                        .set("margin-inline-end", "var(--lumo-space-m)")
                        .set("padding", "var(--lumo-space-xs)");
                RouterLink link = new RouterLink();
                link.add(icon, new Span(glue.labelText));
                link.setTabIndex(-1);
                tabs.add(new Tab(link));



            } else {
                if (currentItem.labelText == null) {
                    // null is separator; not used yet ...
                }
            }
        }
        drawerLayout.addComponentAtIndex(0, tabs);
    }


    protected void exitRequestedFromMenu() {

    }


}