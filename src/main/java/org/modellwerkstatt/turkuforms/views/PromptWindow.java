package org.modellwerkstatt.turkuforms.views;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.Scroller;
import org.modellwerkstatt.dataux.runtime.core.IApplication;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_MainWindow;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.util.Peculiar;

import java.util.List;

import static org.modellwerkstatt.turkuforms.core.MPreisAppConfig.NO_HOKTEY;
import static org.modellwerkstatt.turkuforms.core.MPreisAppConfig.OK_HOKTEY;

public class PromptWindow extends Dialog {

    private ITurkuAppFactory uiFactory;
    private int langIndex;

    public PromptWindow(boolean restrictSize) {
        super();

        setModal(true);
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);

        setDraggable(false);
        setResizable(false);
        if (restrictSize) {
            setMaxWidth(98f, Unit.PERCENTAGE);
            setMaxHeight(99f, Unit.PERCENTAGE);
        }

        addClassName("PromptWindow");
        addThemeVariants(DialogVariant.LUMO_NO_PADDING);
    }

    public PromptWindow(ITurkuAppFactory factory, int langIndex) {
        this(true);
        this.uiFactory = factory;
        this.langIndex = langIndex;
    }

    public void rawPrompt(String heading, Component compt, String okButtonText, String cancelButtonTextOrNull, IApplication.DlgRunnable dlgRunnable) {

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


    public void simplePrompt(IToolkit_MainWindow.DlgType msgType, String text, IApplication.DlgRunnable dlgRunnable) {
        this.setMinWidth("40%");

        Div infoDiv = new Div();
        infoDiv.addClassName("PromptWindowInfoDiv");
        infoDiv.setText(text);


        String headingText = uiFactory.getSystemLabel(langIndex, MoWareTranslations.Key.INFORMATION);
        String okButtonText = uiFactory.getSystemLabel(langIndex,MoWareTranslations.Key.OK_BUTTON);
        String cancelButtonText = null;
        Component center = infoDiv;

        if (msgType == IToolkit_MainWindow.DlgType.INFO_SMALL) {
            // default

        } else if (msgType == IToolkit_MainWindow.DlgType.INFO_LARGE) {
            infoDiv.addClassName("PromptWindowLarge");
            this.setWidthFull();
            center = new Scroller(infoDiv);

            Button copyCsv = new Button("Copy", event -> {
                UI.getCurrent().getPage().executeJs("turku.copyToClipboard($0, $1)", this, text);
            });
            copyCsv.getStyle().set("margin-right", "auto");
            copyCsv.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            copyCsv.addThemeVariants(ButtonVariant.LUMO_SMALL);
            this.getFooter().add(copyCsv);

        } else if (msgType == IToolkit_MainWindow.DlgType.ERROR_SMALL) {
            headingText = uiFactory.getSystemLabel(langIndex, MoWareTranslations.Key.ERROR);

        } else if (msgType == IToolkit_MainWindow.DlgType.ERROR_LARGE) {
            headingText = uiFactory.getSystemLabel(langIndex, MoWareTranslations.Key.ERROR);
            infoDiv.addClassName("PromptWindowLarge");
            this.setWidthFull();
            center = new Scroller(infoDiv);

        } else if (msgType == IToolkit_MainWindow.DlgType.QUESTIONCLOSE_SMALL) {
            headingText = uiFactory.getSystemLabel(langIndex, MoWareTranslations.Key.QUESTION);
            infoDiv.addClassName("PromptWindowQuestion");
            okButtonText = uiFactory.getSystemLabel(langIndex,MoWareTranslations.Key.DISCARD_BUTTON);
            cancelButtonText = uiFactory.getSystemLabel(langIndex,MoWareTranslations.Key.NO_BUTTON);

        }

        rawPrompt(headingText, center, okButtonText, cancelButtonText, dlgRunnable);
    }


    public void simpleProblemDialog(List<IOFXProblem> list, IApplication.DlgRunnable dlgRunnable) {
        this.setMinWidth("40%");
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

        rawPrompt(uiFactory.getSystemLabel(langIndex, MoWareTranslations.Key.INFORMATION),
                 infoDiv,
                 uiFactory.getSystemLabel(langIndex,MoWareTranslations.Key.OK_BUTTON),
                null,
                 dlgRunnable);
    }
}
