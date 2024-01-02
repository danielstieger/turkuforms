package org.modellwerkstatt.addons.components;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.html.Label;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;
import org.modellwerkstatt.turkuforms.util.ListComponentSupport;

import java.util.List;

public class PieChart<T> extends ListComponentSupport<T> {
    private Chart pieChart;

    public PieChart() {

        pieChart = new Chart(ChartType.PIE);

    }

    @Override
    public IToolkit_Form getToolkitImplementation() {
        return null;
    }
}
