package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.util.List;

public class FormHeading extends Div {
    private Div heading;

    public FormHeading() {
        this.addClassName("FormHeading");
    }


    public void setHeading(String label) {
        heading = new Div();
        heading.setText(label);
        this.add(heading);
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
                info.addClassName("TurkuWarningColor");
            }else{
                info.addClassName("TurkuErrorColor");
            }

            this.add(info);
        }


    }



}
