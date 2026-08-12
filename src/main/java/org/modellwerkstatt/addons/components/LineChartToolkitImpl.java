package org.modellwerkstatt.addons.components;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import org.modellwerkstatt.addons.components.support.MultiSeriesUxListBound;
import org.modellwerkstatt.dataux.runtime.customcomponents.ExtCmpt;
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

        if (provider.getOption(ExtCmpt.LINECHART_LINE1COLOR) != null)
            chart.getStyle().set("--vaadin-charts-color-0", provider.getOption(ExtCmpt.LINECHART_LINE1COLOR));

        if (provider.getOption(ExtCmpt.LINECHART_LINE2COLOR) != null)
            chart.getStyle().set("--vaadin-charts-color-1", provider.getOption(ExtCmpt.LINECHART_LINE2COLOR));

        if (provider.getOption(ExtCmpt.LINECHART_LINE3COLOR) != null)
            chart.getStyle().set("--vaadin-charts-color-2", provider.getOption(ExtCmpt.LINECHART_LINE3COLOR));

        if (provider.getOption(ExtCmpt.LINECHART_LINE4COLOR) != null)
            chart.getStyle().set("--vaadin-charts-color-3", provider.getOption(ExtCmpt.LINECHART_LINE4COLOR));

        if (provider.getOption(ExtCmpt.LINECHART_LINE5COLOR) != null)
            chart.getStyle().set("--vaadin-charts-color-4", provider.getOption(ExtCmpt.LINECHART_LINE5COLOR));

        if (provider.getOption(ExtCmpt.LINECHART_LINE6COLOR) != null)
            chart.getStyle().set("--vaadin-charts-color-5", provider.getOption(ExtCmpt.LINECHART_LINE6COLOR));

        if (provider.getOption(ExtCmpt.LINECHART_TITLE) != null) {
            conf.setTitle("<span class=\"FormHeading\">" + provider.getOption(ExtCmpt.LINECHART_TITLE) + "</span>");
            conf.getTitle().setAlign(HorizontalAlign.LEFT);
            conf.getTitle().setUseHTML(true);
        }

        if (provider.getOption(ExtCmpt.LINECHART_YTITLE) != null) conf.getyAxis().setTitle(provider.getOption(ExtCmpt.LINECHART_YTITLE) );

        chart.drawChart();


    }

    @Override
    public void loadList(List<T> list, IOFXSelection<T> iofxSelection) {
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

        if (provider.getOption(ExtCmpt.LINECHART_XLABELSTEP) != null) {
            int step = Integer.parseInt(provider.getOption(ExtCmpt.LINECHART_XLABELSTEP));
            labels.setStep(step);
        }
        xAxis.setLabels(labels);

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
