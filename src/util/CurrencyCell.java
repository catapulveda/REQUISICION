package util;

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
import javafx.util.StringConverter;
import model.Pedido;

public class CurrencyCell<T> extends TableCell<T, Double>{
    
    private final TextField textField ;

    private final NumberFormat format = DecimalFormat.getCurrencyInstance();
    private final DecimalFormat decimalFormat = new DecimalFormat();

    private boolean esc = false;
    StringConverter<Double> converter = null;
    
    public CurrencyCell() {
        
        this.textField = new TextField();
        converter = new StringConverter<Double>() {

            @Override
            public String toString(Double object) {
                return object == null ? "" : decimalFormat.format(object) ;
            }

            @Override
            public Double fromString(String string) {
                try {
                    System.out.println("String -> "+string);
                    return string.isEmpty() ? 0.0 : decimalFormat.parse(string).doubleValue();
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0.0 ;
                }
            }
        };
        
        UnaryOperator<Change> filter = (Change change) -> {
            String newText = change.getControlNewText() ;
            if (newText.isEmpty()) {
                return change ;
            }
            try {
                decimalFormat.parse(newText);
                return change ;
            } catch (ParseException exc) {
                return null ;
            }
        };
        TextFormatter<Double> textFormatter = new TextFormatter<Double>(converter, 0.0, filter);
        textField.setTextFormatter(textFormatter);

        textField.setOnAction(e -> commitEdit(converter.fromString(textField.getText())));
        
        textField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (null != event.getCode()) switch (event.getCode()) {
                case ESCAPE:
                    esc = true;
                    textField.setText(getItem().toString());
                    cancelEdit();
                    esc = false;
                    event.consume();
                    break;
                case TAB:
                    getTableView().getSelectionModel().selectNext();
                    event.consume();
                    break;
                case RIGHT:
                    commitEdit(converter.fromString(textField.getText()));
                    getTableView().getSelectionModel().selectNext();
                    event.consume();
                    break;                
                case LEFT:
                    commitEdit(converter.fromString(textField.getText()));
                    getTableView().getSelectionModel().selectPrevious();
                    event.consume();
                    break;
                case UP:
                    commitEdit(converter.fromString(textField.getText()));
                    getTableView().getSelectionModel().selectAboveCell();
                    event.consume();
                    break;
                case DOWN:
                    commitEdit(converter.fromString(textField.getText()));
                    getTableView().getSelectionModel().selectBelowCell();
                    event.consume();
                    break;                    
                default:                    
                    break;
            }
        });
        
        
        
        setGraphic(textField);
        setContentDisplay(ContentDisplay.TEXT_ONLY);
        setAlignment(Pos.BASELINE_RIGHT);
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
            TableRow<Pedido> row = getTableRow();
            try {
                if (row.getItem().getOc().getNumerodeorden() > 0) {
                    setDisabled(true);
                }
            } catch (Exception e) {
//                setDisabled(true);
            }            
            setText(format.format(item));
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        textField.setText("");
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        textField.requestFocus();
        //textField.selectAll();
        textField.end();
    }

    @Override
    public void cancelEdit() {
        if(esc){
            setText(format.format(getItem()));
        }else{
            setText(""+converter.fromString(textField.getText()));
            commitEdit(converter.fromString(textField.getText()));
        }
        super.cancelEdit();
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    public void commitEdit(Double newValue) {
        super.commitEdit(newValue);
        setContentDisplay(ContentDisplay.TEXT_ONLY);
        getTableView().requestFocus();
    }
    
}
