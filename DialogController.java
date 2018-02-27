package Organizer;

import Organizer.datamodel.ToDoData;
import Organizer.datamodel.ToDoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogController {

    @FXML
    private TextField shortDescriptionNewField;
    @FXML
    private TextArea TextAreaNewField;
    @FXML
    private DatePicker DatePickerNewField;

    @FXML
    private Label warning;

    public ToDoItem addingNewItemFromToolbar() {

        String shortDescription = shortDescriptionNewField.getText().trim();
        String moreDetails = TextAreaNewField.getText().trim();
        LocalDate dates = DatePickerNewField.getValue();

                ToDoItem newItem = new ToDoItem(shortDescription, moreDetails, dates);
                ToDoData.getInstance().addItem(newItem);
                return newItem;
            }


    public void editItemFromToolbar(ToDoItem oldItem){

        shortDescriptionNewField.setText(oldItem.getShortDescription());
        TextAreaNewField.setText(oldItem.getDetails());
        DatePickerNewField.setValue(oldItem.getLocalDate());

        shortDescriptionNewField.setEditable(true);
        TextAreaNewField.setEditable(true);
        DatePickerNewField.setEditable(true);
    }


    public TextField getShortDescriptionNewField() {
        return shortDescriptionNewField;
    }

    public TextArea getTextAreaNewField() {
        return TextAreaNewField;
    }

    public DatePicker getDatePickerNewField() {
        return DatePickerNewField;
    }

    public Label getWarning() {
        return warning;
    }

    public void setWarning(Label warning) {
        this.warning = warning;
    }

    public void setShortDescriptionNewField(TextField shortDescriptionNewField) {
        this.shortDescriptionNewField = shortDescriptionNewField;
    }

    public void setTextAreaNewField(TextArea textAreaNewField) {
        TextAreaNewField = textAreaNewField;
    }

    public void setDatePickerNewField(DatePicker datePickerNewField) {
        DatePickerNewField = datePickerNewField;
    }
}
