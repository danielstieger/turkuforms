package org.modellwerkstatt.turkuforms.sditech;

import org.modellwerkstatt.turkuforms.sditech.uis.CmdUi;

public class EventCommandContainer {
    private IToolkit_Page page;
    private Cmd command;
    private SdiAppCrtl crtl;

    public EventCommandContainer(SdiAppCrtl crtl, IToolkit_Page page, Cmd cmd) {
        this.page = page;
        this.command = cmd;
        this.crtl = crtl;
    }

    public void exec() {
        page.addCmd(new CmdUi(crtl, this, command));
    }

    public void toggleDisable() {
        page.toggleDisable();
    }

    public void showMessage(String message) {
        page.setMessage(message);
    }


}
