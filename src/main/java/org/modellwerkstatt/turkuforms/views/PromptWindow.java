package org.modellwerkstatt.turkuforms.views;


import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;

public class PromptWindow extends Dialog {

    public PromptWindow() {
        super();

        setModal(true);
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);

        setDraggable(false);
        setResizable(false);
        setMaxWidth(80f, Unit.PERCENTAGE);
        addClassName("PromptWindow");

        addThemeVariants(DialogVariant.LUMO_NO_PADDING);
    }
}
