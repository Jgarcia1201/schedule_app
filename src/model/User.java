package model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class User {
    private int userId;
    private String userName;
    private String password;
    private LocalDateTime createDate;
    private String createdBy;
    private Timestamp lastUpdatedBy;
    public static String currentUser;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int id) {
        this.userId = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Timestamp lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public void setCurrentUser(String s) {
        User.currentUser = s;
    }

    public String getCurrentUser() {
        return currentUser;
    }
}
