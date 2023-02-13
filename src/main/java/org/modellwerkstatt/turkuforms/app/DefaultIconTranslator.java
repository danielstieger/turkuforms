package org.modellwerkstatt.turkuforms.app;

import java.util.HashMap;

public class DefaultIconTranslator {
    public HashMap<String, String> trans = new HashMap<String, String>();

    public DefaultIconTranslator() {
        trans.put("new", "vaadin:file-add");
        trans.put("edit", "vaadin:pencil");
        trans.put("add", "vaadin:plus-square-o");
        trans.put("delete", "vaadin:trash");
        trans.put("refresh", "vaadin:refresh");
        trans.put("launch", "vaadin:external-link");
        trans.put("label", "vaadin:tab-a");
        trans.put("search", "vaadin:search");
        trans.put("new", "vaadin:file-add");
        trans.put("settings", "vaadin:cog-o");
        trans.put("status", "vaadin:file-o");
        trans.put("print", "vaadin:print");
        trans.put("save", "vaadin:database ");
        trans.put("logout", "vaadin:power-off");
    }

    public String translate(String iconName) {
        if (trans.containsKey(iconName)) {
            return trans.get(iconName);
        }
        return iconName;
    }
}

