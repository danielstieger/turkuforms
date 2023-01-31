package org.modellwerkstatt.turkuforms.app;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import org.modellwerkstatt.turkuforms.infra.TurkuLog;


public class TestView extends HorizontalLayout implements BeforeEnterObserver, BeforeLeaveObserver {
    private Paragraph mainP;


    public TestView() {
        super();
        this.add(new Label("TestView.class"));

        mainP = new Paragraph();
        this.add(mainP);
        mainP.setText("TestView.constructor()");
    }


    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        TurkuLog.l("TestView.beforeLeave(): "+ event);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        TurkuLog.l("TestView.beforeEnter(): "+ event);

    }
}
