package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class LeftRight2 extends HorizontalLayout {



    public LeftRight2(String cssName) {
        super();

        if (!"".equals(cssName)) {
            this.addClassName(cssName);
        }

        this.setSizeFull();
    }

    public LeftRight2(){
        this("");
    }

    public void add(Component cmpt){
        super.add(cmpt);
    }

    public void spacer() {
        Div spacer = new Div();
        this.add(spacer);
        this.expand(spacer);
    }

    public void clear() {
        this.removeAll();
    }
}