package util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import model.Pedido;

/**
 *
 * @author PROGRAMADOR
 */
public class BooleanCell extends TableCell<Pedido, Boolean>{
    
    private CheckBox checkBox;
    
    public BooleanCell() {
        checkBox = new CheckBox();
//        checkBox.setDisable(true);        
        checkBox.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {            
            System.out.println("addlistener");
            //commitEdit(newValue == null ? false : newValue);           
        });
        this.setGraphic(checkBox);
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.setAlignment(Pos.CENTER);
//        this.setEditable(true);
    }
    
    @Override
    public void startEdit() {
        super.startEdit();
        if (isEmpty()) {
            return;
        }
//        checkBox.setDisable(false);
        checkBox.requestFocus();
    }
    
    @Override
    public void cancelEdit() {
        super.cancelEdit();
//        checkBox.setDisable(true);
    }
    
    @Override
    public void commitEdit(Boolean value) {
        super.commitEdit(value);
//        checkBox.setDisable(true);
    }
    
    @Override
    public void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if (!isEmpty()) {
            System.out.println(item);
            checkBox.setSelected(item);
        }
    }
}
