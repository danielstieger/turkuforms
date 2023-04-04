package org.modellwerkstatt.turkuforms.experiment;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import org.modellwerkstatt.turkuforms.components.DesktopGrid;
import org.modellwerkstatt.turkuforms.util.Turku;

@Route("test")
public class TestView extends HorizontalLayout {


    public TestView() {
        super();

        DesktopGrid g =new DesktopGrid();
        add(g);

    }
}
