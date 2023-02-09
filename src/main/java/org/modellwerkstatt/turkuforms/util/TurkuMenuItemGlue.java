package org.modellwerkstatt.turkuforms.util;

import com.vaadin.flow.component.contextmenu.MenuItem;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_EnableItem;


public class TurkuMenuItemGlue implements IToolkit_EnableItem {
    private MenuItem menuItem;

    public TurkuMenuItemGlue(MenuItem item) {
        menuItem = item;
    }

    @Override
    public void setItemEnabled(boolean b) {
        menuItem.setEnabled(b);
    }
}
