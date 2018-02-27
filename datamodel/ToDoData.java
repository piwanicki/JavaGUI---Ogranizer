package Organizer.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class ToDoData {

    private static ToDoData instance = new ToDoData();
    private static String filename = "RzeczyDoZrobienia.txt";

    private ObservableList<ToDoItem> ToDoItems;
    private DateTimeFormatter formatter ;

    private ToDoData(){
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public void addItem(ToDoItem item){
        ToDoItems.add(item);
    }

    public void deleteItem(ToDoItem item){
        ToDoItems.remove(item);
    }

    public void replaceItem(ToDoItem oldItem, ToDoItem newItem){
    }


    public static ToDoData getInstance() {
        return instance;
    }
    public ObservableList<ToDoItem> getToDoItems() {
        return ToDoItems;
    }

    public void loadItems() throws IOException{
        ToDoItems = FXCollections.observableArrayList();
        Path path = Paths.get(filename);

        BufferedReader br = Files.newBufferedReader(path); // Bufor do wyczytywania pliku ze sciezki
        String input;

        try{
            while((input = br.readLine()) != null){ // Tworzymy warunek, jesli linia z bufora nie jest rowna zero to:

                String[] itemPieces = input.split("\t"); // Tworzymy tablice znakow String zczytujac i rozdzielajac  String input

                String shortDesc = itemPieces[0]; // Przydzielamy w tablicy w indexie 1 krotki opis
                String fullDesc = itemPieces[1]; // index 2 to pelen opis
                String date = itemPieces[2]; // index 3 to data w formacie String

                LocalDate localDate = LocalDate.parse(date,formatter); // Tu tworzymy obiekt localDate, metoda .parse zapisuje ja w formacie String z paramterow String date oraz formattera

                ToDoItem item = new ToDoItem(shortDesc,fullDesc,localDate);  // Tworzymy nowy obiekt o zdefiniowanych obiektach
                ToDoItems.add(item); //Dodajemy item do naszej arraylisty w aplikacji
            }
        } finally {
            if(br != null){
                br.close();
            }
        }
    }

    public void saveItems() throws IOException{
        Path path = Paths.get(filename); //Tworzymy sciezke (sciezka pliku)
        BufferedWriter bw = Files.newBufferedWriter(path); // Bufor do zapisywania pliku do sciezki

        try{
            Iterator<ToDoItem> iterable = ToDoItems.iterator();
            while(iterable.hasNext()){
                ToDoItem item = iterable.next();
                bw.write(String.format("%s\t%s\t%s",
                        item.getShortDescription(),
                        item.getDetails(),
                        item.getLocalDate().format(formatter)));
                bw.newLine();
            }
        } finally {
            if(bw != null)
                bw.close();
        }
    }



}

//public class ToDoData {
//    private static ToDoData instance = new ToDoData();
//    private static String filename = "TodoListItems.txt";
//
//    private List<ToDoItem> ToDoItems;
//    private DateTimeFormatter formatter;
//
//    public static ToDoData getInstance() {
//        return instance;
//    }
//
//    private ToDoData() {
//        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//    }
//
//    public List<ToDoItem> getToDoItems() {
//        return ToDoItems;
//    }
//
//        public void setToDoItems(List<ToDoItem> ToDoItems) {
//        this.ToDoItems = ToDoItems;
//    }
////
//    public void loadToDoItems() throws IOException {
//
//        ToDoItems = FXCollections.observableArrayList();
//        Path path = Paths.get(filename);
//        BufferedReader br = Files.newBufferedReader(path);
//
//        String input;
//
//        try {
//            while ((input = br.readLine()) != null) {
//                String[] itemPieces = input.split("\t");
//
//                String shortDescription = itemPieces[0];
//                String details = itemPieces[1];
//                String dateString = itemPieces[2];
//
//                LocalDate date = LocalDate.parse(dateString, formatter);
//                ToDoItem ToDoItem = new ToDoItem(shortDescription, details, date);
//                ToDoItems.add(ToDoItem);
//            }
//
//        } finally {
//            if(br != null) {
//                br.close();
//            }
//        }
//    }
//
//    public void storeToDoItems() throws IOException {
//
//        Path path = Paths.get(filename);
//        BufferedWriter bw = Files.newBufferedWriter(path);
//        try {
//            Iterator<ToDoItem> iter = ToDoItems.iterator();
//            while(iter.hasNext()) {
//                ToDoItem item = iter.next();
//                bw.write(String.format("%s\t%s\t%s",
//                        item.getShortDescription(),
//                        item.getDetails(),
//                        item.getLocalDate().format(formatter)));
//                bw.newLine();
//            }
//
//        } finally {
//            if(bw != null) {
//                bw.close();
//            }
//        }
//    }
//}
