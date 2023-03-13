package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.grid.contextmenu.GridSubMenu;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.shared.Tooltip;
import org.modellwerkstatt.dataux.runtime.genspecifications.MenuActionGlue;
import org.modellwerkstatt.dataux.runtime.genspecifications.MenuSub;
import org.modellwerkstatt.turkuforms.app.ITurkuFactory;
import org.modellwerkstatt.turkuforms.util.Defs;
import org.modellwerkstatt.turkuforms.util.HkTranslate;
import org.modellwerkstatt.turkuforms.util.TurkuHasEnabled;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.util.List;

public class MenuStructure extends MenuBar {
    public final static String DOUBLECLICK_HK = "ENTER";

    protected MenuActionGlue doubleClickAction;

    public MenuStructure() {
        super();
        addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
    }


    public <T> void initialize(ITurkuFactory factory, MenuSub menu, Grid<T> grid) {
        GridContextMenu<T> rootGCM = grid == null ? null : new GridContextMenu<T>(grid);

        for (org.modellwerkstatt.dataux.runtime.genspecifications.MenuItem currentItem : menu.items) {
            if (currentItem instanceof MenuActionGlue) {
                // only overflow menu for context menu
                createActionItem(factory, this,this, grid, null, null, (MenuActionGlue) currentItem);

            } else {
                if (currentItem.labelText == null) {
                    // null is separator, ignore that here ...

                } else {
                    MenuItem created = this.addItem(Workarounds.createIconWithCollection(factory.translateIconName("table_menu")));
                    SubMenu createdSub = created.getSubMenu();
                    createMainMenuStructure(factory, this, createdSub, grid, rootGCM, null, ((MenuSub) currentItem).items);
                }
            }
        }
    }

    public void execDoubleClick() {
        if (doubleClickAction != null && doubleClickAction.reevalEnabled()) {
            doubleClickAction.startCommand();
        }
    }


    static public <T> SubMenu createMainMenuStructure(ITurkuFactory turkuFactory,
                                                      MenuStructure overflowMenu,
                                                      SubMenu parent,
                                                      Grid grid,
                                                      GridContextMenu<T> rootGCM,
                                                      GridSubMenu<T> subGCM,
                                                      List<org.modellwerkstatt.dataux.runtime.genspecifications.MenuItem> menuItemList) {

        for (org.modellwerkstatt.dataux.runtime.genspecifications.MenuItem currentItem : menuItemList) {
            if (currentItem instanceof MenuActionGlue) {
                createActionItem(turkuFactory, overflowMenu, parent, grid, rootGCM, subGCM, (MenuActionGlue) currentItem);

            } else {
                if (currentItem.labelText == null) {
                    // null is separator
                    parent.add(new Hr());

                } else {
                    MenuItem created = parent.addItem(currentItem.labelText);
                    SubMenu createdSub = created.getSubMenu();

                    if (rootGCM != null) {
                        GridMenuItem<T> createdGCM = rootGCM.addItem(currentItem.labelText);
                        subGCM = createdGCM.getSubMenu();

                    } else if (subGCM != null) {
                        GridMenuItem<T> createdGCM = subGCM.addItem(currentItem.labelText);
                        subGCM = createdGCM.getSubMenu();

                    }

                    createMainMenuStructure(turkuFactory, overflowMenu, createdSub, grid, null, subGCM, ((MenuSub) currentItem).items);
                }
            }
        }
        return parent;
    }




    static public <T> MenuItem createActionItem(ITurkuFactory turkuFactory, MenuStructure overflowMenu, HasMenuItems parent, Grid grid, GridContextMenu<T> rootGCM, GridSubMenu<T> subGCM, MenuActionGlue glue) {
        // Menu & GCM do not have common interfaces and HasGridMenuItems is protected (vaadin bug?)
        ComponentEventListener<ClickEvent<MenuItem>> execItem = event -> {
            event.getSource().setEnabled(false);
            glue.startCommand();
        };
        ComponentEventListener<GridContextMenu.GridContextMenuItemClickEvent<T>> execGCMItem = event -> {
            event.getSource().setEnabled(false);
            glue.startCommand();
        };

        // either or - not both...
        boolean createRootGCM = rootGCM != null;
        boolean createSubGCM = subGCM != null;
        GridMenuItem<T> createdGCM = null;

        MenuItem created;

        if (Defs.hasIcon(glue.imageName)) {
            Icon icon = Workarounds.createIconWithCollection(turkuFactory.translateIconName(glue.imageName));
            icon.addClassName("TurkulayoutMenuIcon");
            created = parent.addItem(icon, execItem);
            created.add(new Text(turkuFactory.translateButtonLabel(glue.labelText, glue.public_hotKey)));

            if (createRootGCM) {
                icon = Workarounds.createIconWithCollection(turkuFactory.translateIconName(glue.imageName));
                icon.addClassName("TurkulayoutMenuIcon");
                createdGCM = rootGCM.addItem(icon, execGCMItem);
                createdGCM.add(new Text(turkuFactory.translateButtonLabel(glue.labelText, glue.public_hotKey)));

            } else if (createSubGCM) {
                icon = Workarounds.createIconWithCollection(turkuFactory.translateIconName(glue.imageName));
                icon.addClassName("TurkulayoutMenuIcon");
                createdGCM = subGCM.addItem(icon, execGCMItem);
                createdGCM.add(new Text(turkuFactory.translateButtonLabel(glue.labelText, glue.public_hotKey)));

            }

        } else {
            String label = turkuFactory.translateButtonLabel(glue.labelText, glue.public_hotKey);
            created = parent.addItem(label, execItem);

            if (createRootGCM) {
                createdGCM = rootGCM.addItem(label, execGCMItem);
            } else if (createSubGCM) {
                createdGCM = subGCM.addItem(label, execGCMItem);
            }

        }

        glue.attachButton1(new TurkuHasEnabled(created, "Menu " + glue.labelText));
        Tooltip t = Tooltip.forComponent(created);
        t.setText(Workarounds.mlToolTipText(glue.getToolTip()));

        if (createdGCM != null) {
            glue.attachButton2(new TurkuHasEnabled(createdGCM, "Context " + glue.labelText));
        }

        if (Defs.needsHkRegistration(glue.public_hotKey) && grid != null) {
            Workarounds.useGridShortcutHk(grid, glue.public_hotKey, event -> { if (glue.reevalEnabled()) { glue.startCommand(); } });
            if (overflowMenu != null) {
                overflowMenu.doubleClickAction = glue;
            }

        }


        return created;
    }
}
