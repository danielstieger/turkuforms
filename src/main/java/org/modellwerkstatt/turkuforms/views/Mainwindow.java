package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import org.modellwerkstatt.dataux.runtime.genspecification.CmdAction;
import org.modellwerkstatt.dataux.runtime.genspecification.Tile;

import java.util.List;

@JavaScript("./turku.js")
abstract public class Mainwindow extends BasicWindow {

    protected TilesLayout tilesLayout;

    public Mainwindow() {

    }

    protected FlexLayout updateTiles(List<Tile> tileActionList) {
        if (tilesLayout == null) {
            tilesLayout = new TilesLayout();

            for(Tile tile: tileActionList) {
                CmdAction glue = tile.getCmdAction();
                ComponentEventListener<ClickEvent<Button>> execItem = event -> {
                    this.setDrawerOpened(false);
                    glue.startCommand();
                };
                tilesLayout.addTile(turkuFactory, tile, execItem);
            }

        } else {
          int runningIndex = 0;

          for(Tile tile: tileActionList) {
              CmdAction glue = tile.getCmdAction();
              tilesLayout.updateTile(runningIndex, turkuFactory.translateButtonLabel(glue.labelText, glue.hotKey));
              runningIndex ++;
          }

        }

        return tilesLayout;
    }
}
