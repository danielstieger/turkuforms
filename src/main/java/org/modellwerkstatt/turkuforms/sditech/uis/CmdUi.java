package org.modellwerkstatt.turkuforms.sditech.uis;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.modellwerkstatt.turkuforms.sditech.Cmd;
import org.modellwerkstatt.turkuforms.sditech.EventCommandContainer;
import org.modellwerkstatt.turkuforms.sditech.SdiAppCrtl;

public class CmdUi extends VerticalLayout {
    private Cmd cmd;

    public CmdUi(SdiAppCrtl crtl, EventCommandContainer ecc, Cmd aCmd) {
        cmd = aCmd;

        add(new Label("USER " + crtl.userName));
        add(new Label(cmd.toString()));

        add(new Button("Conclusion", buttonClickEvent -> {

            Notification.show("Calling conclusion on " + cmd);

        }));

        String cmdName = "cmd_" + crtl.numCommands();
        add(new Button("Open " + cmdName, buttonClickEvent -> {
            UI.getCurrent().getPage().open("/simpleone/v2/" + cmdName + "/4711/23", "_blank");
        }));


        add(new Button("Toggle Disable", buttonClickEvent -> {

            Notification.show("Disabling others");
            crtl.disable(ecc);
        }));

        add(new Button("Set data to " + cmdName, buttonClickEvent -> {

            Notification.show("Setting data to " + cmdName);
            crtl.adjustData(cmdName);
        }));


        add(new Button("Close window ", buttonClickEvent -> {

            this.getElement().executeJs("turku.closeTab()");
            Notification.show("Close executed ..");
        }));



    }
}
