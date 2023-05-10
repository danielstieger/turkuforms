package org.modellwerkstatt.turkuforms.views;

import com.vaadin.experimental.Feature;
import com.vaadin.experimental.FeatureFlags;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.Version;
import com.vaadin.flow.theme.lumo.Lumo;
import org.modellwerkstatt.dataux.runtime.genspecifications.MenuActionGlue;
import org.modellwerkstatt.dataux.runtime.genspecifications.MenuSub;
import org.modellwerkstatt.objectflow.runtime.MoVersion;
import org.modellwerkstatt.turkuforms.app.ITurkuFactory;
import org.modellwerkstatt.turkuforms.util.Defs;
import org.modellwerkstatt.turkuforms.forms.Menu;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.TurkuHasEnabled;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.lang.management.ManagementFactory;
import java.util.List;


abstract public class TurkuAppLayout extends AppLayout implements HasDynamicTitle {

    private Label sysInfoLabel;
    private Label userInfoLabel;
    private VerticalLayout drawerLayout;
    private Div navbarTitle;
    private MenuBar mainmenuBar;
    protected ITurkuFactory turkuFactory;
    private VerticalLayout drawerCommandsLayout;

    public TurkuAppLayout() {
    }

    protected void init(ITurkuFactory factory, String appNavbarTitle) {
        turkuFactory = factory;

        DrawerToggle toggle = new DrawerToggle();
        setPrimarySection(Section.NAVBAR);
        setDrawerOpened(false);

        navbarTitle = new Div();
        navbarTitle.setWidthFull();
        navbarTitle.addClassName("TurkuLayoutNavbarTitle");

        addToNavbar(toggle, navbarTitle);

        Button darkToggle = new Button(Workarounds.createIconWithCollection(factory.translateIconName("mainmenu_adjust")), event -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();

            if (themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
            } else {
                themeList.add(Lumo.DARK);
            }
            this.setDrawerOpened(false);
        });
        darkToggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        darkToggle.setSizeUndefined();

        Button logout = new Button(Workarounds.createIconWithCollection(factory.translateIconName("mainmenu_logout")), event -> {
            this.setDrawerOpened(false);
            exitRequestedFromMenu();
        });
        logout.setSizeUndefined();

        userInfoLabel = new Label("-");
        userInfoLabel.setWidthFull();

        String basicSysInfo = MoVersion.MOWARE_PLUGIN_VERSION + "\n" + Turku.INTERNAL_VERSION + "\n";
        basicSysInfo += "Vaadin Version " + Version.getFullVersion() + "\n";
        basicSysInfo += ManagementFactory.getRuntimeMXBean().getVmVendor() + " " +
                       ManagementFactory.getRuntimeMXBean().getVmName() + " " +
                       ManagementFactory.getRuntimeMXBean().getVmVersion();

        for (Feature f: FeatureFlags.get(VaadinService.getCurrent().getContext()).getFeatures()) {
            if (f.isEnabled()) {
                basicSysInfo += "\nVaadin feature " + f.getTitle();
            }
        }


        sysInfoLabel = new Label("-");
        Tooltip sysInfoTooltip = Tooltip.forComponent(sysInfoLabel);
        sysInfoTooltip.setText(basicSysInfo);
        sysInfoTooltip.setPosition(Tooltip.TooltipPosition.TOP_END);
        sysInfoTooltip.setHideDelay(5000);

        HorizontalLayout drawerBottom = new HorizontalLayout(userInfoLabel, darkToggle, logout);
        drawerBottom.setWidthFull();
        drawerBottom.setAlignSelf(FlexComponent.Alignment.CENTER, userInfoLabel);

        drawerCommandsLayout = new VerticalLayout();
        drawerCommandsLayout.setSizeFull();

        drawerLayout = new VerticalLayout(drawerCommandsLayout, sysInfoLabel, drawerBottom);
        drawerLayout.setSizeFull();
        addToDrawer(drawerLayout);
    }

    protected void setNavbarTitle(String title) { navbarTitle.setText(title); }
    protected String getNavbarTitle() { return navbarTitle.getText(); }

    @Override
    public String getPageTitle() {
        return navbarTitle.getText();
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
            mainmenuBar.setOpenOnHover(false);
            mainmenuBar.setWidthFull();
            mainmenuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
            mainmenuBar.addClassName("TurkuLayoutMenuBar");
            addToNavbar(mainmenuBar);
        }


        MenuItem root = mainmenuBar.addItem(Workarounds.createIconWithCollection(turkuFactory.translateIconName("mainmenu_down")));
        root.add(new Text(menuName));
        SubMenu rootSubMenu = root.getSubMenu();
        return Menu.addMainMenuStructure(turkuFactory, rootSubMenu, menu.items);
    }

    protected void addDrawerMenu(List<org.modellwerkstatt.dataux.runtime.genspecifications.MenuItem> menuItemList){

        for (org.modellwerkstatt.dataux.runtime.genspecifications.MenuItem currentItem : menuItemList) {
            if (currentItem instanceof MenuActionGlue) {
                MenuActionGlue glue =  (MenuActionGlue) currentItem;
                ComponentEventListener<ClickEvent<Button>> execItem = event -> {
                    this.setDrawerOpened(false);
                    glue.startCommand();
                };
                Button btn;

                if (Defs.hasIcon(glue.imageName)) {
                    Component icn = Workarounds.createIconWithCollection(turkuFactory.translateIconName(glue.imageName));
                    btn = new Button(turkuFactory.translateButtonLabel(glue.labelText, glue.public_hotKey), icn, execItem);

                } else {
                    btn = new Button(turkuFactory.translateButtonLabel(glue.labelText, glue.public_hotKey), execItem);

                }

                glue.attachButton1(new TurkuHasEnabled(btn, "Drawer " + glue.labelText));
                btn.setTooltipText(Workarounds.mlToolTipText(glue.getToolTip()));
                btn.setWidthFull();
                // btn.setDisableOnClick(true);
                btn.setClassName("MainwindowDrawerCmdButton");
                drawerCommandsLayout.add(btn);

            } else {
                if (currentItem.labelText == null) {
                    // null is separator; not used yet ...
                }
                // subfolders not used yet ...
            }
        }
    }


    abstract protected void exitRequestedFromMenu();


}