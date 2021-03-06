package model;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class Appointment {
    private int appointmentId;
    private String title;
    private String description;
    private String contact;
    private String location;
    private String type;
    private LocalDateTime start;
    private String displayStart;
    private LocalDateTime end;
    private String displayEnd;
    private LocalDateTime createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String displayLastUpdate;
    private String lastUpdatedBy;
    private int CustomerId;
    private int userId;
    private int contactId;

    String pattern = "MM/dd/yyyy hh:mm a";

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
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

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public int getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(int customerId) {
        CustomerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public void setDisplayStart(LocalDateTime time) {
        ZonedDateTime utc = ZonedDateTime.of(time, ZoneId.of("UTC"));
        ZoneId localId = ZoneId.systemDefault();
        LocalDateTime local = utc.withZoneSameInstant(localId).toLocalDateTime();
        this.displayStart = local.toString();
    }

    public String getDisplayStart() {
        return displayStart;
    }

    public void setDisplayEnd(LocalDateTime time) {
        ZonedDateTime utc = ZonedDateTime.of(time, ZoneId.of("UTC"));
        ZoneId localId = ZoneId.of(TimeZone.getDefault().getID());
        LocalDateTime local = utc.withZoneSameInstant(localId).toLocalDateTime();
        this.displayEnd = local.toString();
    }

    public String getDisplayEnd() {
        return displayEnd;
    }

    public void setDisplayLastUpdate(LocalDateTime time) {
        ZoneId utc = ZoneId.of("UTC");
        ZoneId local = ZoneId.of(TimeZone.getDefault().getID());
        ZonedDateTime utcTime = ZonedDateTime.of(time, utc);
        LocalDateTime convertedTime = utcTime.withZoneSameInstant(local).toLocalDateTime();
        this.displayLastUpdate = convertedTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public String getDisplayLastUpdate() {
        return displayLastUpdate;
    }



}
