package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.PreserveOnRefresh;
import org.modellwerkstatt.dataux.runtime.genspecifications.TileAction;

import java.util.List;

public class Mainwindow extends TurkuLayout {


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
                Button btn = new Button(tile.getAction().labelText);
                btn.setTooltipText(tile.getAction().getToolTip());
                btn.setMinHeight("200px");
                btn.setMinWidth("200px");
                btn.addClassName("MainwindowTileButton");

                tilesFlexLayout.setFlexGrow(0d, btn);
                tilesFlexLayout.setFlexShrink(0d, btn);
                tilesFlexLayout.setFlexBasis("30%", btn);
                tilesFlexLayout.add(btn);
            }

        } else {
          int runningIndex = 0;

          for(TileAction tile: tileActionList) {
              ((Button) tilesFlexLayout.getComponentAt(runningIndex)).setText(tile.getAction().labelText);
              runningIndex ++;
          }

        }


        return tilesFlexLayout;
    }

}
