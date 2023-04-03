package org.modellwerkstatt.turkuforms.experiment;


import com.vaadin.componentfactory.selectiongrid.SelectionGrid;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.modellwerkstatt.turkuforms.util.Turku;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Route("selectiongrid")
public class SelectionGridTest extends VerticalLayout {
        private int DELAY = 0;

        Div myDiv = new Div();


        public SelectionGridTest() {
            SelectionGrid<Person2> grid = new SelectionGrid<>();
            SelectionGrid<Person2> grid2 = new SelectionGrid<>();


            List<Person2> data = createData(0, "", 1500);


            grid.addColumn(Person2::getFirstName).setHeader("Pos 1");
            grid.addColumn(Person2::getLastName).setHeader("Pos 2");
            grid.addColumn(Person2::getField3).setHeader("?");
            grid.addColumn(Person2::getField4).setHeader("Description");
            grid.addColumn(Person2::getField5).setHeader("DAN");
            grid.addColumn(Person2::getField6).setHeader("In Folder");
            grid.addColumn(Person2::getField7).setHeader("Location");


            grid.setItems(data);
            grid.setSelectionMode(Grid.SelectionMode.MULTI);


            grid2.addColumn(Person2::getFirstName).setHeader("Pos 1");
            grid2.addColumn(Person2::getLastName).setHeader("Pos 2");
            grid2.addColumn(Person2::getField3).setHeader("?");
            grid2.addColumn(Person2::getField4).setHeader("Description");
            grid2.addColumn(Person2::getField5).setHeader("DAN");
            grid2.addColumn(Person2::getField6).setHeader("In Folder");
            grid2.addColumn(Person2::getField7).setHeader("Location");
            grid2.setSelectionMode(Grid.SelectionMode.MULTI);

            add(grid);
            add(grid2);
            myDiv.setText("?");
            add(myDiv);

            grid.asMultiSelect().addValueChangeListener(event -> {
                String message = String.format("Selection changed from %s to %s",
                        event.getOldValue(), event.getValue());
                myDiv.setText(message);


                Set<Person2> selection = event.getValue();
                Optional<Person2> p = selection.stream().findFirst();

                p.ifPresent( person -> {
                    List<Person2> detail = createData( Integer.valueOf(person.firstName),"200", 60);
                    grid2.setItems(detail);

                    if (DELAY > 0) {
                        try {
                            Thread.sleep(DELAY);

                        }catch (InterruptedException e) {

                        }
                    }
                });






            });

            grid.setSizeFull();
            grid2.setSizeFull();
            setSizeFull();
        }


        public List<Person2> createData(int base, String heading, int size) {
            List<Person2> total = new ArrayList<>();

            for (int i=0; i < size; i++){
                total.add(new Person2("" + base, heading + base, "!", "this stuff behaves the way I expect",
                        "almost everywhere", "some.folder", "line "+ base + " in nowhere"));
                base ++;

            }

            return total;
        }



        public static class Person2 {
            private String firstName, lastName;
            private String field3, field4, field5, field6, field7;


            public Person2(String firstName, String lastName, String field3, String field4, String field5, String field6, String field7) {
                this.firstName = firstName;
                this.lastName = lastName;
                this.field3 = field3;
                this.field4 = field4;
                this.field5 = field5;
                this.field6 = field6;
                this.field7 = field7;
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

            public String getField3() {
                return field3;
            }

            public void setField3(String field3) {
                this.field3 = field3;
            }

            public String getField4() {
                return field4;
            }

            public void setField4(String field4) {
                this.field4 = field4;
            }

            public String getField5() {
                return field5;
            }

            public void setField5(String field5) {
                this.field5 = field5;
            }

            public String getField6() {
                return field6;
            }

            public void setField6(String field6) {
                this.field6 = field6;
            }

            public String getField7() {
                return field7;
            }

            public void setField7(String field7) {
                this.field7 = field7;
            }
        }
}
