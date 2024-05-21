package org.modellwerkstatt.turkuforms.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_EnableItem;


public class TurkuHasEnabled implements IToolkit_EnableItem {
    private HasEnabled enabled_1;
    private String desc;
    private boolean hideWhenDisabled;

    public TurkuHasEnabled(boolean hide, HasEnabled item, String dbgItemDesc) {
        //TODO: Only needed in case we add context menu here.
        //      Otherwise remove and use anonymous IToolkit_EnableItem
        enabled_1 = item;
        desc = dbgItemDesc;
        hideWhenDisabled = hide;
    }

    @Override
    public void setItemEnabled(boolean b) {
        enabled_1.setEnabled(b);
        if (hideWhenDisabled) {
            ((Component) enabled_1).setVisible(b);
        }
    }
}
