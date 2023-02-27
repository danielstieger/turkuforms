package org.modellwerkstatt.turkuforms.views;

import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.turkuforms.app.ITurkuFactory;

public class CmdUiPrompt extends CmdUi {
    protected PromptWindow promptWindow;
    protected boolean fullSize;


    public CmdUiPrompt(ITurkuFactory fact, boolean fullSize) {
        super(fact);
        this.fullSize = fullSize;

    }

    @Override
    public void show(String windowTitle, IToolkit_Form form) {
        promptWindow = new PromptWindow(true);
        promptWindow.add(this);
        initialShow(form);
        if (fullSize) {
            promptWindow.setSizeFull();
            this.setSizeFull();
        }
        promptWindow.open();
    }

    @Override
    public void close() {
        promptWindow.close();
        super.close();
    }

}
