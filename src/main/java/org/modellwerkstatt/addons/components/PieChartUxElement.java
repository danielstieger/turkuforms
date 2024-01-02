package org.modellwerkstatt.addons.components;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import org.modellwerkstatt.addons.components.support.CustomDataUxListBound;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;

public class PieChartUxElement<T> extends CustomDataUxListBound<T> {


    public PieChartUxElement() {
        super();

        setToolkitFormImpl(new PieChartToolkitImpl<T>(this));
    }

}
