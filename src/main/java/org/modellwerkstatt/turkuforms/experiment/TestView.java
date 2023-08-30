package org.modellwerkstatt.turkuforms.experiment;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import org.modellwerkstatt.turkuforms.components.DesktopGrid;

@Route("test")
public class TestView extends HorizontalLayout {


    public TestView() {
        super();

        DesktopGrid g =new DesktopGrid();
        add(g);

    }
}
