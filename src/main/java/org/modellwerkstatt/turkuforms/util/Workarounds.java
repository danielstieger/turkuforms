package org.modellwerkstatt.turkuforms.util;

import com.vaadin.flow.component.icon.Icon;

public class Workarounds {

    public static String mlToolTipText(String tooltip){
        // \n is acceptable here for now, since we use
        // css property:   white-space: pre;
        return tooltip;
    }

    public static boolean hasIcon(String iconname){
        return iconname != null && !"".equals(iconname);
    }

    public static Icon createIconWithCollection(String fullName) {
        try {
            if (fullName.contains(":")) {
                String[] parts = fullName.split(":");
                return new Icon(parts[0], parts[1]);
            }
            return new Icon("vaadin", fullName);

        } catch (Throwable t) {
            throw new RuntimeException("While looking for icon '" + fullName + "'", t);
        }
    }

    public static boolean hasHk(String hkname){
        return hkname != null && !"".equals(hkname);
    }
}

