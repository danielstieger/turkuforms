package org.modellwerkstatt.turkuforms.views;


import com.vaadin.flow.component.Component;
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
import org.modellwerkstatt.turkuforms.util.Peculiar;

import java.util.List;

import static org.modellwerkstatt.turkuforms.app.AppConfig.NO_HOKTEY;
import static org.modellwerkstatt.turkuforms.app.AppConfig.OK_HOKTEY;

public class PromptWindow extends Dialog {


    private ITurkuFactory uiFactory;
    private int langIndex;

    public PromptWindow(boolean restrictSize) {
        super();

        setModal(true);
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);

        setDraggable(false);
        setResizable(false);
        if (restrictSize) {
            setMaxWidth(80f, Unit.PERCENTAGE);
            setMaxHeight(90f, Unit.PERCENTAGE);
        }

        addClassName("PromptWindow");
        addThemeVariants(DialogVariant.LUMO_NO_PADDING);
    }

    public PromptWindow(ITurkuFactory factory, int langIndex) {
        this(true);
        this.uiFactory = factory;
        this.langIndex = langIndex;
    }

    public void rawPrompt(String heading, Component compt, String okButtonText, String cancelButtonTextOrNull, IApplicationController.DlgRunnable dlgRunnable) {

        Button okButton = new Button(uiFactory.translateButtonLabel(okButtonText ,OK_HOKTEY), event -> {
            this.close();
            if (dlgRunnable!=null) {
                dlgRunnable.run(true);
            }
        });
        Peculiar.useButtonShortcutHk(okButton, OK_HOKTEY);
        okButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        Button cancelButton = null;
        if (cancelButtonTextOrNull != null){
            cancelButton = new Button(uiFactory.translateButtonLabel(cancelButtonTextOrNull, NO_HOKTEY), event -> {
                this.close();
                if (dlgRunnable!=null) {
                    dlgRunnable.run(false);
                }
            });
            Peculiar.useButtonShortcutHk(cancelButton, NO_HOKTEY);
            cancelButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        }

        this.setHeaderTitle(heading);
        this.add(compt);

        if (cancelButton != null) { this.getFooter().add(cancelButton); }
        this.getFooter().add(okButton);
        this.open();
    }


    public void simplePrompt(IToolkit_Application.DlgType msgType, String text, IApplicationController.DlgRunnable dlgRunnable) {
        Div infoDiv = new Div();
        infoDiv.addClassName("PromptWindowInfoDiv");
        infoDiv.setText(text);


        String headingText = uiFactory.getSystemLabel(langIndex, MoWareTranslations.Key.INFORMATION);
        String okButtonText = uiFactory.getSystemLabel(langIndex,MoWareTranslations.Key.OK_BUTTON);
        String cancelButtonText = null;
        Component center = infoDiv;

        if (msgType == IToolkit_Application.DlgType.INFO_SMALL) {
            // default
        } else if (msgType == IToolkit_Application.DlgType.INFO_LARGE) {
            infoDiv.addClassName("PromptWindowLarge");
            this.setWidthFull();
            center = new Scroller(infoDiv);

        } else if (msgType == IToolkit_Application.DlgType.ERROR_SMALL) {
            headingText = uiFactory.getSystemLabel(langIndex, MoWareTranslations.Key.ERROR);

        } else if (msgType == IToolkit_Application.DlgType.ERROR_LARGE) {
            headingText = uiFactory.getSystemLabel(langIndex, MoWareTranslations.Key.ERROR);
            infoDiv.addClassName("PromptWindowLarge");
            this.setWidthFull();
            center = new Scroller(infoDiv);

        } else if (msgType == IToolkit_Application.DlgType.QUESTIONCLOSE_SMALL) {
            headingText = uiFactory.getSystemLabel(langIndex, MoWareTranslations.Key.QUESTION);
            infoDiv.addClassName("PromptWindowQuestionColor");
            okButtonText = uiFactory.getSystemLabel(langIndex,MoWareTranslations.Key.DISCARD_BUTTON);
            cancelButtonText = uiFactory.getSystemLabel(langIndex,MoWareTranslations.Key.NO_BUTTON);

        }

        rawPrompt(headingText, center, okButtonText, cancelButtonText, dlgRunnable);
    }


    public void simpleProblemDialog(List<IOFXProblem> list, IApplicationController.DlgRunnable dlgRunnable) {
        Div infoDiv = new Div();
        infoDiv.addClassName("PromptWindowInfoDiv");

        for(IOFXProblem problem: list){
            Div textDiv = new Div();
            textDiv.setText(problem.getSimpleUserText());
            if (problem.isWarningOnly()) {
                textDiv.addClassName("TurkuWarningDiv");
            } else {
                textDiv.addClassName("TurkuErrorDiv");
            }
            infoDiv.add(textDiv);
        }

        rawPrompt(uiFactory.getSystemLabel(langIndex, MoWareTranslations.Key.ERROR),
                 infoDiv,
                 uiFactory.getSystemLabel(langIndex,MoWareTranslations.Key.OK_BUTTON),
                null,
                 dlgRunnable);
    }
}
