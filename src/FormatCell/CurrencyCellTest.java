package FormatCell;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Random;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import javafx.geometry.Pos;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
import model.Pedido;

public class CurrencyCellTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        TableView<Item> table = new TableView<>();

        table.getSelectionModel().setCellSelectionEnabled(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        table.setOnKeyPressed(evt->{
            if(evt.getCode().isDigitKey()){
                final TablePosition focusedCell = table.focusModelProperty().get().focusedCellProperty().get();
                table.edit(focusedCell.getRow(), focusedCell.getTableColumn());
            }
        });        

        table.setEditable(true);
        table.getColumns().add(column("Item", Item::nameProperty));
        TableColumn<Item, Double> priceCol = column("Price", item -> item.priceProperty().asObject());
        table.getColumns().add(priceCol);

        priceCol.setCellFactory(tc -> new CurrencyCell<>());

        Random rng = new Random();
        for (int i = 1; i <= 100; i++) {
            table.getItems().add(new Item("Item " + i, rng.nextInt(10000) / 100.0));
        }

        primaryStage.setScene(new Scene(table, 600, 600));
        primaryStage.show();
    }

    private static <S, T> TableColumn<S, T> column(String title, Function<S, Property<T>> property) {
        TableColumn<S, T> col = new TableColumn<>(title);
        col.setCellValueFactory(cellData -> property.apply(cellData.getValue()));
        return col;
    }

    public static class Item {

        private final StringProperty name = new SimpleStringProperty();
        private final DoubleProperty price = new SimpleDoubleProperty();

        public Item(String name, double price) {
            setName(name);
            setPrice(price);
        }

        public final StringProperty nameProperty() {
            return this.name;
        }

        public final String getName() {
            return this.nameProperty().get();
        }

        public final void setName(final String name) {
            this.nameProperty().set(name);
        }

        public final DoubleProperty priceProperty() {
            return this.price;
        }

        public final double getPrice() {
            return this.priceProperty().get();
        }

        public final void setPrice(final double price) {
            this.priceProperty().set(price);
        }

    }

    public static void main(String[] args) {
        // for testing:
//        Locale.setDefault(new Locale("NL", "nl"));
        launch(args);
    }
}
