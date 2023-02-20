package org.modellwerkstatt.turkuforms.util;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import org.modellwerkstatt.turkuforms.forms.TurkuTableCol;

import java.util.ArrayList;
import java.util.List;

public class Workarounds {

    public static String mlToolTipText(String tooltip){
        // \n is acceptable here for now, since we use
        // css property:   white-space: pre;
        return tooltip;
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

    public static boolean hasIcon(String iconname){
        return iconname != null && !"".equals(iconname);
    }

    public static boolean hasHk(String hkname){
        return hkname != null && !"".equals(hkname);
    }

    public static boolean hasLabel(String label){
        return label != null && !"".equals(label);
    }


    public static <DTO> void adjustColWidthToCheckbox(List<TurkuTableCol> columns) {
        List<TurkuTableCol> toAdjust = new ArrayList<>(columns);
        toAdjust.sort((t0, t1) -> Integer.compare(t1.widthInPercent, t0.widthInPercent));

        int alreadyAdjusted = 0;
        int MAX_TO_ADJUST = 4;
        for (int i = 0; i < toAdjust.size(); i++) {
            TurkuTableCol col = toAdjust.get(i);
            int diff = 0;
            int width = col.widthInPercent;

            if (width >= 40 && alreadyAdjusted < MAX_TO_ADJUST) {
                diff = (MAX_TO_ADJUST - alreadyAdjusted);

            } else if (width >= 30 && alreadyAdjusted < MAX_TO_ADJUST) {
                diff = 2;

            } else if (width >= 10 && alreadyAdjusted < MAX_TO_ADJUST) {
                diff = 1;
            }

            col.widthInPercent -= diff;
            alreadyAdjusted += diff;
        }
    }
}

