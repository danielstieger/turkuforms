package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class LeftRight extends Composite<Component> {
    private FlexLayout flexLayout;

    private final static String LEFTRIGHT_FLEX_GAPS = "var(--lumo-space-m)";


    public LeftRight(String cssName) {
        super();

        flexLayout = new FlexLayout();

        flexLayout.addClassName("LeftRightFlex");
        if (!"".equals(cssName)) {
            flexLayout.addClassName(cssName);
        }

        flexLayout.setWidthFull();
        flexLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        flexLayout.getStyle().set("column-gap", LEFTRIGHT_FLEX_GAPS);
    }

    public LeftRight(){
        this("");
    }


    @Override
    protected Component initContent() {
        return flexLayout;
    }

    public void add(Component cmpt){
        ((HasStyle) cmpt).addClassName("LeftRightFlexChild");
        // ((HasSize) cmpt).setSizeUndefined();
        // flexLayout.setAlignSelf(FlexComponent.Alignment.CENTER, cmpt);

        flexLayout.add(cmpt);
    }

    public void spacer() {
        Div spacer = new Div();
        spacer.addClassName("LeftRightFlexSpacer");
        flexLayout.add(spacer);
        flexLayout.expand(spacer);
    }

    public void clear() {
        flexLayout.removeAll();
    }

}
