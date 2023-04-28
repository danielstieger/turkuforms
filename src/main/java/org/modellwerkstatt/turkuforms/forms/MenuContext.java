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
import com.vaadin.flow.component.icon.Icon;
import org.modellwerkstatt.dataux.runtime.genspecifications.MenuActionGlue;
import org.modellwerkstatt.dataux.runtime.genspecifications.MenuSub;
import org.modellwerkstatt.turkuforms.app.ITurkuFactory;
import org.modellwerkstatt.turkuforms.util.Defs;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.TurkuHasEnabled;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.util.List;

public class MenuContext<T> {

    public final static String DOUBLECLICK_HK = "ENTER";

    protected MenuActionGlue doubleClickAction;

    public MenuContext(ITurkuFactory factory, Grid<T> grid, MenuSub menu) {

        GridContextMenu<T> rootGCM = new GridContextMenu<>(grid);

        for (org.modellwerkstatt.dataux.runtime.genspecifications.MenuItem currentItem : menu.items) {
            if (currentItem instanceof MenuActionGlue) {
                // only overflow menu for context menu

            } else if (currentItem.labelText == null) {
                // null is separator, ignore that here ...

            } else {
                createMainMenuStructure(factory, grid, rootGCM, null, ((MenuSub) currentItem).items);
            }
        }
    }

    public void execDoubleClick() {
        if (doubleClickAction != null && doubleClickAction.reevalEnabled()) {
            doubleClickAction.startCommand();
        }
    }


    private void createMainMenuStructure(ITurkuFactory turkuFactory,
                                                      Grid<T> grid,
                                                      GridContextMenu<T> rootGCM,
                                                      GridSubMenu<T> subGCM,
                                                      List<org.modellwerkstatt.dataux.runtime.genspecifications.MenuItem> menuItemList) {

        for (org.modellwerkstatt.dataux.runtime.genspecifications.MenuItem currentItem : menuItemList) {
            if (currentItem instanceof MenuActionGlue) {
                addContextItem(turkuFactory, grid, rootGCM, subGCM, (MenuActionGlue) currentItem);

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

                    createMainMenuStructure(turkuFactory, grid, null, subGCM, ((MenuSub) currentItem).items);
                }
            }
        }
    }




    private void addContextItem(ITurkuFactory turkuFactory, Grid<T> grid, GridContextMenu<T> rootGCM, GridSubMenu<T> subGCM, MenuActionGlue glue) {

        ComponentEventListener<GridContextMenu.GridContextMenuItemClickEvent<T>> execGCMItem = event -> {
            event.getSource().setEnabled(false);
            glue.startCommand();
        };

        // either or - not both...
        boolean createRootGCM = rootGCM != null;
        GridMenuItem<T> createdGCM = null;

        MenuItem created;

        if (Defs.hasIcon(glue.imageName)) {
            Icon icon = Workarounds.createIconWithCollection(turkuFactory.translateIconName(glue.imageName));
            icon.addClassName("TurkulayoutMenuIcon");

            if (createRootGCM) {
                createdGCM = rootGCM.addItem(icon, execGCMItem);
            } else {
                createdGCM = subGCM.addItem(icon, execGCMItem);
            }

            createdGCM.add(new Text(turkuFactory.translateButtonLabel(glue.labelText, glue.public_hotKey)));


        } else {
            String label = turkuFactory.translateButtonLabel(glue.labelText, glue.public_hotKey);

            if (createRootGCM) {
                createdGCM = rootGCM.addItem(label, execGCMItem);
            } else {
                createdGCM = subGCM.addItem(label, execGCMItem);
            }

        }

        glue.attachButton2(new TurkuHasEnabled(createdGCM, "Context " + glue.labelText));


        if (Defs.needsHkRegistration(glue.public_hotKey)) {
            Component turkuTable = grid.getParent().get();
            Peculiar.useGridShortcutHk(turkuTable, glue.public_hotKey, event -> { if (glue.reevalEnabled()) { glue.startCommand(); } });

            if (glue.public_hotKey.equals(DOUBLECLICK_HK)) {
                if (doubleClickAction != null) {
                    throw new IllegalStateException("There is already a " + DOUBLECLICK_HK + " hotkey registered for this context menu! Registered " + doubleClickAction + " new to add " + glue);
                } else {
                    doubleClickAction = glue;
                }
            }
        }
    }


}
