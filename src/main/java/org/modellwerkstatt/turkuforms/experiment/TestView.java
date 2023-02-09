package org.modellwerkstatt.turkuforms.experiment;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import org.modellwerkstatt.turkuforms.util.Turku;


@JsModule("./turku.js")
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

        ui.getPage().executeJs("turku.init()");

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
        Turku.l("TestView.beforeLeave(): "+ event);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Turku.l("TestView.beforeEnter(): "+ event);

    }
}
