package org.modellwerkstatt.turkuforms.experiment;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.modellwerkstatt.turkuforms.components.SelectionGrid;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Route("grid-dense-theme")
@CssImport(themeFor = "vaadin-grid", value = "./styles/densegrid.css")
public class GridDenseTheme extends VerticalLayout {
    private SelectionGrid<Person> grid = new SelectionGrid<>(Person.class);


    public GridDenseTheme() {

        this.setSizeFull();

        List<Person> data = new ArrayList<>();
        data.add(new Person("Dan", "Man", LocalDate.now()));
        data.add(new Person("Dan", "Man", LocalDate.now()));
        data.add(new Person("Dan", "Man", LocalDate.now()));
        data.add(new Person("Dan", "Man", LocalDate.now()));
        data.add(new Person("Dan", "Man", LocalDate.now()));
        data.add(new Person("Dan", "Man", LocalDate.now()));
        data.add(new Person("Dan", "Man", LocalDate.now()));
        data.add(new Person("Dan", "Man", LocalDate.now()));
        data.add(new Person("Dan", "Man", LocalDate.now()));
        data.add(new Person("Dan", "Man", LocalDate.now()));
        data.add(new Person("Dan", "Man", LocalDate.now()));
        data.add(new Person("Dan", "Man", LocalDate.now()));
        data.add(new Person("Dan", "Man", LocalDate.now()));
        data.add(new Person("Dan", "Man", LocalDate.now()));

        grid.setItems(data);
        // grid.setThemeName("dense");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        grid.setSizeFull();
        add(grid);

        Button b = new Button("Focus on ", event -> { grid.focusOnCell(data.get(5));});
        add(b);
    }

    public static class Person {
        private String firstName, lastName;
        private LocalDate birthDate;

        public Person(String firstName, String lastName, LocalDate birthDate) {
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