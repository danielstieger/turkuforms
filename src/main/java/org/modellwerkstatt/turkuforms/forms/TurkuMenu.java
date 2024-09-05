package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.shared.Tooltip;
import org.modellwerkstatt.dataux.runtime.genspecifications.AbstractAction;
import org.modellwerkstatt.dataux.runtime.genspecifications.CmdAction;
import org.modellwerkstatt.dataux.runtime.genspecifications.Menu;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.util.Defs;
import org.modellwerkstatt.turkuforms.util.TurkuHasEnabled;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.util.List;

public class TurkuMenu extends MenuBar {

    public TurkuMenu() {
        super();
        setOpenOnHover(false);
        addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        addClassName("TurkuFormMenu");
    }


    public <T> void initialize(ITurkuAppFactory factory, Menu menu) {

        for (AbstractAction currentItem : menu.getAllItems()) {
            if (currentItem instanceof CmdAction) {
                CmdAction action = (CmdAction) currentItem;

                if (action.isGraphEdit || factory.cmdAccessible(action.commandFqName)){
                    MenuItem button = addActionItem(factory, this, action, true);
                    button.addThemeNames("tertiary", "small");
                }

            } else if (currentItem.labelText == null) {
                // null is separator, ignore that here ...

            } else {
                MenuItem created = this.addItem(Workarounds.createIconWithCollection(factory.translateIconName("table_menu"), true));
                created.addThemeNames("tertiary", "small");
                if (Defs.hasLabel(currentItem.labelText)) { created.add(new Text(currentItem.labelText)); }
                SubMenu createdSub = created.getSubMenu();
                addMainMenuStructure(factory, createdSub, ((Menu) currentItem).getAllItems());

            }
        }
    }



    static public <T> SubMenu addMainMenuStructure(ITurkuAppFactory turkuFactory,
                                                   SubMenu parent,
                                                   List<AbstractAction> menuItemList) {

        for (AbstractAction currentItem : menuItemList) {
            if (currentItem instanceof CmdAction) {
                CmdAction action = (CmdAction) currentItem;
                if (action.isGraphEdit || turkuFactory.cmdAccessible(action.commandFqName)) {
                    addActionItem(turkuFactory, parent, (CmdAction) currentItem, false);

                }

            } else if (currentItem.labelText == null) {
                // null is separator
                parent.add(new Hr());

            } else {
                MenuItem created = parent.addItem(currentItem.labelText);
                SubMenu createdSub = created.getSubMenu();

                addMainMenuStructure(turkuFactory, createdSub, ((Menu) currentItem).getAllItems());

            }
        }
        return parent;
    }




    static public <T> MenuItem addActionItem(ITurkuAppFactory turkuFactory, HasMenuItems parent, CmdAction glue, boolean topLevel) {
        ComponentEventListener<ClickEvent<MenuItem>> execItem = event -> {
            event.getSource().setEnabled(false);
            glue.startCommand();
        };

        MenuItem created;
        String label = glue.labelText;
        if (! topLevel) { label = turkuFactory.translateButtonLabel(glue.labelText, glue.hotKey); }


        if (Defs.hasIcon(glue.image)) {
            Component icon = Workarounds.createIconWithCollection(turkuFactory.translateIconName(glue.image), topLevel);
            created = parent.addItem(icon, execItem);
            created.add(new Text(label));

        } else {
            created = parent.addItem(label, execItem);

        }

        glue.attachButton1(new TurkuHasEnabled(glue.hideWhenDisabled, created, "Menu " + glue.labelText));
        Tooltip t = Tooltip.forComponent(created);
        t.setText(Workarounds.mlToolTipText(glue.getToolTip()));

        return created;
    }
}
