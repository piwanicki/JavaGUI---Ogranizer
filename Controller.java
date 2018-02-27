package Organizer;

import Organizer.datamodel.ToDoData;
import Organizer.datamodel.ToDoItem;


import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {

    @FXML
    private TextArea ItemsTextArea;

    @FXML
    private Label deadlineLabel;

    @FXML
    private ListView<ToDoItem> ItemsListView;


    private List<ToDoItem> choosenItems = new ArrayList<>();

    @FXML
    private DatePicker date;

    @FXML
    private Button back;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private ContextMenu listContextMenu;


    @FXML
    private CheckBox important;
    @FXML
    private CheckBox itsDone;

    private FilteredList<ToDoItem> filteredList;

    private Predicate<ToDoItem> allItems;
    private Predicate<ToDoItem> importantItems;
    private Predicate<ToDoItem> choosenDate;

    private SortedList<ToDoItem> sortedList;


    public void initialize() {

        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                ToDoItem item = ItemsListView.getSelectionModel().getSelectedItem();
                deleteFromList(item);
            }
        });

        MenuItem editMenuItem = new Menu("Edit");
        editMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showEditItem();
            }
        });

        listContextMenu.getItems().addAll(deleteMenuItem);
        listContextMenu.getItems().addAll(editMenuItem);

        ItemsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoItem>() {
            @Override
            public void changed(ObservableValue<? extends ToDoItem> observable, ToDoItem oldValue, ToDoItem newValue) {
                if (newValue != null) {
                    ToDoItem newItem = ItemsListView.getSelectionModel().getSelectedItem();
                    ItemsTextArea.setText(newItem.getDetails());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                    deadlineLabel.setText(formatter.format(newItem.getLocalDate()));
                }
            }
        });

        allItems = new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem item) {
                return true;
            }
        };

        choosenDate = new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem item) {
                return item.getLocalDate().equals(date.getValue());
            }
        };

        importantItems = new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem item) {
                return (item.getLocalDate().equals(LocalDate.now().minusDays(1)) || item.getLocalDate().equals(LocalDate.now()));
            }
        };

        filteredList = new FilteredList<>(ToDoData.getInstance().getToDoItems(),allItems);

        sortedList = new SortedList<ToDoItem>(filteredList, new Comparator<ToDoItem>() {
            @Override
            public int compare(ToDoItem o1, ToDoItem o2) {
                return (o1.getLocalDate().compareTo(o2.getLocalDate()));
            }
        });


        ItemsListView.setItems(sortedList);
        ItemsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ItemsListView.getSelectionModel().selectFirst();

        ItemsListView.setCellFactory(new Callback<ListView<ToDoItem>, ListCell<ToDoItem>>() {
            @Override
            public ListCell<ToDoItem> call(ListView<ToDoItem> param) {
                ListCell<ToDoItem> cell = new ListCell<>() {

                    @Override
                    protected void updateItem(ToDoItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item.getShortDescription());
                            if (item.getLocalDate().equals(LocalDate.now())) {
                                setTextFill(Color.RED);
                            } else if (item.getLocalDate().equals(LocalDate.now().minusDays(2))) {
                                setTextFill(Color.GREEN);
                            }
                        }
                    }
                };

                cell.emptyProperty().addListener(
                        (observable, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }
                        });
                return cell;
            }
        });
    }

    @FXML
    public void filterTheList(){
        if(important.isSelected()){
            filteredList.setPredicate(importantItems);
        } else {
            filteredList.setPredicate(allItems);
        }
    }

    @FXML
    public void chooseDateOnCalendar() {
            filteredList.setPredicate(choosenDate);
    }

    @FXML
    public void backToList(){
        date.setValue(null);
        filteredList.setPredicate(allItems);
    }


    public void showNewItemWindow() {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Dodaj nowy termin");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("DialogPaneFX.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Can't load dialogPane");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        DialogController controller = fxmlLoader.getController();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if ((controller.getDatePickerNewField().getValue() == null) || (controller.getShortDescriptionNewField().getText().isEmpty())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Uzupelnij pola!");
                alert.setHeaderText("Musisz podać przynajmniej date i tytuł zdarzenia");
                Optional<ButtonType> alertButton = alert.showAndWait();
                if (alertButton.isPresent() && alertButton.get() == ButtonType.OK) {
                    showNewItemWindow();
//                } else if (controller.getShortDescriptionNewField().getText() != null) {
//                    ToDoItem newItem = controller.addingNewItemFromToolbar();
//                    ItemsListView.getSelectionModel().select(newItem);
//                    System.out.println("Obiekt stworzony");
                }
            } else {
                ToDoItem newItem = controller.addingNewItemFromToolbar();
                ItemsListView.getSelectionModel().select(newItem);
                System.out.println("Obiekt stworzony");
            }
        }
    }

    public void deleteFromList(ToDoItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuń wydarzenie");
        alert.setHeaderText("Czy chcesz usunąć wskazany rekord?");
        alert.setContentText(item.getShortDescription() + ", " + item.getLocalDate());

        Optional<ButtonType> result = alert.showAndWait();
        if ((result.isPresent()) && (result.get().equals(ButtonType.OK))) {
            ToDoData.getInstance().deleteItem(item);
        }
    }

    public void handleOnDeletePressed(KeyEvent keyEvent) {
        ToDoItem selectedItem = ItemsListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                deleteFromList(selectedItem);
            }
        }
    }

    public void itsDoneChecked(){
        if(itsDone.isSelected()){
            ToDoItem finishItem = ItemsListView.getSelectionModel().getSelectedItem();
            Alert alert =new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Ostrzeżenie");
            alert.setHeaderText("Zrobione?");
            alert.setContentText("Jeśli wykonałeś zadanie rekord zostanie usunięty");

            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get()==ButtonType.OK ){
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Dobra robota");
                alert.setTitle("Usunieto");
                alert.showAndWait();

                ToDoData.getInstance().getToDoItems().remove(finishItem);
                itsDone.selectedProperty().setValue(null);
            }

        }


    }

    public void showEditItem() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Edytuj rekord");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("DialogPaneFX.fxml"));


        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.getStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
        DialogController dialogController = fxmlLoader.getController();

        ToDoItem oldItem = ItemsListView.getSelectionModel().getSelectedItem();
        dialogController.editItemFromToolbar(oldItem);

        Optional<ButtonType> result = dialog.showAndWait();
        if((result.isPresent() && result.get() == (ButtonType.OK))){
            String newShortDescription = dialogController.getShortDescriptionNewField().getText();
            String newDetails = dialogController.getTextAreaNewField().getText();
            LocalDate newLocalDate = dialogController.getDatePickerNewField().getValue();
            ToDoItem editedItem = new ToDoItem(newShortDescription,newDetails,newLocalDate);

            System.out.println(editedItem);

            ToDoData.getInstance().getToDoItems().remove(oldItem);
            ToDoData.getInstance().getToDoItems().add(editedItem);
            filteredList.setPredicate(allItems);
            ItemsListView.getSelectionModel().select(editedItem);

        }
    }

    public void exitApp(){
        Platform.exit();
    }
}
