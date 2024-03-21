package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.grid.contextmenu.GridSubMenu;
import com.vaadin.flow.component.html.Hr;
import org.modellwerkstatt.dataux.runtime.genspecifications.AbstractAction;
import org.modellwerkstatt.dataux.runtime.genspecifications.CmdAction;
import org.modellwerkstatt.dataux.runtime.genspecifications.Menu;
import org.modellwerkstatt.turkuforms.infra.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.util.Defs;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.TurkuHasEnabled;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.util.List;

public class MenuContext<T> {

    public final static String DOUBLECLICK_HK = "ENTER";

    protected CmdAction doubleClickAction;

    public MenuContext(ITurkuAppFactory factory, Grid<T> grid, Menu menu) {

        GridContextMenu<T> rootGCM = new GridContextMenu<>(grid);

        for (AbstractAction currentItem : menu.getAllItems()) {
            if (currentItem instanceof CmdAction) {
                // only overflow menu for context menu

            } else if (currentItem.labelText == null) {
                // null is separator, ignore that here ...

            } else {
                createMainMenuStructure(factory, grid, rootGCM, null, ((Menu) currentItem).getAllItems());
            }
        }
    }

    public void execDoubleClick() {
        if (doubleClickAction != null && doubleClickAction.reevalEnabled()) {
            doubleClickAction.startCommand();
        }
    }


    private void createMainMenuStructure(ITurkuAppFactory turkuFactory,
                                         Grid<T> grid,
                                         GridContextMenu<T> rootGCM,
                                         GridSubMenu<T> subGCM,
                                         List<AbstractAction> menuItemList) {

        for (AbstractAction currentItem : menuItemList) {
            if (currentItem instanceof CmdAction) {
                addContextItem(turkuFactory, grid, rootGCM, subGCM, (CmdAction) currentItem);

            } else {
                if (currentItem.labelText == null) { // null is separator
                    if (rootGCM != null) {
                        rootGCM.add(new Hr());
                    } else {
                        subGCM.add(new Hr());
                    }

                } else {

                    GridMenuItem<T> createdItem;
                    if (rootGCM != null) {
                        createdItem = rootGCM.addItem(currentItem.labelText);
                    } else {
                        createdItem = subGCM.addItem(currentItem.labelText);
                    }
                    subGCM = createdItem.getSubMenu();

                    createMainMenuStructure(turkuFactory, grid, null, subGCM, ((Menu) currentItem).getAllItems());
                }
            }
        }
    }




    private void addContextItem(ITurkuAppFactory turkuFactory, Grid<T> grid, GridContextMenu<T> rootGCM, GridSubMenu<T> subGCM, CmdAction glue) {

        ComponentEventListener<GridContextMenu.GridContextMenuItemClickEvent<T>> execGCMItem = event -> {
            event.getSource().setEnabled(false);
            glue.startCommand();
        };

        // either or - not both...
        boolean createRootGCM = rootGCM != null;
        GridMenuItem<T> createdGCM = null;

        MenuItem created;

        if (Defs.hasIcon(glue.image)) {
            Component icon = Workarounds.createIconWithCollection(turkuFactory.translateIconName(glue.image));

            if (createRootGCM) {
                createdGCM = rootGCM.addItem(icon, execGCMItem);
            } else {
                createdGCM = subGCM.addItem(icon, execGCMItem);
            }

            createdGCM.add(new Text(turkuFactory.translateButtonLabel(glue.labelText, glue.hotKey)));


        } else {
            String label = turkuFactory.translateButtonLabel(glue.labelText, glue.hotKey);

            if (createRootGCM) {
                createdGCM = rootGCM.addItem(label, execGCMItem);
            } else {
                createdGCM = subGCM.addItem(label, execGCMItem);
            }

        }

        glue.attachButton2(new TurkuHasEnabled(createdGCM, "Context " + glue.labelText));


        if (Defs.needsHkRegistration(glue.hotKey)) {
            Component turkuTable = grid.getParent().get();
            Peculiar.useGridShortcutHk(turkuTable, glue.hotKey, event -> { if (glue.reevalEnabled()) { glue.startCommand(); } });

            if (glue.hotKey.equals(DOUBLECLICK_HK)) {
                if (doubleClickAction != null) {
                    throw new IllegalStateException("There is already a " + DOUBLECLICK_HK + " hotkey registered for this context menu! Registered " + doubleClickAction + " new to add " + glue);
                } else {
                    doubleClickAction = glue;
                }
            }
        }
    }


}
