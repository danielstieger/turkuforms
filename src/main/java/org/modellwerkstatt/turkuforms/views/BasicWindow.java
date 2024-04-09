package org.modellwerkstatt.turkuforms.views;

import com.vaadin.experimental.Feature;
import com.vaadin.experimental.FeatureFlags;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.Version;
import org.modellwerkstatt.dataux.runtime.genspecifications.AbstractAction;
import org.modellwerkstatt.dataux.runtime.genspecifications.CmdAction;
import org.modellwerkstatt.dataux.runtime.genspecifications.Menu;
import org.modellwerkstatt.objectflow.runtime.MoVersion;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.forms.LeftRight;
import org.modellwerkstatt.turkuforms.forms.TurkuMenu;
import org.modellwerkstatt.turkuforms.util.Defs;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.TurkuHasEnabled;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.lang.management.ManagementFactory;
import java.util.List;


abstract public class BasicWindow extends AppLayout implements HasDynamicTitle {

    private Label sysInfoLabel;
    private Label userInfoLabel;
    private Div navbarTitleDiv;
    private String navbarTitle = "";
    private String optionalTabTitleInNavbar = "";

    protected ITurkuAppFactory turkuFactory;
    protected LeftRight topLrLayout;

    protected DrawerToggle drawerToggle;
    protected VerticalLayout drawerCommandsLayout;
    protected MenuBar mainmenuBar;
    protected boolean appInCompactMode;

    public BasicWindow() {
    }

    protected void init(ITurkuAppFactory factory, boolean compact, String appNavbarTitle) {
        turkuFactory = factory;
        appInCompactMode = compact;

        userInfoLabel = new Label("-");
        userInfoLabel.addClassName("TurkuLayoutNavbarText");
        sysInfoLabel = new Label("-");
        sysInfoLabel.addClassName("TurkuLayoutNavbarText");

        topLrLayout = new LeftRight("TurkuLayoutNavbarTop");
        addToNavbar(topLrLayout);

        Span logo = new Span();
        logo.addClassName("NavBarSmallLogo");
        navbarTitleDiv = new Div();
        navbarTitleDiv.addClassName("TurkuLayoutNavbarTitle");
        navbarTitleDiv.addClassName("TurkuLayoutNavbarText");



        if (!appInCompactMode) {
            mainmenuBar = new MenuBar();
            mainmenuBar.setOpenOnHover(false);
            mainmenuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
            mainmenuBar.addClassName("TurkuLayoutMenuBar");

            topLrLayout.add(logo);
            topLrLayout.add(navbarTitleDiv);
            topLrLayout.spacer();
            topLrLayout.add(mainmenuBar);


        } else {
            drawerToggle = new DrawerToggle();
            setPrimarySection(Section.NAVBAR);
            setDrawerOpened(false);

            topLrLayout.add(drawerToggle);
            topLrLayout.add(navbarTitleDiv);
            topLrLayout.spacer();
            topLrLayout.add(logo);


            /* Button darkToggle = new Button(Workarounds.createIconWithCollection(factory.translateIconName("mainmenu_adjust")), event -> {
                ThemeList themeList = UI.getCurrent().getElement().getThemeList();

                if (themeList.contains(Lumo.DARK)) {
                    themeList.remove(Lumo.DARK);
                } else {
                    themeList.add(Lumo.DARK);
                }
                this.setDrawerOpened(false);
            });
            darkToggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            darkToggle.setSizeUndefined(); */

            Button logout = new Button(Workarounds.createIconWithCollection(factory.translateIconName("mainmenu_logout"), false), event -> {
                this.setDrawerOpened(false);
                exitRequestedFromMenu();
            });
            logout.setSizeUndefined();
            logout.addClassName("DrawerLogout");



            Tooltip sysInfoTooltip = Tooltip.forComponent(sysInfoLabel);
            sysInfoTooltip.setText(getTurkuVersionInfo());
            sysInfoTooltip.setPosition(Tooltip.TooltipPosition.TOP_END);
            sysInfoTooltip.setHideDelay(5000);

            userInfoLabel.setWidthFull();
            HorizontalLayout drawerBottom = new HorizontalLayout(userInfoLabel, logout);
            drawerBottom.setWidthFull();
            drawerBottom.setAlignSelf(FlexComponent.Alignment.CENTER, userInfoLabel);

            drawerCommandsLayout = new VerticalLayout();
            drawerCommandsLayout.setSizeFull();

            Label spacer = new Label();
            drawerCommandsLayout.add(spacer, sysInfoLabel, drawerBottom);
            drawerCommandsLayout.setFlexGrow(2d, spacer);
            addToDrawer(drawerCommandsLayout);
        }


    }

    protected void setNavbarTitleDiv(String title) {
        navbarTitle = title;
        String total = optionalTabTitleInNavbar.isEmpty()? navbarTitle: navbarTitle + " - " + optionalTabTitleInNavbar;
        navbarTitleDiv.setText(total);
    }

    @Override
    public String getPageTitle() {
        // returns browser tab title
        return navbarTitleDiv != null ? navbarTitleDiv.getText() : "";
    }

    protected void setUserInfo(String info) {
        userInfoLabel.setText(info);
    }

    protected void setSysInfo(String info){
        sysInfoLabel.setText(info);
    }

    protected void setOptionalTabTitleInNavbar(String title) {
        optionalTabTitleInNavbar = title;
        setNavbarTitleDiv(navbarTitle);
    }

    protected SubMenu addToMainMenu(Menu menu, String menuName){

        MenuItem root = mainmenuBar.addItem(Workarounds.createIconWithCollection(turkuFactory.translateIconName("mainmenu_down"), true));
        root.add(new Text(menuName));
        SubMenu rootSubMenu = root.getSubMenu();
        return TurkuMenu.addMainMenuStructure(turkuFactory, rootSubMenu, menu.getAllItems());
    }

    protected void addDrawerMenu(List<AbstractAction> menuItemList){

        int componentIndex = 0;
        for (AbstractAction currentItem : menuItemList) {
            if (currentItem instanceof CmdAction) {
                CmdAction glue =  (CmdAction) currentItem;
                ComponentEventListener<ClickEvent<Button>> execItem = event -> {
                    this.setDrawerOpened(false);
                    glue.startCommand();
                };
                Button btn;

                if (Defs.hasIcon(glue.image)) {
                    Component icn = Workarounds.createIconWithCollection(turkuFactory.translateIconName(glue.image), false);
                    btn = new Button(turkuFactory.translateButtonLabel(glue.labelText, glue.hotKey), icn, execItem);

                } else {
                    btn = new Button(turkuFactory.translateButtonLabel(glue.labelText, glue.hotKey), execItem);

                }

                glue.attachButton1(new TurkuHasEnabled(btn, "Drawer " + glue.labelText));
                btn.setTooltipText(Workarounds.mlToolTipText(glue.getToolTip()));
                btn.setWidthFull();
                // btn.setDisableOnClick(true);
                btn.setClassName("MainwindowDrawerCmdButton");
                drawerCommandsLayout.addComponentAtIndex(componentIndex++, btn);

            } else {
                if (currentItem.labelText == null) {
                    // null is separator; not used yet ...
                }
                // subfolders not used yet ...
            }
        }
    }

    public void adjustTopBarColorOrNull(String col){
        if (col == null) {
            topLrLayout.getElement().getStyle().remove("border-top");
        } else {
            topLrLayout.getElement().getStyle().set("border-top", "6px solid " + col);
        }
    }

    public String getTurkuVersionInfo() {
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
        return basicSysInfo;
    }

    abstract protected void exitRequestedFromMenu();
}