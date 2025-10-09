package org.modellwerkstatt.turkuforms.auth;

public class UserPrincipal {

    protected String userName;
    protected String password;

    public UserPrincipal(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return  userName + " UserPrincipal";
    }

}
