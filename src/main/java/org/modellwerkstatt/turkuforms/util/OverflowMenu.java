package org.modellwerkstatt.turkuforms.util;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.shared.Tooltip;
import org.modellwerkstatt.dataux.runtime.genspecifications.MenuActionGlue;
import org.modellwerkstatt.dataux.runtime.genspecifications.MenuSub;
import org.modellwerkstatt.turkuforms.app.ITurkuFactory;

import java.util.List;

public class OverflowMenu extends MenuBar {


    public OverflowMenu() {
        super();
        addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
    }


    public void initialize(ITurkuFactory factory, MenuSub menu){

        for (org.modellwerkstatt.dataux.runtime.genspecifications.MenuItem currentItem : menu.items) {
            if (currentItem instanceof MenuActionGlue) {
                createActionItem(factory, this, (MenuActionGlue) currentItem);

            } else {
                if (currentItem.labelText == null) {
                    // null is separator, ignore that here ...

                } else {
                    MenuItem created = this.addItem(Workarounds.createIconWithCollection(factory.translateIconName("table_menu")));
                    SubMenu createdSub = created.getSubMenu();
                    createMainMenuStructure(factory, createdSub,((MenuSub) currentItem).items);
                }
            }
        }
    }



    static public SubMenu createMainMenuStructure(ITurkuFactory turkuFactory, SubMenu parent, List<org.modellwerkstatt.dataux.runtime.genspecifications.MenuItem> menuItemList) {

        for (org.modellwerkstatt.dataux.runtime.genspecifications.MenuItem currentItem : menuItemList) {
            if (currentItem instanceof MenuActionGlue) {
                createActionItem(turkuFactory, parent, (MenuActionGlue) currentItem);
            } else {
                if (currentItem.labelText == null) {
                    // null is separator
                    parent.add(new Hr());

                } else {
                    MenuItem created = parent.addItem(currentItem.labelText);
                    SubMenu createdSub = created.getSubMenu();
                    createMainMenuStructure(turkuFactory, createdSub,((MenuSub) currentItem).items);
                }
            }
        }
        return parent;
    }




    static public MenuItem createActionItem(ITurkuFactory turkuFactory, HasMenuItems parent, MenuActionGlue glue) {

        ComponentEventListener<ClickEvent<MenuItem>> execItem = event -> {
            event.getSource().setEnabled(false);
            glue.startCommand();
        };

        MenuItem created;

        if (Workarounds.hasIcon(glue.imageName)) {
            Icon icn = Workarounds.createIconWithCollection(turkuFactory.translateIconName(glue.imageName));
            icn.addClassName("TurkulayoutMenuIcon");
            created = parent.addItem(icn, execItem);
            created.add(new Text(turkuFactory.translateButtonLabel(glue.labelText, glue.public_hotKey)));

        } else {
            created = parent.addItem(turkuFactory.translateButtonLabel(glue.labelText, glue.public_hotKey), execItem);

        }

        glue.attachButton1(new TurkuHasEnabled(created));

        Tooltip t = Tooltip.forComponent(created);
        t.setText(Workarounds.mlToolTipText(glue.getToolTip()));

        return created;
    }
}
