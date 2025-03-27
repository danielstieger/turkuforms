package org.modellwerkstatt.turkuforms.sdi;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
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

import java.util.List;


public class StaticLandingPage {




    protected Div titleDiv;


    public StaticLandingPage() {

    }

    public VerticalLayout installTilePage(ITurkuAppFactory factory, IToolkit_Window current, SdiAppCrtl crlt, String title, String msg, List<LandingPageUrlItem> allItems) {
        VerticalLayout mainContentLayout = new VerticalLayout();
        mainContentLayout.setSizeFull();
        mainContentLayout.setFlexGrow(2d);

        // new tiles layout ...
        TilesLayout tilesLayout = new TilesLayout();

        for(LandingPageUrlItem item: allItems) {
            ComponentEventListener<ClickEvent<Button>> execItem = event -> {
                Turku.l("Tile opening new url " + item.url);

                OFXUrlParams params = new OFXUrlParams();
                params.parse(item.url);
                crlt.startCommandViaUrlPickUp(current, params);
            };

            Button btn = tilesLayout.addButtonOnly(factory, item.icon, item.label, item.tooltip, item.color, item.hotkey, execItem);
            btn.setEnabled(item.enabled);
        }

        titleDiv =  new Div();
        titleDiv.addClassName("LandingPageTopTitle");
        titleDiv.setText(title);

        Div messageDiv = new Div();
        messageDiv.addClassName("TurkuErrorDiv");
        if (msg != null) {
            messageDiv.setText(msg);
        }

        mainContentLayout.add(messageDiv);
        mainContentLayout.add(tilesLayout);

        return mainContentLayout;
    }



    protected Button createDrawerButton(ITurkuAppFactory turkuFactory, CmdAction glue) {
        ComponentEventListener<ClickEvent<Button>> execItem = event -> {
            glue.startCommand();
        };
        Button btn;

        if (Defs.hasIcon(glue.image)) {
            Component icn = Workarounds.createIconWithCollection(turkuFactory.translateIconName(glue.image), false);
            btn = new Button(turkuFactory.translateButtonLabel(glue.labelText, glue.hotKey), icn, execItem);

        } else {
            btn = new Button(turkuFactory.translateButtonLabel(glue.labelText, glue.hotKey), execItem);

        }

        glue.attachButton1(new TurkuHasEnabled(glue.hideWhenDisabled, btn, "Drawer " + glue.labelText));

        Workarounds.addMlToolTipIfNec(glue.getToolTip(), btn);

        btn.setWidthFull();
        // btn.setDisableOnClick(true);
        btn.setClassName("MainwindowDrawerCmdButton");
        return btn;
    }

    protected void addDrawerMenu(List<AbstractAction> menuItemList){


    }

    public VerticalLayout installDrawerCommands(ITurkuAppFactory factory, List<AbstractAction> menuItemList) {
        VerticalLayout drawerCommandsLayout = new VerticalLayout();
        drawerCommandsLayout.setSizeFull();
        drawerCommandsLayout.setFlexGrow(1d);


        int componentIndex = 0;
        for (AbstractAction currentItem : menuItemList) {
            if (currentItem instanceof CmdAction) {
                Button btn = createDrawerButton(factory, (CmdAction) currentItem);
                drawerCommandsLayout.addComponentAtIndex(componentIndex++, btn);

            } else if (currentItem.labelText == null) {
                // null is separator; not used yet ...

            } else {
                Label section = new Label(currentItem.labelText);
                section.addClassName("DrawerMenuHeading");
                drawerCommandsLayout.addComponentAtIndex(componentIndex++, section);

                for (AbstractAction inFolder : ((Menu) currentItem).getAllItems()) {
                    if (inFolder instanceof CmdAction) {
                        Button btn = createDrawerButton(factory, (CmdAction) inFolder);
                        drawerCommandsLayout.addComponentAtIndex(componentIndex++, btn);

                    }
                }

            }
        }

        return drawerCommandsLayout;
    }
}
