package org.modellwerkstatt.turkuforms.sdicore;

import com.vaadin.experimental.Feature;
import com.vaadin.experimental.FeatureFlags;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.Version;
import org.modellwerkstatt.dataux.runtime.genspecifications.Menu;
import org.modellwerkstatt.objectflow.runtime.MoVersion;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.forms.LeftRight;
import org.modellwerkstatt.turkuforms.forms.TurkuMenu;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.lang.management.ManagementFactory;

public class SdiLayout extends VerticalLayout implements HasDynamicTitle {
    protected Div navbarTitleDiv;
    protected LeftRight topLrLayout;
    protected MenuBar mainmenuBar;

    protected String navbarTitle = "";

    protected ITurkuAppFactory turkuFactory;

    public SdiLayout() {
        Peculiar.shrinkSpace(this);

    }

    @Override
    public String getPageTitle() {
        return navbarTitle;
    }

    protected SubMenu addToMainMenu(Menu menu, String menuName){

        MenuItem root = mainmenuBar.addItem(Workarounds.createIconWithCollection(turkuFactory.translateIconName("mainmenu_down"), true));
        root.add(new Text(menuName));
        SubMenu rootSubMenu = root.getSubMenu();
        return TurkuMenu.addMainMenuStructure(turkuFactory, rootSubMenu, menu.getAllItems());
    }

    public void checkForMainMenu() {
        if (topLrLayout == null) {
            Span logo = new Span();
            logo.addClassName("NavBarSmallLogo");
            navbarTitleDiv = new Div();
            navbarTitleDiv.addClassName("TurkuLayoutNavbarTitle");
            navbarTitleDiv.addClassName("TurkuLayoutNavbarText");

            mainmenuBar = new MenuBar();
            mainmenuBar.setOpenOnHover(false);
            mainmenuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
            mainmenuBar.addClassName("TurkuLayoutMenuBar");

            topLrLayout = new LeftRight("TurkuLayoutNavbarTop");
            topLrLayout.addClassName("TurkuLayoutNavbarTopSDI");
            topLrLayout.add(logo);
            topLrLayout.add(navbarTitleDiv);
            topLrLayout.spacer();
            topLrLayout.add(mainmenuBar);
            addComponentAtIndex(0, topLrLayout);
        }
    }

    public String getTurkuVersionInfo() {
        String basicSysInfo = MoVersion.MOWARE_PLUGIN_VERSION + "\n" + Turku.INTERNAL_VERSION + " SDI DEMO\n";
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

}
