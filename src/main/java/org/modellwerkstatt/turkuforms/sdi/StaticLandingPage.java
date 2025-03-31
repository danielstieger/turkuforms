package org.modellwerkstatt.turkuforms.sdi;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import org.modellwerkstatt.dataux.runtime.genspecifications.AbstractAction;
import org.modellwerkstatt.dataux.runtime.genspecifications.CmdAction;
import org.modellwerkstatt.dataux.runtime.genspecifications.Menu;
import org.modellwerkstatt.dataux.runtime.sdicore.LandingPageUrlItem;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Window;
import org.modellwerkstatt.objectflow.runtime.OFXUrlParams;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.forms.LeftRight;
import org.modellwerkstatt.turkuforms.util.*;
import org.modellwerkstatt.turkuforms.views.TilesLayout;

import java.security.SecurityPermission;
import java.util.List;


public class StaticLandingPage {




    protected Div titleDiv;


    public StaticLandingPage() {

    }

    public Component installTilePage(ITurkuAppFactory factory, IToolkit_Window landingWindow, SdiAppCrtl crlt, String msg) {
        VerticalLayout mainContentLayout = new VerticalLayout();
        mainContentLayout.setSizeUndefined();
        mainContentLayout.setHeightFull();

        // new tiles layout ...
        TilesLayout tilesLayout = new TilesLayout();

        List<LandingPageUrlItem> allItems = crlt.updateLandingPageTileUrlItems();
        for(LandingPageUrlItem item: allItems) {
            ComponentEventListener<ClickEvent<Button>> execItem = event -> {
                Turku.l("Tile opening new url " + item.url);

                OFXUrlParams params = new OFXUrlParams();
                params.parse(item.url);
                crlt.startCommandViaUrlPickUp(landingWindow, params);
            };

            Button btn = tilesLayout.addButtonOnly(factory, item.icon, item.label, item.tooltip, item.color, item.hotkey, execItem);
            btn.setEnabled(item.enabled);
        }

        titleDiv =  new Div();
        titleDiv.addClassName("LandingPageTopTitle");
        titleDiv.setText(crlt.getAppVersionAndDyn());

        Div messageDiv = new Div();
        messageDiv.addClassName("TurkuErrorDiv");
        if (msg != null) {
            messageDiv.setText(msg);
        }

        mainContentLayout.add(messageDiv);
        mainContentLayout.add(tilesLayout);

        return mainContentLayout;
    }


    protected void addDrawerMenu(ITurkuAppFactory factory, IToolkit_Window landingWindow, SdiAppCrtl crlt, VerticalLayout layout, List<LandingPageUrlItem> menuItemList){

        for (LandingPageUrlItem item : menuItemList) {
            if (item.isSubMenu()) {
                Label section = new Label(item.label);
                section.addClassName("DrawerMenuHeading");
                layout.add(section);

                addDrawerMenu(factory, landingWindow, crlt, layout, item.getSubItems());

            } else if (item.isSeperator()) {
                // not used yet

            } else {
                ComponentEventListener<ClickEvent<Button>> execItem = event -> {
                    OFXUrlParams params = new OFXUrlParams();
                    params.parse(item.url);
                    crlt.startCommandViaUrlPickUp(landingWindow, params);
                };

                Button btn;
                if (Defs.hasIcon(item.icon)) {
                    Component icn = Workarounds.createIconWithCollection(factory.translateIconName(item.icon), false);
                    btn = new Button(factory.translateButtonLabel(item.label, item.hotkey), icn, execItem);

                } else {
                    btn = new Button(factory.translateButtonLabel(item.label, item.hotkey), execItem);

                }
                Workarounds.addMlToolTipIfNec(item.tooltip, btn);
                btn.setWidthFull();
                // btn.setDisableOnClick(true);
                btn.setClassName("MainwindowDrawerCmdButton");
                btn.setEnabled(item.enabled);
                layout.add(btn);
            }
        }
    }

    public Component installDrawerCommands(ITurkuAppFactory factory, IToolkit_Window landingWindow, SdiAppCrtl crlt) {
        VerticalLayout drawerCommandsLayout = new VerticalLayout();
        drawerCommandsLayout.setSizeUndefined();
        drawerCommandsLayout.setHeightFull();

        Span logo = new Span();
        logo.addClassName("NavBarSmallLogo" + crlt.getUserEnvironment().getBrandingId());
        Label userInfoLabel = new Label(crlt.getAppVersionAndDyn());
        userInfoLabel.addClassName("TurkuLayoutNavbarText");
        drawerCommandsLayout.add(new HorizontalLayout(logo, userInfoLabel));

        List<LandingPageUrlItem> menuItemList = crlt.updateLandingPageMenuUrlItems();
        addDrawerMenu(factory, landingWindow, crlt, drawerCommandsLayout, menuItemList);
        return drawerCommandsLayout;
    }
}
