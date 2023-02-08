package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.PreserveOnRefresh;

@PreserveOnRefresh
public class Mainwindow extends TurkuLayout {


    public Mainwindow() {
        super();

    }

    private FlexLayout getTiles() {
        FlexLayout tilesGrid = new FlexLayout();
        tilesGrid.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        tilesGrid.addClassName("MainwindowTilesGrid");
        tilesGrid.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        tilesGrid.setWidthFull();
        tilesGrid.setAlignContent(FlexLayout.ContentAlignment.SPACE_AROUND);

        String st = "_";
        for(int i=0; i < 10; i++) {
            Button tile = new Button("Start Command "+ st + i + " here. ");
            tile.setTooltipText("This is the tooltip for this command.. <br> This is a rather large explanation.");
            tile.setMinHeight("200px");
            tile.setMinWidth("200px");
            tile.addClassName("MainwindowTileButton");

            tilesGrid.setFlexGrow(0d, tile);
            tilesGrid.setFlexShrink(0d, tile);
            tilesGrid.setFlexBasis("30%", tile);

            st += "_";
            tilesGrid.add(tile);
        }

        return tilesGrid;
    }

}
