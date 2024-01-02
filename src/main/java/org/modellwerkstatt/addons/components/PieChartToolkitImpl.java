package org.modellwerkstatt.addons.components;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.charts.model.style.SolidColor;
import org.modellwerkstatt.addons.components.support.CustomDataUxListBound;
import org.modellwerkstatt.dataux.runtime.customcomponents.ExtCmpt;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;

import java.math.BigDecimal;
import java.util.List;

public class PieChartToolkitImpl<T> extends Chart implements IToolkit_Form<T> {
    protected CustomDataUxListBound<T> dataProvider;

    public PieChartToolkitImpl(CustomDataUxListBound<T> provider) {
        super(ChartType.PIE);

        dataProvider = provider;
    }

    @Override
    public void afterFullUiInitialized() {
        PlotOptionsPie opt = new PlotOptionsPie();
        opt.setInnerSize("60%");

        getConfiguration().setPlotOptions(opt);
    }
    @Override
    public void loadList(List<T> list, IOFXSelection<T> iofxSelection) {

        Configuration conf = getConfiguration();
        conf.setTitle("My First Chart... ");

        DataSeries series = new DataSeries();
        boolean supportColor = dataProvider.hasLabel(ExtCmpt.PIECHART_ITEM_COLOR);

        for (int i = 0; i < list.size(); i++) {
            T obj = list.get(i);
            boolean last = i == list.size() - 1;

            DataSeriesItem dsi = new DataSeriesItem(
                    dataProvider.getString(obj, ExtCmpt.PIECHART_ITEM_LABEL),
                    dataProvider.getBigDecimal(obj, ExtCmpt.PIECHART_ITEM_VALUE));

            if (supportColor) {
                dsi.setColor(new SolidColor(dataProvider.getString(obj, ExtCmpt.PIECHART_ITEM_COLOR)));
            }

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
