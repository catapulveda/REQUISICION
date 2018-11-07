package util;

import java.text.Format;
import java.text.ParseException;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 *
 * @author AUXPLANTA
 */
public class FormattedTableCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {
    
    private final TextField textField ;
    
    private Pos alignment;
    private Format format;   

    public FormattedTableCellFactory(Pos alignment, Format format) {
        this.alignment = alignment;
        this.format = format;
        this.textField = new TextField();        
    }

    public FormattedTableCellFactory() {
        this.textField = new TextField();
        
        StringConverter<Integer> converter = new StringConverter<Integer>() {

            @Override
            public String toString(Integer object) {
                return object == null ? "" : object.toString();
            }

            @Override
            public Integer fromString(String string) {
                return string.isEmpty() ? 0 : Integer.parseInt(string);
            }
        };
        
    }   
    
    @Override
    @SuppressWarnings("unchecked")
    public TableCell<S, T> call(TableColumn<S, T> p) {
        TableCell<S, T> cell = new TableCell<S, T>() {            
            
            @Override
            public void updateItem(Object item, boolean empty) {               
                if (item == getItem()) {
                    return;
                }
                super.updateItem((T) item, empty);
                if (item == null) {
                    super.setText(null);
                    super.setGraphic(null);
                } else if (format != null) {
                    super.setText(format.format(item));
                } else if (item instanceof Node) {
                    super.setText(null);
                    super.setGraphic((Node) item);
                } else {
                    super.setText(item.toString());
                    super.setGraphic(null);
                }
            }
            
            @Override
            public void startEdit() {
                super.startEdit();
                System.out.println("startEdit");
                textField.setText((getItem().toString()));                
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                textField.requestFocus();
                textField.selectAll();
            }

            @Override
            public void cancelEdit() {
                System.out.println("cancelEdit");
                super.cancelEdit();
                setText(format.format(getItem()));
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }

            @Override
            public void commitEdit(T newValue) {
                System.out.println("commitEdit");
                super.commitEdit(newValue); 
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
            
        };
        cell.setAlignment(alignment);        
        return cell;
    }
    
    public Pos getAlignment() {
        return alignment;
    }

    public void setAlignment(Pos alignment) {
        this.alignment = alignment;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }
    
}
