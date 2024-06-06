package org.modellwerkstatt.turkuforms.sditech;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import org.modellwerkstatt.turkuforms.sditech.uis.CmdUi;
import org.modellwerkstatt.turkuforms.util.Turku;



public class SdiTestWindow extends VerticalLayout implements BeforeEnterObserver, BeforeLeaveObserver, IToolkit_Window {

    private Label contentLabel;

    public SdiTestWindow() {
        super();
        this.add(new Label("Turku v2 - the SDI tech (on ui " + UI.getCurrent() + ")"));
     }

    @Override
    public void setMessage(String message) {
        this.add(new Label("Message: " + message));
    }

    @Override
    public void askQuestion(String question) {
        this.add(new Label("Question " + question));
    }

    @Override
    public void addCmd(CmdUi ui) {
        this.add(ui);
    }

    @Override
    public void setContent() {

    }

    @Override
    public void openPropmpt() {

    }

    @Override
    public void toggleDisable() {
        this.add(new Label("Now, i will be " + (!this.isEnabled()) ));
        this.setEnabled(! this.isEnabled() );
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        Turku.l("SdiTestView.beforeLeave(): "+ event);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        SdiAppCrtl crtl = Hacks.getCrtl();
        if (crtl == null) {
            event.forwardTo("/v2/login?reroute=" + event.getLocation().getPath());

        } else {
            crtl.startCmdByParams(this, new DynParams(event.getLocation()));

        }
    }
}
