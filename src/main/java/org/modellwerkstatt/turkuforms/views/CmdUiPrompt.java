package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.HasSize;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;

public class CmdUiPrompt extends CmdUi {
    protected PromptWindow promptWindow;
    protected boolean fullSize;


    public CmdUiPrompt(ITurkuAppFactory fact, boolean fullSize) {
        super(fact);
        setClassName("CmdUiPrompt");
        this.fullSize = fullSize;

    }

    @Override
    public void adjustWindowTitle(String s) {
        // noop for prompt windows
    }

    @Override
    public void show(String windowTitle, IToolkit_Form form) {
        promptWindow = new PromptWindow(true);
        promptWindow.setMinWidth("50%");
        promptWindow.add(this);

        initialShow(form);

        if (fullSize) {
            promptWindow.setSizeFull();
            this.setSizeFull();
            ((HasSize) form).setHeightFull();

        }

        promptWindow.open();
    }

    @Override
    public void setContent(IToolkit_Form formAsComponent) {
        super.setContent(formAsComponent);
        if (fullSize) {
            ((HasSize) formAsComponent).setHeightFull();
        }
    }

    @Override
    public void close() {
        promptWindow.close();
        super.close();
    }

}
