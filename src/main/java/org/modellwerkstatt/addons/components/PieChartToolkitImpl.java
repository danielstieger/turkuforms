package org.modellwerkstatt.addons.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.charts.model.style.SolidColor;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.modellwerkstatt.addons.components.support.CustomDataUxListBound;
import org.modellwerkstatt.dataux.runtime.customcomponents.ExtCmpt;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;
import org.modellwerkstatt.turkuforms.views.CmdUi;
import org.modellwerkstatt.turkuforms.views.CmdUiTab;

import java.util.List;
import java.util.Optional;

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
        conf.getChart().setStyledMode(true);
        conf.getChart().setHeight("300px");

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
        l.getStyle().set("overflow", "hidden");
        // l.setMinWidth("0");
        l.setMinHeight("0");
    }

    @Override
    public void gcClear() {

    }
}