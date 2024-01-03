package org.modellwerkstatt.addons.components;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.charts.model.style.SolidColor;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import org.modellwerkstatt.addons.components.support.CustomDataUxListBound;
import org.modellwerkstatt.dataux.runtime.customcomponents.ExtCmpt;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;

import java.util.List;

public class PieChartToolkitImpl<T> extends Div implements IToolkit_Form<T> {
    protected CustomDataUxListBound<T> dataProvider;
    protected Chart chart;

    public PieChartToolkitImpl(CustomDataUxListBound<T> provider) {
        super();

        setSizeFull();
        chart = new Chart(ChartType.PIE);
        chart.setSizeFull();
        add(chart);

        dataProvider = provider;
    }

    @Override
    public void afterFullUiInitialized() {
        Configuration conf = chart.getConfiguration();
        PlotOptionsPie opt = new PlotOptionsPie();
        opt.setInnerSize("60%");
        opt.setAnimation(false);

        conf.setPlotOptions(opt);
        conf.getChart().setStyledMode(true);
    }

    @Override
    public void loadList(List<T> list, IOFXSelection<T> iofxSelection) {

        Configuration conf = chart.getConfiguration();

        DataSeries series = new DataSeries();
        boolean supportColor = dataProvider.hasLabel(ExtCmpt.PIECHART_ITEM_COLOR);
        boolean titleOnFirstItemExpected = dataProvider.hasLabel(ExtCmpt.PIECHART_TITEL_ON_FIRST_ITEM);



        for (int i = 0; i < list.size(); i++) {
            T obj = list.get(i);
            boolean last = i == list.size() - 1;
            boolean first = i == 0;

            if (first && titleOnFirstItemExpected) {
                String title = dataProvider.getString(obj, ExtCmpt.PIECHART_TITEL_ON_FIRST_ITEM);
                conf.setTitle(title);
            }

            DataSeriesItem dsi = new DataSeriesItem(
                    dataProvider.getString(obj, ExtCmpt.PIECHART_ITEM_LABEL),
                    dataProvider.getBigDecimal(obj, ExtCmpt.PIECHART_ITEM_VALUE));

//            dsi.setDataLabels(new DataLabels(true));

            /* if (supportColor) {
                dsi.setColor(new SolidColor(dataProvider.getString(obj, ExtCmpt.PIECHART_ITEM_COLOR)));
            } */

            if (last) { dsi.setSliced(true); }

            series.add(dsi);

        }

        conf.setSeries(series);
    }

    @Override
    public boolean selectionChanged(IOFXSelection<T> iofxSelection) {
        return false;
    }

    @Override
    public void setTitleText(String s) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void setProblems(List<IOFXProblem> list) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Object myRequestFocus() {
        return null;
    }

    @Override
    public void gcClear() {

    }
}
