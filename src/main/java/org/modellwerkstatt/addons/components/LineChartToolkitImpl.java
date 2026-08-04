package org.modellwerkstatt.addons.components;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.charts.model.style.SolidColor;
import org.modellwerkstatt.addons.components.support.MultiSeriesUxListBound;
import org.modellwerkstatt.dataux.runtime.telemetrics.Dux;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class LineChartToolkitImpl<T> extends Chart implements IToolkit_Form<T> {
    protected MultiSeriesUxListBound<T> provider;
    protected Chart chart;

    public LineChartToolkitImpl(MultiSeriesUxListBound<T> provider) {
        super(ChartType.LINE);

        setSizeFull();
        chart = this;
        chart.setSizeFull();
        this.provider = provider;
    }

    @Override
    public void afterFullUiInitialized() {
        Configuration conf = chart.getConfiguration();
        PlotOptionsLine opt = new PlotOptionsLine();
        opt.setAnimation(false);

        conf.setPlotOptions(opt);

        conf.getTitle().getStyle().setFontSize("14px");
        chart.drawChart();

    }

    @Override
    public void loadList(List<T> list, IOFXSelection<T> iofxSelection) {
        Dux.hl("LineChartToolkitImpl with " + list.size() + " items");

        Configuration conf = chart.getConfiguration();
        conf.setSeries(Collections.emptyList());


        XAxis xAxis = conf.getxAxis();
        xAxis.setType(AxisType.CATEGORY);

        List<DataSeries> allSeries = new ArrayList<DataSeries>();
        provider.getSeries().forEach(series ->
            allSeries.add(new DataSeries(series.getName())));

        for (int dataIndex = 0; dataIndex < list.size(); dataIndex++) {
            T obj = list.get(dataIndex);

            allSeries.forEach(series -> {
                    DataSeriesItem item = new DataSeriesItem();
                    item.setName(provider.getXValueAsString(obj));
                    item.setY(provider.getYValueAsBigDecimal(obj, series.getName()));
                    series.add(item);
                    });
        }

        allSeries.forEach(conf::addSeries);
        Dux.hl("LineChartToolkitImpl with " + allSeries.size() + " series.");

        Labels labels = xAxis.getLabels();
        labels.setEnabled(true);
        labels.setRotation(55);
        labels.setAlign(HorizontalAlign.RIGHT);
        // labels.setStep(5);
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
