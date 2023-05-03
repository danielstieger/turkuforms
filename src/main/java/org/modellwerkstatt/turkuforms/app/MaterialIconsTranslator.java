package org.modellwerkstatt.turkuforms.app;

public class MaterialIconsTranslator extends VaadinIconTranslator {

    public MaterialIconsTranslator() {
        super();

        trans.put("new", "add_circle_outline");
        trans.put("edit", "create");
        trans.put("add", "add");
        trans.put("delete", "delete");
        trans.put("refresh", "refresh");
        trans.put("launch", "launch");
        trans.put("label", "label");
        trans.put("search", "search");
        trans.put("settings", "settings");
        trans.put("status", "status");
        trans.put("print", "print");
        trans.put("save", "save");

        // system icons ...
        trans.put("arrow_back", "keyboard_arrow_left"); // left ESC button
        trans.put("mainmenu_down", "keyboard_arrow_down");
        trans.put("mainmenu_logout", "power_settings_new");
        trans.put("mainmenu_adjust", "light_mode");
        trans.put("table_menu", "menu");
    }
}
