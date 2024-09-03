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

    protected TilesLayout tilesLayout;

    public Mainwindow() {

    }

    protected FlexLayout updateTiles(List<TileAction> tileActionList) {
        if (tilesLayout == null) {
            tilesLayout = new TilesLayout();

            for(TileAction tile: tileActionList) {
                CmdAction glue = tile.getAction();
                ComponentEventListener<ClickEvent<Button>> execItem = event -> {
                    this.setDrawerOpened(false);
                    glue.startCommand();
                };
                tilesLayout.addTile(turkuFactory, tile, execItem);
            }

        } else {
          int runningIndex = 0;

          for(TileAction tile: tileActionList) {
              CmdAction glue = tile.getAction();
              tilesLayout.updateTile(runningIndex, turkuFactory.translateButtonLabel(glue.labelText, glue.hotKey));
              runningIndex ++;
          }

        }

        return tilesLayout;
    }
}
