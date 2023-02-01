package org.modellwerkstatt.turkuforms.app;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import org.modellwerkstatt.turkuforms.infra.TurkuLog;


public class TestView extends HorizontalLayout implements BeforeEnterObserver, BeforeLeaveObserver, Runnable {
    private Paragraph mainP;
    private int counter = 0;
    private UI ui;


    public TestView() {
        super();
        this.add(new Label("TestView.class"));

        mainP = new Paragraph();
        this.add(mainP);
        mainP.setText("TestView.constructor()");

        ui = UI.getCurrent();

        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        for (int i=0; i < 20; i++) {
            counter ++;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            ui.access(() -> {
                mainP.setText("Counter is " + counter);
            });
        }
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
