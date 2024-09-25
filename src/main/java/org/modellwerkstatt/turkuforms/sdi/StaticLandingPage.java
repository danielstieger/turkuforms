package org.modellwerkstatt.turkuforms.sdi;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import org.modellwerkstatt.dataux.runtime.sdicore.LandingPageUrlItem;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.views.TilesLayout;

import java.util.List;


@JavaScript("./turku.js")
abstract public class StaticLandingPage extends VerticalLayout implements HasDynamicTitle {

    protected String navbarTitle = "";
    protected ITurkuAppFactory turkuFactory;

    protected TilesLayout tilesLayout;
    protected Div messageDiv;
    protected Div titleDiv;


    public StaticLandingPage() {
        Peculiar.shrinkSpace(this);
        setWidthFull();
        setHeightFull();
    }

    public void installLandingPage(ITurkuAppFactory factory, String title, String msg, List<LandingPageUrlItem> allItems) {

        // new tiles layout ...
        tilesLayout = new TilesLayout();

        for(LandingPageUrlItem item: allItems) {
            ComponentEventListener<ClickEvent<Button>> execItem = event -> {
                Turku.l("Tile.navigate to " + item.url);
                UI.getCurrent().navigate(item.url);
            };

            Button btn = tilesLayout.addButtonOnly(factory, item.icon, item.label, item.tooltip, item.color, item.hotkey, execItem);
            btn.setEnabled(item.enabled);
        }

        titleDiv =  new Div();
        titleDiv.addClassName("LandingPageTopTitle");
        titleDiv.setText(title);

        messageDiv = new Div();
        messageDiv.addClassName("TurkuErrorDiv");
        if (msg != null) {
            messageDiv.setText(msg);
        }

        add(titleDiv);
        add(messageDiv);
        add(tilesLayout);
    }

    @Override
    public String getPageTitle() {
        return navbarTitle;
    }
}
