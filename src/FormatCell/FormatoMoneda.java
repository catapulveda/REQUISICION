package FormatCell;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import javafx.geometry.Pos;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import model.Pedido;

public class FormatoMoneda<T> extends TableCell<T, Double>{
    
    private final TextField textField ;
    private final DecimalFormat decimalFormat = new DecimalFormat();

    private boolean esc = false;
    
    public FormatoMoneda() {
        
        this.textField = new TextField();                
        
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
                    commitEdit(Double.parseDouble(textField.getText()));
                    getTableView().getSelectionModel().selectNext();
                    event.consume();
                    break;                
                case LEFT:
                    commitEdit(Double.parseDouble(textField.getText()));
                    getTableView().getSelectionModel().selectPrevious();
                    event.consume();
                    break;
                case UP:
                    commitEdit(Double.parseDouble(textField.getText()));
                    getTableView().getSelectionModel().selectAboveCell();
                    event.consume();
                    break;
                case DOWN:
                    commitEdit(Double.parseDouble(textField.getText()));
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
            setText(NumberFormat.getCurrencyInstance().format(item));
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        textField.setText("");
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        textField.requestFocus();
        textField.end();
    }

    @Override
    public void cancelEdit() {
        if(esc){
            setText(NumberFormat.getCurrencyInstance().format(getItem()));
        }else{
            try {
                setText(NumberFormat.getCurrencyInstance().format(Double.parseDouble(textField.getText())));
                commitEdit(Double.parseDouble(textField.getText()));
            } catch (java.lang.NumberFormatException e) {
//                setText(NumberFormat.getCurrencyInstance().format(Double.parseDouble("0")));
//                commitEdit(Double.parseDouble("0"));
            }
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
