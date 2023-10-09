package org.modellwerkstatt.turkuforms.experiment;

import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.gridpro.GridProVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import java.time.LocalDate;

@Route("test")
public class TestView<DTO> extends HorizontalLayout {


    public TestView() {
        super();

        Peculiar.shrinkSpace(this);
        setSizeFull();

        GridPro<Person> gp = new GridPro<Person>();

        gp.setEditOnClick(true);
        gp.setEnterNextRow(true);
        gp.addThemeVariants(GridProVariant.LUMO_HIGHLIGHT_EDITABLE_CELLS);

        gp.setItems(Person.getAllPersons());
        gp.addColumn(Person::getFirstName).setHeader("FirstName");
        gp.addColumn(Person::getLastName).setHeader("LastName");
        gp.addEditColumn(Person::getBirthDate).text((person, newValue) -> {
                if (newValue.equals("dan")) {
                    showErrorNotification("Enter a valid phone number");
                }else {
                    person.setBirthDate(LocalDate.now());
                }
                }).setHeader("BirthDate");


        add(gp);

    }


    private static void showErrorNotification(String msg) {
        Notification notification = new Notification(msg, 5000,
                Notification.Position.BOTTOM_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.open();
    }
}
