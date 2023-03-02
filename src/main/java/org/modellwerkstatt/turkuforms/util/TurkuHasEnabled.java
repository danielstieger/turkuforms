package org.modellwerkstatt.turkuforms.util;

import com.vaadin.flow.component.HasEnabled;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_EnableItem;

import java.awt.*;


public class TurkuHasEnabled implements IToolkit_EnableItem {
    private HasEnabled enabled_1;
    private String desc;

    public TurkuHasEnabled(HasEnabled item, String dbgItemDesc) {
        //TODO: Only needed in case we add context menu here.
        //      Otherwise remove and use anonymous IToolkit_EnableItem
        enabled_1 = item;
        desc = dbgItemDesc;
    }

    @Override
    public void setItemEnabled(boolean b) {
        Turku.l("TurkuHasEnabled reevaluate to " + b + " for "+ desc);
        enabled_1.setEnabled(b);
    }
}
