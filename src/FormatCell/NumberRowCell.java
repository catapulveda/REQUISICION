/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FormatCell;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;

/**
 *
 * @author PROGRAMADOR
 */
public class NumberRowCell<T> extends TableCell<T, Integer> {

    private final Button btn = new Button();
    
    public NumberRowCell() {
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setMaxHeight(Double.MAX_VALUE);
    }
    
    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        if (this.getTableRow() != null && item != null) {
            btn.setOnAction(evt->{
                getTableView().getSelectionModel().select(getIndex());
            });
            btn.setText((this.getTableRow().getIndex() + 1) + "");
            //setText((this.getTableRow().getIndex() + 1) + "");
            setGraphic(btn);
        } else {
            setText("");
        }
        setStyle("-fx-padding: 0 0 0 0;");
        setAlignment(Pos.CENTER);
    }
}
