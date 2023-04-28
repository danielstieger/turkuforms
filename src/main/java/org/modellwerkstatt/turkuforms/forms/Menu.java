package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
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
import org.modellwerkstatt.turkuforms.util.*;

import java.util.List;

public class Menu extends MenuBar {

    public Menu() {
        super();
        setOpenOnHover(false);

        // addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
    }


    public <T> void initialize(ITurkuFactory factory, MenuSub menu) {

        for (org.modellwerkstatt.dataux.runtime.genspecifications.MenuItem currentItem : menu.items) {
            if (currentItem instanceof MenuActionGlue) {
                // only overflow menu for context menu
                MenuItem button = addActionItem(factory, this, (MenuActionGlue) currentItem);

            } else if (currentItem.labelText == null) {
                // null is separator, ignore that here ...

            } else {
                MenuItem created = this.addItem(Workarounds.createIconWithCollection(factory.translateIconName("table_menu")));
                SubMenu createdSub = created.getSubMenu();
                addMainMenuStructure(factory, createdSub, ((MenuSub) currentItem).items);

            }
        }
    }



    static public <T> SubMenu addMainMenuStructure(ITurkuFactory turkuFactory,
                                                   SubMenu parent,
                                                   List<org.modellwerkstatt.dataux.runtime.genspecifications.MenuItem> menuItemList) {

        for (org.modellwerkstatt.dataux.runtime.genspecifications.MenuItem currentItem : menuItemList) {
            if (currentItem instanceof MenuActionGlue) {
                addActionItem(turkuFactory, parent, (MenuActionGlue) currentItem);

            } else if (currentItem.labelText == null) {
                // null is separator
                parent.add(new Hr());

            } else {
                MenuItem created = parent.addItem(currentItem.labelText);
                SubMenu createdSub = created.getSubMenu();

                addMainMenuStructure(turkuFactory, createdSub, ((MenuSub) currentItem).items);

            }
        }
        return parent;
    }




    static public <T> MenuItem addActionItem(ITurkuFactory turkuFactory, HasMenuItems parent, MenuActionGlue glue) {
        ComponentEventListener<ClickEvent<MenuItem>> execItem = event -> {
            event.getSource().setEnabled(false);
            glue.startCommand();
        };

        MenuItem created;

        if (Defs.hasIcon(glue.imageName)) {
            Icon icon = Workarounds.createIconWithCollection(turkuFactory.translateIconName(glue.imageName));
            icon.addClassName("TurkulayoutMenuIcon");
            created = parent.addItem(icon, execItem);
            created.add(new Text(turkuFactory.translateButtonLabel(glue.labelText, glue.public_hotKey)));

        } else {
            String label = turkuFactory.translateButtonLabel(glue.labelText, glue.public_hotKey);
            created = parent.addItem(label, execItem);

        }

        glue.attachButton1(new TurkuHasEnabled(created, "Menu " + glue.labelText));
        Tooltip t = Tooltip.forComponent(created);
        t.setText(Workarounds.mlToolTipText(glue.getToolTip()));

        return created;
    }
}
