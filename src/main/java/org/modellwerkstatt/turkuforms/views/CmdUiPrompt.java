package org.modellwerkstatt.turkuforms.views;

import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.turkuforms.app.ITurkuFactory;

public class CmdUiPrompt extends CmdUi {

    public CmdUiPrompt(ITurkuFactory fact) {
        super(fact);
    }

    @Override
    public void show(String windowTitle, IToolkit_Form form) {
        initialShow(form);
    }

    @Override
    public void close() {
        super.close();
    }

}
