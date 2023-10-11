package org.modellwerkstatt.turkuforms.experiment;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.modellwerkstatt.turkuforms.components.SelectionGrid;

import java.util.List;

@Route("grid-dense-theme")
@CssImport(themeFor = "vaadin-grid", value = "./styles/densegrid.css")
public class GridDenseTheme extends VerticalLayout {
    private SelectionGrid<Person> grid = new SelectionGrid<>(Person.class);


    public GridDenseTheme() {

        this.setSizeFull();

        List<Person> data = Person.getAllPersons();
        grid.setItems(data);
        // grid.setThemeName("dense");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        grid.setSizeFull();
        add(grid);

        Button b = new Button("Focus on ", event -> {
            grid.focusOnCell(data.get(5));
        });
        add(b);
    }
}