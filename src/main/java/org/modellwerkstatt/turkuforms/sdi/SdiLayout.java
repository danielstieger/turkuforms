package org.modellwerkstatt.turkuforms.sdi;

import com.vaadin.experimental.Feature;
import com.vaadin.experimental.FeatureFlags;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.Version;
import org.modellwerkstatt.dataux.runtime.genspecifications.AbstractAction;
import org.modellwerkstatt.dataux.runtime.genspecifications.CmdAction;
import org.modellwerkstatt.dataux.runtime.genspecifications.Menu;
import org.modellwerkstatt.dataux.runtime.genspecifications.TileAction;
import org.modellwerkstatt.objectflow.runtime.MoVersion;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.forms.LeftRight;
import org.modellwerkstatt.turkuforms.forms.TurkuMenu;
import org.modellwerkstatt.turkuforms.sdidemo.Cmd;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;
import org.modellwerkstatt.turkuforms.views.TilesLayout;

import java.lang.management.ManagementFactory;
import java.util.List;


@JavaScript("./turku.js")
public class SdiLayout extends VerticalLayout implements HasDynamicTitle {

    protected String navbarTitle = "";
    protected ITurkuAppFactory turkuFactory;
    protected TilesLayout tilesLayout;
    protected Div messageDiv;


    public SdiLayout() {
        Peculiar.shrinkSpace(this);
        setWidthFull();
        setHeightFull();
    }

    @Override
    public String getPageTitle() {
        return navbarTitle;
    }

    protected String getTurkuVersionInfo() {
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

    protected void quickUserInfo(String msg) {
        Notification notification = new Notification();
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        notification.setDuration(20000);

        Text text = new Text(msg);

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();
    }


    protected void initLandingPage (List<TileAction> tileActions) {

        if (tilesLayout != null) {
            Turku.l("SdiLayout.initLandingPage() THIS CAN NOT HAPPEN. TilesLayout was already initialized.");
        }

        tilesLayout = new TilesLayout();

        for(TileAction tile: tileActions) {
            ComponentEventListener<ClickEvent<Button>> execItem = event -> {
                Notification.show("Hello World");
            };

            CmdAction act = tile.getAction();
            Button btn = tilesLayout.addButtonOnly(turkuFactory, act.image, act.labelText, act.getToolTip(), tile.getColor(), act.hotKey, execItem);
            btn.setEnabled(act.reevalEnabled());
        }

        messageDiv = new Div();
        messageDiv.addClassName("TurkuErrorDiv");

        add(messageDiv, tilesLayout);
    }




}
