package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.html.Div;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;

import java.util.List;

public class FormHeading extends Div {
    private Div heading;
    private int hLevel = 0;
    private boolean pageTitle = false;

    public FormHeading() {
        this.addClassName("FormHeading");
        heading = new Div();
        this.add(heading);
    }

    public void setHLevel(int numComponent, int level) {
        hLevel = level;
        if (numComponent == 0) { pageTitle = true; }
        this.getElement().setAttribute("level", "" + hLevel);
        heading.getElement().setAttribute("pageTitle", ""+ pageTitle);
    }

    public void setHeading(String label) {
        heading.setText(label);

    }

    public void flag(List<IOFXProblem> problemList) {
        boolean hasHeading = heading != null;

        this.removeAll();

        if (hasHeading) {
            this.add(heading);
        }

        for (IOFXProblem prblm: problemList) {
            Div info =new Div();
            info.setText(prblm.getSimpleUserText());
            if (prblm.isWarningOnly()) {
                info.addClassName("TurkuWarningDiv");
            }else{
                info.addClassName("TurkuErrorDiv");
            }

            this.add(info);
        }


    }



}
