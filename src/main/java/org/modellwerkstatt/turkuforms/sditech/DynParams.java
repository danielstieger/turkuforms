package org.modellwerkstatt.turkuforms.sditech;

import com.vaadin.flow.router.Location;

import java.util.List;


public class DynParams {
    private final static boolean SKIPFIRST = true;

    List<String> segments;

    public DynParams(Location location) {
        segments = location.getSegments();
    }

    public boolean hasCmdName() {
        if (get(0) == null) {
            return false;
        }
        return true;
    }

    public String get(int index){
        if (SKIPFIRST) { index ++; }

        if (index < segments.size()) {
            return segments.get(index);

        } else {
            return null;

        }
    }
}
