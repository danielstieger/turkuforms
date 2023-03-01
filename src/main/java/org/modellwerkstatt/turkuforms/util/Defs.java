package org.modellwerkstatt.turkuforms.util;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.ShortcutEvent;

import java.util.Set;

public class Defs {
    public static boolean hasIcon(String iconname) {
        return iconname != null && !"".equals(iconname);
    }

    public static boolean needsHkRegistration(String hkname) {
        return hkname != null && !"".equals(hkname) && !"UPD".equals(hkname) && !"GO".equals(hkname);
    }

    public static boolean hkNeedsCrtl(String hkname) {
        return hkname.length() == 1;
    }

    public static boolean hasLabel(String label) {
        return label != null && !"".equals(label);
    }

}
