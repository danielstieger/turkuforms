package org.modellwerkstatt.turkuforms.experiment;


import com.vaadin.componentfactory.selectiongrid.SelectionGrid;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Route("selectiongrid")
public class SelectionGridTest extends VerticalLayout {
        Div myDiv = new Div();


        public SelectionGridTest() {
            SelectionGrid<Person2> grid = new SelectionGrid<>();

            List<Person2> data = new ArrayList<>();
            data.add(new Person2("Test", "3.Apr. 11:24", LocalDate.now()));
            data.add(new Person2("Dan", "Man", LocalDate.now()));
            data.add(new Person2("Dan", "Man", LocalDate.now()));
            data.add(new Person2("Dan", "Man", LocalDate.now()));
            data.add(new Person2("Dan", "Man", LocalDate.now()));

            data.add(new Person2("Dan", "Man", LocalDate.now()));
            data.add(new Person2("Dan", "Man", LocalDate.now()));
            data.add(new Person2("Dan", "Man", LocalDate.now()));
            data.add(new Person2("Dan", "Man", LocalDate.now()));
            data.add(new Person2("Dan", "Man", LocalDate.now()));

            data.add(new Person2("Dan", "Man", LocalDate.now()));
            data.add(new Person2("Dan", "Man", LocalDate.now()));
            data.add(new Person2("Dan", "Man", LocalDate.now()));
            data.add(new Person2("Dan", "Man", LocalDate.now()));
            data.add(new Person2("Dan", "Man", LocalDate.now()));

            data.add(new Person2("Dan", "Man", LocalDate.now()));
            data.add(new Person2("Dan", "Man", LocalDate.now()));
            data.add(new Person2("Dan", "Man", LocalDate.now()));
            data.add(new Person2("Dan", "Man", LocalDate.now()));
            data.add(new Person2("Dan", "Man", LocalDate.now()));

            data.add(new Person2("Dan", "Man", LocalDate.now()));
            data.add(new Person2("Dan", "Man", LocalDate.now()));
            data.add(new Person2("Dan", "Man", LocalDate.now()));
            data.add(new Person2("Dan", "Man", LocalDate.now()));
            data.add(new Person2("Dan", "Man", LocalDate.now()));

            grid.addColumn(Person2::getFirstName).setHeader("First Name");
            grid.addColumn(Person2::getLastName).setHeader("Last Name");
            grid.addColumn(Person2::getBirthDate).setHeader("BirthDate");


            grid.setItems(data);


            grid.setSelectionMode(Grid.SelectionMode.MULTI);

            add(grid);
            myDiv.setText("?");
            add(myDiv);

            grid.asMultiSelect().addValueChangeListener(event -> {
                String message = String.format("Selection changed from %s to %s",
                        event.getOldValue(), event.getValue());
                myDiv.setText(message);
            });

            grid.setSizeFull();
            setSizeFull();
        }

        public static class Person2 {
            private String firstName, lastName;
            private LocalDate birthDate;

            public Person2(String firstName, String lastName, LocalDate birthDate) {
                this.firstName = firstName;
                this.lastName = lastName;
                this.birthDate = birthDate;
            }

            public LocalDate getBirthDate() {
                return birthDate;
            }

            public void setBirthDate(LocalDate birthDate) {
                this.birthDate = birthDate;
            }

            public String getFirstName() {
                return firstName;
            }

            public void setFirstName(String firstName) {
                this.firstName = firstName;
            }

            public String getLastName() {
                return lastName;
            }

            public void setLastName(String lastName) {
                this.lastName = lastName;
            }
        }
}
