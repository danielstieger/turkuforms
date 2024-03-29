package org.modellwerkstatt.turkuforms.experiment;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.math.BigDecimal;

@PageTitle("About")
@Route(value = "about")
public class AboutView extends VerticalLayout {

    public AboutView() {
        setSizeFull();

        /* the content */
        HorizontalLayout l1 = new HorizontalLayout();
        l1.add(new Button("Top Bar related to Chart "));

        Chart chart = new Chart(ChartType.PIE);
        chart.setSizeFull();
        Configuration conf = chart.getConfiguration();

        DataSeries series = new DataSeries();
        DataSeriesItem dsi1 = new DataSeriesItem(
                "Num 1", new BigDecimal("10"));
        DataSeriesItem dsi2 = new DataSeriesItem(
                "Num 2", new BigDecimal("20"));

        series.add(dsi1);
        series.add(dsi2);
        conf.setSeries(series);

        VerticalLayout exchangeableContent = new VerticalLayout();
        exchangeableContent.setSizeFull();
        exchangeableContent.add(l1, chart);
        exchangeableContent.getStyle().set("overflow", "hidden");
        exchangeableContent.setMinWidth("0");
        exchangeableContent.setMinHeight("0");


        /* ------ the application setup  ----- */
        HorizontalLayout bottomButtonBar = new HorizontalLayout();
        bottomButtonBar.add(new Button("Application Navigation Bar"));


        VerticalLayout application = new VerticalLayout();
        application.add(exchangeableContent, bottomButtonBar);
        application.setSizeFull();
        add(application);
    }
}
