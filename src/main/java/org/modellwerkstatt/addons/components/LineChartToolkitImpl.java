package org.modellwerkstatt.addons.components;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import org.modellwerkstatt.addons.components.support.MultiSeriesUxListBound;
import org.modellwerkstatt.dataux.runtime.telemetrics.Dux;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;

import java.util.ArrayList;
import java.util.List;

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

        conf.getChart().setStyledMode(true);

        if (provider.getTitle() != null) {
            conf.setTitle("<span class=\"FormHeading\">" + provider.getyTitle() + "</span>");
            conf.getTitle().setAlign(HorizontalAlign.LEFT);
            conf.getTitle().setUseHTML(true);
        }
        if (provider.getyTitle() != null) conf.getyAxis().setTitle(provider.getyTitle());

        chart.drawChart();


    }

    @Override
    public void loadList(List<T> list, IOFXSelection<T> iofxSelection) {
        Dux.hl("LineChartToolkitImpl with " + list.size() + " items");

        Configuration conf = chart.getConfiguration();


        List<Series> allSeries = new ArrayList<Series>();
        List<String> categoriesUsed = new ArrayList<>();

        provider.getSeries().forEach(series ->
            allSeries.add(new DataSeries(series.getName())));

        for (int dataIndex = 0; dataIndex < list.size(); dataIndex++) {
            T obj = list.get(dataIndex);

            categoriesUsed.add(provider.getXValueAsString(obj));

            allSeries.forEach(series -> {
                    DataSeriesItem item = new DataSeriesItem();
                    item.setY(provider.getYValueAsBigDecimal(obj, series.getName()));
                    ((DataSeries) series).add(item);
            });
        }

        XAxis xAxis = conf.getxAxis();
        xAxis.setType(AxisType.CATEGORY);
        conf.getxAxis().setCategories(categoriesUsed.toArray(new String[categoriesUsed.size()]));
        conf.setSeries(allSeries);

        Labels labels = new Labels();
        labels.setEnabled(true);
        labels.setUseHTML(false);
        labels.setRotation(-55);
        labels.setAutoRotation(new Number[]{-55});
        labels.setAlign(HorizontalAlign.RIGHT);

        if (provider.getxLabelStep() > 0) labels.setStep(provider.getxLabelStep());
        xAxis.setLabels(labels);

        Dux.hl("LineChartToolkitImpl with " + allSeries.size() + " series.");

        chart.drawChart(true);
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
