package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import org.modellwerkstatt.dataux.runtime.genspecifications.MenuActionGlue;
import org.modellwerkstatt.dataux.runtime.genspecifications.TileAction;
import org.modellwerkstatt.turkuforms.util.Defs;
import org.modellwerkstatt.turkuforms.util.TurkuHasEnabled;
import org.modellwerkstatt.turkuforms.util.Workarounds;

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
                MenuActionGlue glue = tile.getAction();
                ComponentEventListener<ClickEvent<Button>> execItem = event -> {
                    this.setDrawerOpened(false);
                    glue.startCommand();
                };
                Button btn;

                if (Defs.hasIcon(glue.imageName)) {
                    Icon icn = Workarounds.createIconWithCollection(turkuFactory.translateIconName(glue.imageName));
                    icn.addClassName("TurkulayoutMenuIcon");
                    btn = new Button(turkuFactory.translateButtonLabel(glue.labelText, glue.public_hotKey), icn, execItem);

                } else {
                    btn = new Button(turkuFactory.translateButtonLabel(glue.labelText, glue.public_hotKey), execItem);

                }

                glue.attachButton1(new TurkuHasEnabled(btn));

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
              MenuActionGlue glue = tile.getAction();
              ((Button) tilesFlexLayout.getComponentAt(runningIndex)).setText(turkuFactory.translateButtonLabel(glue.labelText, glue.public_hotKey));
              runningIndex ++;
          }

        }


        return tilesFlexLayout;
    }

}
