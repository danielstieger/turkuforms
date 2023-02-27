package org.modellwerkstatt.turkuforms.views;


import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.Scroller;
import org.modellwerkstatt.dataux.runtime.core.IApplicationController;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Application;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.turkuforms.app.ITurkuFactory;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.util.List;

public class PromptWindow extends Dialog {
    private ITurkuFactory uiFactory;
    private int langIndex;

    public PromptWindow(boolean eightPercent) {
        super();

        setModal(true);
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);

        setDraggable(false);
        setResizable(false);
        if (eightPercent) {
            setMaxWidth(80f, Unit.PERCENTAGE);
        }

        addClassName("PromptWindow");
        addThemeVariants(DialogVariant.LUMO_NO_PADDING);
    }

    public PromptWindow(ITurkuFactory factory, int langIndex) {
        this(false);
        this.uiFactory = factory;
        this.langIndex = langIndex;
    }

    public void simplePrompt(IToolkit_Application.DlgType msgType, String text, IApplicationController.DlgRunnable dlgRunnable) {
        Div infoDiv = new Div();
        infoDiv.addClassName("PromptWindowInfoDiv");
        infoDiv.setText(text);

        Button okButton = new Button(uiFactory.getSystemLabel(langIndex,MoWareTranslations.Key.OK_BUTTON), event -> {
            this.close();
            if (dlgRunnable!=null) {
                dlgRunnable.run(true);
            }
        });
        Workarounds.useButtonShortcutHk(okButton, "F12");
        okButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        if (msgType == IToolkit_Application.DlgType.INFO_SMALL) {
            this.setHeaderTitle(uiFactory.getSystemLabel(langIndex, MoWareTranslations.Key.INFORMATION));
            this.add(infoDiv);

        } else if (msgType == IToolkit_Application.DlgType.INFO_LARGE) {
            this.setHeaderTitle(uiFactory.getSystemLabel(langIndex, MoWareTranslations.Key.INFORMATION));
            this.add(infoDiv);

        } else if (msgType == IToolkit_Application.DlgType.ERROR_SMALL) {
            this.setHeaderTitle(uiFactory.getSystemLabel(langIndex, MoWareTranslations.Key.ERROR));
            this.add(infoDiv);

        } else if (msgType == IToolkit_Application.DlgType.ERROR_LARGE) {
            this.setHeaderTitle(uiFactory.getSystemLabel(langIndex, MoWareTranslations.Key.ERROR));
            infoDiv.addClassName("PromptWindowErrorLarge");
            Scroller scroller = new Scroller(infoDiv);
            this.add(scroller);

        } else if (msgType == IToolkit_Application.DlgType.QUESTIONCLOSE_SMALL) {
            this.setHeaderTitle(uiFactory.getSystemLabel(langIndex, MoWareTranslations.Key.QUESTION));
            infoDiv.addClassName("PromptWindowQuestionColor");
            this.add(infoDiv);
            okButton.setText(uiFactory.getSystemLabel(langIndex,MoWareTranslations.Key.DISCARD_BUTTON));

            Button cancelButton = new Button(uiFactory.getSystemLabel(langIndex,MoWareTranslations.Key.NO_BUTTON), event -> {
                this.close();
                if (dlgRunnable!=null) {
                    dlgRunnable.run(false);
                }
            });
            Workarounds.useButtonShortcutHk(cancelButton, "ESC");
            cancelButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);

            this.getFooter().add(cancelButton);
        }

        this.getFooter().add(okButton);
        this.open();
    }


    public void simpleProblemDialog(List<IOFXProblem> list, IApplicationController.DlgRunnable dlgRunnable) {
        this.setHeaderTitle(uiFactory.getSystemLabel(langIndex, MoWareTranslations.Key.ERROR));

        Button okButton = new Button(uiFactory.getSystemLabel(langIndex,MoWareTranslations.Key.OK_BUTTON), event -> {
            this.close();
            if (dlgRunnable!=null) {
                dlgRunnable.run(true);
            }
        });
        okButton.addThemeVariants(ButtonVariant.LUMO_SMALL);


        Div infoDiv = new Div();
        infoDiv.addClassName("PromptWindowInfoDiv");

        for(IOFXProblem problem: list){
            Div textDiv = new Div();
            textDiv.setText(problem.getSimpleUserText());
            if (problem.isWarningOnly()) {
                textDiv.addClassName("TurkuWarningColor");
            } else {
                textDiv.addClassName("TurkuErrorColor");
            }
            infoDiv.add(textDiv);
        }

        this.add(infoDiv);
        this.getFooter().add(okButton);
        this.open();
    }
}
