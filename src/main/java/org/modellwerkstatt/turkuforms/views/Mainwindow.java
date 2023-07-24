package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import org.modellwerkstatt.dataux.runtime.genspecifications.CmdAction;
import org.modellwerkstatt.dataux.runtime.genspecifications.TileAction;
import org.modellwerkstatt.turkuforms.util.Defs;
import org.modellwerkstatt.turkuforms.util.TurkuHasEnabled;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.util.List;

@JavaScript("./turku.js")

abstract public class Mainwindow extends BasicWindow {

    protected FlexLayout tilesFlexLayout;

    public Mainwindow() {

    }


    protected FlexLayout updateTiles(List<TileAction> tileActionList) {
        if (tilesFlexLayout == null) {
            tilesFlexLayout = new FlexLayout();
            tilesFlexLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            tilesFlexLayout.addClassName("MainwindowTilesGrid");
            tilesFlexLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
            tilesFlexLayout.setWidthFull();
            tilesFlexLayout.setAlignContent(FlexLayout.ContentAlignment.SPACE_AROUND);

            for(TileAction tile: tileActionList) {
                CmdAction glue = tile.getAction();
                ComponentEventListener<ClickEvent<Button>> execItem = event -> {
                    this.setDrawerOpened(false);
                    glue.startCommand();
                };
                Button btn;

                if (Defs.hasIcon(glue.image)) {
                    Component icn = Workarounds.createIconWithCollection(turkuFactory.translateIconName(glue.image));
                    btn = new Button(turkuFactory.translateButtonLabel(glue.labelText, glue.hotKey), icn, execItem);

                } else {
                    btn = new Button(turkuFactory.translateButtonLabel(glue.labelText, glue.hotKey), execItem);

                }

                glue.attachButton1(new TurkuHasEnabled(btn, "Tile " + glue.labelText));

                // btn.setDisableOnClick(true);
                btn.setTooltipText(Workarounds.mlToolTipText(tile.getAction().getToolTip()));
                btn.setMinHeight("200px");
                btn.setMinWidth("200px");
                btn.addClassName("MainwindowTileButton");
                btn.getStyle().set("border-bottom", "5px solid " + tile.getColor());

                tilesFlexLayout.setFlexGrow(0d, btn);
                tilesFlexLayout.setFlexShrink(0d, btn);
                tilesFlexLayout.setFlexBasis("30%", btn);
                tilesFlexLayout.add(btn);
            }

        } else {
          int runningIndex = 0;

          for(TileAction tile: tileActionList) {
              CmdAction glue = tile.getAction();
              ((Button) tilesFlexLayout.getComponentAt(runningIndex)).setText(turkuFactory.translateButtonLabel(glue.labelText, glue.hotKey));
              runningIndex ++;
          }

        }


        return tilesFlexLayout;
    }

}
