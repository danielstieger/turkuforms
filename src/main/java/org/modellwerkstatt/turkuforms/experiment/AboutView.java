package org.modellwerkstatt.turkuforms.experiment;

import com.vaadin.componentfactory.selectiongrid.SelectionGrid;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@PageTitle("About")
@Route(value = "about")
public class AboutView extends VerticalLayout {

    public AboutView() {
        setSpacing(true);

        Div messageDiv = new Div();

        List<Person> personList = getItems();
        Grid<Person> grid = new SelectionGrid<>();
        grid.setItems(personList);

        grid.addColumn(Person::getFirstName).setHeader("First Name");
        grid.addColumn(Person::getAge).setHeader("Age");

        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        grid.asMultiSelect().addValueChangeListener(event -> {
            String message = String.format("Selection changed from %s to %s",
                    event.getOldValue(), event.getValue());
            messageDiv.setText(message);
        });

        // You can pre-select items
        grid.asMultiSelect().select(personList.get(0), personList.get(1));
        add(grid, messageDiv);
    }

    private List<Person> getItems() {
        List<Person> p =  new ArrayList<Person>();

        p.add(new Person());
        p.add(new Person());
        p.add(new Person());

        return p;
    }

}
