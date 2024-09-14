package com.example.tour;

import javafx.scene.image.Image;

public class SessionManager {
    private static SessionManager instance = new SessionManager();
    private String username;
    private Image image;
    private String email = null, address = null, fullname = null;

    private SessionManager(){
        try {
            image = new Image(LoginController.class.getResource("/profile.png").toExternalForm());
        } catch (NullPointerException e) {
            System.out.println("Session Pic a problem");
            e.printStackTrace();
            image = null;
        }
    }

    // Singleton pattern
    public static SessionManager getInstance() {
        return instance;
    }

    public void setLoggedInUser(String username) {
        this.username = username;
    }

    public String getLoggedInUser() {
        return username;
    }

    public void setLoggedInUserPic(Image image) {
        this.image = image;
    }

    public Image getLoggedInUserPic() {
        return image;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getAddress() {
        return address;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    public String getFullname() {
        return fullname;
    }

    public void clearSession() {
        username = null;
        try {
            image = new Image(getClass().getResourceAsStream("/profile.png"));
        } catch (NullPointerException e) {
            e.printStackTrace();
            image = null;
        }
        email = null;
        fullname = null;
        address = null;

    }
}
