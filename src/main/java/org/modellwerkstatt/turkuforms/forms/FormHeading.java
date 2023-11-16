package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.html.Div;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;

import java.util.List;

public class FormHeading extends Div {
    private Div heading;

    public FormHeading) {
        int level = 1;
        this.addClassName("FormHeading");
        this.getElement().setAttribute("level", "" + level);
        heading = new Div();
        this.add(heading);
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
