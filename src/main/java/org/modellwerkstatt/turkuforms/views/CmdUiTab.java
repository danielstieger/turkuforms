package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.HasSize;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.util.Peculiar;

public class CmdUiTab extends CmdUi {
    private boolean isModal;
    private String windowTitle;
    private ITurkuMainTab mainTab;


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

    public void setMainTabForColorAdjustments(ITurkuMainTab main) {
        mainTab = main;
    }

    @Override
    public void setColor(String col) {
        super.setColor(col);
        if (mainTab != null) { mainTab.adjustTabStyle(this, col); }
    }

    @Override
    public void delayedRequestFocus() {
        // getUI().get().getPage().getHistory().replaceState(null, "home/" + hashCode());
        // und history Push State

        if (mainTab != null) { mainTab.adjustTopBarColorOrNull(color); }
        super.delayedRequestFocus();
    }

    @Override
    public boolean isModalTabWindow() {
        return isModal;
    }

    public String getWindowTitle() { return windowTitle;}
}
