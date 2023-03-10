package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.ThemableLayout;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.turkuforms.app.ITurkuFactory;
import org.modellwerkstatt.turkuforms.util.Workarounds;

public class CmdUiTab extends CmdUi {
    private boolean isModal;
    private String windowTitle;


    public CmdUiTab(ITurkuFactory fact, boolean modal) {
        super(fact);
        Workarounds.shrinkSpace(this);

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
    public boolean isModalTabWindow() {
        return isModal;
    }

    public String getWindowTitle() { return windowTitle;}
}
