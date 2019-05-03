package FormatCell;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.function.UnaryOperator;
import javafx.geometry.Pos;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import model.Pedido;
import model.RecepcionDePedido;

public class ColorYRecibirPedido extends TableCell<RecepcionDePedido, Double> {

    private final TextField textField;

    private final NumberFormat numberFormat = DecimalFormat.getNumberInstance(Locale.getDefault());
    private final DecimalFormat decimalFormat = new DecimalFormat("0.0");

    public ColorYRecibirPedido() {

        this.textField = new TextField();
        StringConverter<Double> converter = new StringConverter<Double>() {

            @Override
            public String toString(Double object) {
                return object == null ? "" : decimalFormat.format(object);
            }

            @Override
            public Double fromString(String string) {
                try {
                    return string.isEmpty() ? 0.0 : decimalFormat.parse(string.replace(".", ",")).doubleValue();
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0.0;
                }
            }
        };

        UnaryOperator<Change> filter = (Change change) -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                return change;
            }
            try {
                decimalFormat.parse(newText);
                return change;
            } catch (ParseException exc) {
                System.out.println(exc.getMessage());
                return null;
            }
        };

        TextFormatter<Double> textFormatter = new TextFormatter<>(converter, 0.0, filter);
        textField.setTextFormatter(textFormatter);

        textField.setOnAction(e -> {
            if (converter.fromString(textField.getText())>0 && converter.fromString(textField.getText()) <= getTableView().getItems().get(getIndex()).getPedido().getCantidadsolicitada()) {
                commitEdit(converter.fromString(textField.getText()));
            }
        });
        textField.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });

        setGraphic(textField);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    @Override
    protected void updateItem(Double item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else if (isEditing()) {
            textField.setText(item.toString());
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        } else {
            RecepcionDePedido rdp = getTableView().getItems().get(getIndex());
            if(rdp.getCantidadrecibida()==rdp.getPedido().getCantidadsolicitada()){
                setDisable(true);
            }
            if ( item>0 ) {
                setStyle("-fx-background-insets: 0 0 1 0 ;-fx-background-color: -fx-background;"
                        + "-fx-background: #d14836;-fx-font-weight: bold;");
            } else {
                setStyle("-fx-background-insets: 0 0 1 0 ;-fx-background-color: -fx-background;"
                        + "-fx-background: #3cba54;-fx-font-weight: bold;");
            }
            setText(numberFormat.format(item));
            setContentDisplay(ContentDisplay.TEXT_ONLY);
            setAlignment(Pos.CENTER);
            setTextFill(Color.ANTIQUEWHITE);
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        textField.setText(decimalFormat.format(getItem()));
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        textField.requestFocus();
        textField.selectAll();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(numberFormat.format(getItem()));
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    public void commitEdit(Double newValue) {
        super.commitEdit(newValue);
        setContentDisplay(ContentDisplay.TEXT_ONLY);
        getTableView().requestFocus();
    }

}
