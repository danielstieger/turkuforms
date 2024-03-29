package org.modellwerkstatt.turkuforms.core;

import java.util.HashMap;

public class VaadinIconTranslator {
    public HashMap<String, String> trans = new HashMap<String, String>();

    public VaadinIconTranslator() {
        trans.put("new", "vaadin:file-add");
        trans.put("edit", "vaadin:pencil");
        trans.put("add", "vaadin:plus-square-o");
        trans.put("delete", "vaadin:trash");
        trans.put("refresh", "vaadin:refresh");
        trans.put("launch", "vaadin:external-link");
        trans.put("label", "vaadin:tab-a");
        trans.put("search", "vaadin:search");
        trans.put("settings", "vaadin:cog-o");
        trans.put("status", "vaadin:file-o");
        trans.put("print", "vaadin:print");
        trans.put("save", "vaadin:database ");

        // system icons ...
        trans.put("arrow_back", "vaadin:caret-left"); // left ESC button
        trans.put("mainmenu_down", "vaadin:caret-down");
        trans.put("mainmenu_logout", "vaadin:power-off");
        trans.put("mainmenu_adjust", "vaadin:adjust");
        trans.put("table_menu", "vaadin:menu");

    }

    public String translate(String iconName) {
        if (trans.containsKey(iconName)) {
            return trans.get(iconName);
        }
        return iconName;
    }
}

