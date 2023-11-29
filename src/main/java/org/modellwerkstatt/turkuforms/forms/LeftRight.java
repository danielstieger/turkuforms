package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class LeftRight extends FlexLayout {

    private final static String LEFTRIGHT_FLEX_GAPS = "var(--lumo-space-m)";


    public LeftRight(String cssName) {
        super();

        this.addClassName("LeftRightFlex");
        if (!"".equals(cssName)) {
            this.addClassName(cssName);
        }

        this.setWidthFull();
        this.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        this.getStyle().set("column-gap", LEFTRIGHT_FLEX_GAPS);
    }

    public LeftRight(){
        this("");
    }

    public void add(Component cmpt){
        ((HasStyle) cmpt).addClassName("LeftRightFlexChild");
        super.add(cmpt);
    }

    public void spacer() {
        Div spacer = new Div();
        spacer.addClassName("LeftRightFlexSpacer");
        this.add(spacer);
        this.expand(spacer);
    }

    public void clear() {
        this.removeAll();
    }
}