package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.html.Div;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.Workarounds2;

import java.util.List;

public class FormHeading extends Div {
    private Div titleDiv;
    private Div subTitleDiv;

    private String[] splittedText = new String[]{"", null};

    public FormHeading() {
        this.addClassName("FormHeading");
        titleDiv = new Div();
        titleDiv.addClassName("FormHeadingTitle");

        this.add(titleDiv);
    }

    public void inRootPos() {
        this.getElement().setAttribute("pageTitle", "true");
    }

    public void setHeading(String label) {
        splittedText = Workarounds2.getMainAndSubTitle(label);
        titleDiv.setText(splittedText[0]);

        if (splittedText[1] != null) {
            if (subTitleDiv == null) {
                subTitleDiv = new Div();
                subTitleDiv.addClassName("FormHeadingSubTitle");
                this.add(subTitleDiv);
            }
            subTitleDiv.setText(splittedText[1]);

        } else if (subTitleDiv != null) {
            this.remove(subTitleDiv);
            subTitleDiv = null;

        }
    }

    public void flag(List<IOFXProblem> problemList) {

        this.removeAll();

        if (titleDiv != null) {
            this.add(titleDiv);
        }

        if (subTitleDiv != null) {
            this.add(subTitleDiv);
        }

        for (IOFXProblem prblm: problemList) {
            Div info = new Div();
            info.setText(prblm.getSimpleUserText());
            info.addClassName(prblm.isWarningOnly() ? "TurkuWarningDiv" : "TurkuErrorDiv");

            this.add(info);
        }


    }



}
