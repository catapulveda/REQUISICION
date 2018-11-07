/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SearchComboBox;

import java.util.function.Function;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class TableViewDragRows extends Application {

    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

    @Override
    public void start(Stage primaryStage) {
        TableView<Person> tableView = new TableView<>();
        tableView.getColumns().add(createCol("First Name", Person::firstNameProperty, 150));
        tableView.getColumns().add(createCol("Last Name", Person::lastNameProperty, 150));
        tableView.getColumns().add(createCol("Email", Person::emailProperty, 200));

        tableView.getItems().addAll(
            new Person("Jacob", "Smith", "jacob.smith@example.com"),
            new Person("Isabella", "Johnson", "isabella.johnson@example.com"),
            new Person("Ethan", "Williams", "ethan.williams@example.com"),
            new Person("Emma", "Jones", "emma.jones@example.com"),
            new Person("Michael", "Brown", "michael.brown@example.com")
        );

        tableView.setRowFactory(tv -> {
            TableRow<Person> row = new TableRow<>();

            row.setOnDragDetected(event -> {
                if (! row.isEmpty()) {
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                }
            });

            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != ((Integer)db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });

            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                    Person draggedPerson = tableView.getItems().remove(draggedIndex);

                    int dropIndex ; 

                    if (row.isEmpty()) {
                        dropIndex = tableView.getItems().size() ;
                    } else {
                        dropIndex = row.getIndex();
                    }

                    tableView.getItems().add(dropIndex, draggedPerson);

                    event.setDropCompleted(true);
                    tableView.getSelectionModel().select(dropIndex);
                    event.consume();
                }
            });

            return row ;
        });


        Scene scene = new Scene(new BorderPane(tableView), 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TableColumn<Person, String> createCol(String title, 
            Function<Person, ObservableValue<String>> mapper, double size) {

        TableColumn<Person, String> col = new TableColumn<>(title);
        col.setCellValueFactory(cellData -> mapper.apply(cellData.getValue()));
        col.setPrefWidth(size);

        return col ;
    }


   public class Person {
        private final StringProperty firstName = new SimpleStringProperty(this, "firstName");
        private final StringProperty lastName = new SimpleStringProperty(this, "lastName");
        private final StringProperty email = new SimpleStringProperty(this, "email");

        public Person(String firstName, String lastName, String email) {
            this.firstName.set(firstName);
            this.lastName.set(lastName);
            this.email.set(email);
        }

        public final StringProperty firstNameProperty() {
            return this.firstName;
        }

        public final String getFirstName() {
            return this.firstNameProperty().get();
        }

        public final void setFirstName(final String firstName) {
            this.firstNameProperty().set(firstName);
        }

        public final StringProperty lastNameProperty() {
            return this.lastName;
        }

        public final String getLastName() {
            return this.lastNameProperty().get();
        }

        public final void setLastName(final String lastName) {
            this.lastNameProperty().set(lastName);
        }

        public final StringProperty emailProperty() {
            return this.email;
        }

        public final String getEmail() {
            return this.emailProperty().get();
        }

        public final void setEmail(final String email) {
            this.emailProperty().set(email);
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
