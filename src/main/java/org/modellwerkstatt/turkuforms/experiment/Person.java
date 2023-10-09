package org.modellwerkstatt.turkuforms.experiment;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Person {
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


    public static List<Person> getAllPersons () {
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
        return data;
    }
}