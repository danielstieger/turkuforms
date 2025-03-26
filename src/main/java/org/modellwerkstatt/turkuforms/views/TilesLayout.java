package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import org.modellwerkstatt.dataux.runtime.genspecifications.CmdAction;
import org.modellwerkstatt.dataux.runtime.genspecifications.TileAction;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.util.Defs;
import org.modellwerkstatt.turkuforms.util.TurkuHasEnabled;
import org.modellwerkstatt.turkuforms.util.Workarounds;

public class TilesLayout extends FlexLayout {

    public TilesLayout() {
        this("MainwindowTilesGrid");
    }

    public TilesLayout(String clsName) {

        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        addClassName(clsName);
        setFlexWrap(FlexLayout.FlexWrap.WRAP);
        setWidthFull();
        setAlignContent(FlexLayout.ContentAlignment.SPACE_AROUND);

    }


    public void addTile(ITurkuAppFactory factory, TileAction tile, ComponentEventListener<ClickEvent<Button>> execItem) {

        CmdAction action = tile.getAction();
        Button btn = addButtonOnly(factory, action.image, action.labelText, action.getToolTip(), tile.getColor(), action.hotKey, execItem);

        action.attachButton1(new TurkuHasEnabled(action.hideWhenDisabled, btn, "Tile " + action.labelText));

    }

    public Button addButtonOnly(ITurkuAppFactory factory, String icon, String text, String tooltip, String clr, String hk, ComponentEventListener<ClickEvent<Button>> execItem){
        Button btn;
        if (Defs.hasIcon(icon)) {
            Component icn = Workarounds.createIconWithCollection(factory.translateIconName(icon), false);
            btn = new Button(factory.translateButtonLabel(text, hk), icn, execItem);

        } else {
            btn = new Button(factory.translateButtonLabel(text, hk), execItem);

        }

        // btn.setDisableOnClick(true);
        Workarounds.addMlToolTipIfNec(tooltip, btn);
        btn.setMinHeight("200px");
        btn.setMinWidth("200px");
        btn.addClassName("MainwindowTileButton");

        if (clr == null) { clr = "var(--lumo-primary-color)"; }
        btn.getStyle().set("border-bottom", "6px solid " + clr);
        btn.getStyle().set("color", clr);


        setFlexGrow(0d, btn);
        setFlexShrink(0d, btn);
        setFlexBasis("30%", btn);
        add(btn);
        return btn;
    }

    public void updateTile(int index, String text) {
        ((Button) getComponentAt(index)).setText(text);
    }
}
