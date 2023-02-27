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
    private String componentCssName;

    public LeftRight(String cssName) {
        super();
        componentCssName = cssName;
        flexLayout = new FlexLayout();
        flexLayout.addClassName("LeftRightFlex"+componentCssName);
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
        ((HasStyle) cmpt).addClassName("LeftRightFlexChild"+componentCssName);
        // ((HasSize) cmpt).setSizeUndefined();
        flexLayout.setAlignSelf(FlexComponent.Alignment.CENTER, cmpt);
        flexLayout.add(cmpt);
    }

    public void spacer() {
        Div spacer = new Div();
        spacer.addClassName("LeftRightFlexSpacer"+componentCssName);
        flexLayout.add(spacer);
        flexLayout.expand(spacer);
    }

    public void clear() {
        flexLayout.removeAll();
    }

}
