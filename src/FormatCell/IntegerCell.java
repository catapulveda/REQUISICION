package FormatCell;

import java.awt.event.FocusEvent;
import java.text.NumberFormat;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
import model.Pedido;

/**
 *
 * @author AUXPLANTA
 */
public class IntegerCell<T> extends TableCell<T, Double>{
    
    private final TextField textField ;

//    private final NumberFormat format = NumberFormat.getIntegerInstance();
    private final NumberFormat format = NumberFormat.getNumberInstance();
    private boolean esc = false;
    StringConverter<Double> converter = null;

    public IntegerCell() {
        this.textField = new TextField();
        
        converter = new StringConverter<Double>() {

            @Override
            public String toString(Double object) {
                return object == null ? "" : object.toString();
            }

            @Override
            public Double fromString(String string) {                
                return string.isEmpty() ? 0 : Double.parseDouble(string);                
            }
        };
        
        textField.setOnAction(e ->{
            commitEdit(converter.fromString(textField.getText()));
        });
        textField.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });
        
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
//                setDisabled(false);
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
        System.out.println(getItem()+"\t"+textField.getText());
        if(esc){
            System.out.println("CANCELADO "+getItem());
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
