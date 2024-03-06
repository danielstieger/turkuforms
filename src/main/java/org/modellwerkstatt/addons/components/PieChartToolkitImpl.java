package org.modellwerkstatt.addons.components;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.charts.model.style.SolidColor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.modellwerkstatt.addons.components.support.CustomDataUxListBound;
import org.modellwerkstatt.dataux.runtime.customcomponents.ExtCmpt;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;

import java.util.List;

public class PieChartToolkitImpl<T> extends Chart implements IToolkit_Form<T> {
    protected CustomDataUxListBound<T> dataProvider;
    protected Chart chart;

    public PieChartToolkitImpl(CustomDataUxListBound<T> provider) {
        super(ChartType.PIE);

        setSizeFull();
        chart = this;
        chart.setSizeFull();
        // add(chart);  // from experiments with the Height on Flag/Resize bug....

        dataProvider = provider;
    }

    @Override
    public void afterFullUiInitialized() {
        Configuration conf = chart.getConfiguration();
        PlotOptionsPie opt = new PlotOptionsPie();
        opt.setInnerSize("60%");
        opt.setAnimation(false);

        conf.setPlotOptions(opt);
        // Enables css styling, but disables programmatically setting colors
        // conf.getChart().setStyledMode(true);

        // pass in as option? on first item?
        conf.getChart().setHeight("300px");
        conf.getChart().setBackgroundColor(new SolidColor(255, 255,255 , 0.0));
        conf.getTitle().getStyle().setColor(new SolidColor(23, 132, 200, 1.0));
        conf.getTitle().getStyle().setFontSize("14px");
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
        // due to vaadin charts, flexbox issue
        // https://github.com/vaadin/web-components/issues/2171#issuecomment-934340680

        /* Optional<Component> parent = getParent();
        while (parent.isPresent() && ! (parent.get() instanceof CmdUi)) {
            if (parent.get() instanceof VerticalLayout) {
                applyWorkaround((VerticalLayout) parent.get());
            }

            parent = parent.get().getParent();
        } */

        return null;
    }

    public void applyWorkaround(VerticalLayout l) {
        // Probably use fixed size
        l.getStyle().set("overflow", "hidden");
        // l.setMinWidth("0");
        l.setMinHeight("0");
    }

    @Override
    public void gcClear() {

    }
}
