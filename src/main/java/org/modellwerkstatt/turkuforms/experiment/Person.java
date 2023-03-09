package org.modellwerkstatt.turkuforms.experiment;

public class Person {
    private int num;

    public Person(int i) {
        num = i;
    }

    public String getFirstName() {
        return "DanMan " + num;
    }

    public String getAge() {
        return " " + num;
    }
}
