package org.modellwerkstatt.turkuforms.sditech;

import org.modellwerkstatt.turkuforms.sditech.uis.CmdUi;

public interface IToolkit_Window {

    void setMessage(String message);
    void askQuestion(String question);
    void setContent();
    void openPropmpt();

    void addCmd(CmdUi ui);

    void toggleDisable();
}
