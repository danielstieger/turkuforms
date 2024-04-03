package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.HasSize;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.util.Peculiar;

public class CmdUiTab extends CmdUi {
    private boolean isModal;
    private String windowTitle;


    public CmdUiTab(ITurkuAppFactory fact, boolean modal) {
        super(fact);
        Peculiar.shrinkSpace(this);
        setClassName("CmdUiTab");
        isModal = modal;
    }

    @Override
    public void show(String winTitle, IToolkit_Form form) {
        windowTitle = winTitle;

        this.setSizeFull();
        ((HasSize) form).setSizeFull();
        initialShow(form);
    }

    @Override
    public void setContent(IToolkit_Form formAsComponent) {
        ((HasSize) formAsComponent).setSizeFull();
        super.setContent(formAsComponent);
    }

    @Override
    public void delayedRequestFocus() {
        ((ITurkuMainTab) this.getParent().get()).adjustStyleDynamically(this, color);
        super.delayedRequestFocus();
    }

    @Override
    public boolean isModalTabWindow() {
        return isModal;
    }

    public String getWindowTitle() { return windowTitle;}

    public String getCmdColor() { return color; }
}
