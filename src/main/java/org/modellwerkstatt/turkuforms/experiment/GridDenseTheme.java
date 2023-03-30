package org.modellwerkstatt.turkuforms.experiment;
import com.flowingcode.vaadin.addons.gridhelpers.GridHelper;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Route("grid-dense-theme")
@CssImport(themeFor = "vaadin-grid", value = "./styles/densegrid.css")
public class GridDenseTheme extends VerticalLayout {
    private Grid<Person> grid = new Grid<>(Person.class);


    public GridDenseTheme() {

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
        data.add(new Person("Dan", "Man", LocalDate.now()));

        data.add(new Person("Dan", "Man", LocalDate.now()));
        data.add(new Person("Dan", "Man", LocalDate.now()));
        data.add(new Person("Dan", "Man", LocalDate.now()));
        data.add(new Person("Dan", "Man", LocalDate.now()));
        data.add(new Person("Dan", "Man", LocalDate.now()));

        grid.setItems(data);


        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        GridHelper.setArrowSelectionEnabled(grid, true);
        GridHelper.setColumnToggleVisible(grid, isVisible());
        GridHelper.setSelectOnClick(grid, true);
        grid.addThemeName(GridHelper.DENSE_THEME);

        add(grid);
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