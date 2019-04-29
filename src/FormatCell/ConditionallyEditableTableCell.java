package FormatCell;

import java.util.function.Function;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ConditionallyEditableTableCell extends Application {

    @Override
    public void start(Stage primaryStage) {
        TableView<Item> table = new TableView<>();

        table.setEditable(true);

        TableColumn<Item, String> nameCol = createCol("Name", Item::nameProperty);
        TableColumn<Item, Number> canEditCol = createCol("Value", Item::valueProperty);

        PseudoClass editableCssClass = PseudoClass.getPseudoClass("editable");

        Callback<TableColumn<Item, String>, TableCell<Item, String>> defaultTextFieldCellFactory
                = TextFieldTableCell.<Item>forTableColumn();

        nameCol.setCellFactory(col -> {
            TableCell<Item, String> cell = defaultTextFieldCellFactory.call(col);
            cell.itemProperty().addListener((obs, oldValue, newValue) -> {
                TableRow row = cell.getTableRow();
                if (row == null) {
                    cell.setEditable(false);
                } else {
                    Item item = (Item) cell.getTableRow().getItem();
                    if (item == null) {
                        cell.setEditable(false);
                    } else {
                        cell.setEditable(item.getValue() % 2 == 0);
                    }
                }
                cell.pseudoClassStateChanged(editableCssClass, cell.isEditable());
            });
            return cell;
        });

        table.getColumns().addAll(canEditCol, nameCol);

        for (int i = 1; i <= 20; i++) {
            table.getItems().add(new Item("Item " + i, i));
        }

        Scene scene = new Scene(new BorderPane(table), 600, 400);

        scene.getStylesheets().add("conditionally-editable-table-cell.css");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private <S, T> TableColumn<S, T> createCol(String title, Function<S, ObservableValue<T>> property) {
        TableColumn<S, T> col = new TableColumn<>(title);
        col.setCellValueFactory(cellData -> property.apply(cellData.getValue()));
        return col;
    }

    public static class Item {

        private final IntegerProperty value = new SimpleIntegerProperty();
        private final StringProperty name = new SimpleStringProperty();

        public Item(String name, int value) {
            setName(name);
            setValue(value);
        }

        public final StringProperty nameProperty() {
            return this.name;
        }

        public final java.lang.String getName() {
            return this.nameProperty().get();
        }

        public final void setName(final java.lang.String name) {
            this.nameProperty().set(name);
        }

        public final IntegerProperty valueProperty() {
            return this.value;
        }

        public final int getValue() {
            return this.valueProperty().get();
        }

        public final void setValue(final int value) {
            this.valueProperty().set(value);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
