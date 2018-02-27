package Organizer.datamodel;

import java.time.LocalDate;

public class ToDoItem {

    private String shortDescription;
    private String details;
    private LocalDate localDate;

    public ToDoItem(String shortDescription, String details, LocalDate localDate) {
        this.shortDescription = shortDescription;
        this.details = details;
        this.localDate = localDate;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    @Override
    public String toString() {
        return shortDescription;
    }
}
