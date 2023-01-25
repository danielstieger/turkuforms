package org.modellwerkstatt.turkuforms.app;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class TestView extends HorizontalLayout {

    public TestView() {
        super();
        this.add(new Label("Dan Test"));

    }
}
