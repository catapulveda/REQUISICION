package SearchComboBox;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.control.Skinnable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 *
 * @author Carmelo Marin Abrego
 */
public class SearchComboBoxSkin<T> extends ComboBoxListViewSkin<T> {

    private final TextField searchBox;
    private final ListView<T> itemView;

    private boolean clickSelection = false;

    public SearchComboBoxSkin(SearchComboBox<T> comboBox) {
        super(comboBox);

        // cuadro de busqueda
        searchBox = new TextField();
        searchBox.setPromptText("Search Box");
        searchBox.textProperty().addListener((p, o, text) -> handleTextChange(text));

        // ListView que muestra los elementos filtrados
        itemView = new ListView<>();
        itemView.setItems(comboBox.getFilterList());

        // administrar la seleccion de un nuevo item
        itemView.getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
            if (item != null) {
                comboBox.getSelectionModel().select(item);
                
                // ocultar popup cuando el item fue seleccionado mediante un click
                if (clickSelection) {
                    comboBox.hide();
                }
            }
        });

        // ocultar popup al usar las teclas determindas ENTER, ESC, SPACE
        itemView.setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.ENTER || t.getCode() == KeyCode.SPACE || t.getCode() == KeyCode.ESCAPE) {
                comboBox.hide();
            }
        });

        // cambia el foco del TextField al ListView usando las teclas ENTER y ESC
        searchBox.setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.ENTER || t.getCode() == KeyCode.ESCAPE) {
                itemView.requestFocus();
            }
        });

        // se ha hecho click sobre el ListView
        itemView.addEventFilter(MouseEvent.ANY, me
                -> clickSelection = me.getEventType().equals(MouseEvent.MOUSE_PRESSED));
    }

    @Override
    protected PopupControl getPopup() {

        // redefinir el combobox popup
        super.getPopup().setSkin(new Skin<Skinnable>() {

            @Override
            public Skinnable getSkinnable() {
                return null;
            }

            @Override
            public Node getNode() {
                return createPopupContent();
            }

            @Override
            public void dispose() {
                
            }
        });

        return super.getPopup();
    }

    private void handleTextChange(String text) {
        SearchComboBox<T> scb = ((SearchComboBox) getSkinnable());
        scb.setPredicateFilter(item -> text.isEmpty() ? true : scb.getFilter().test(item, text));
    }

    private Node createPopupContent() {

        VBox box = new VBox(searchBox, itemView);
        box.setSpacing(2.0);
        box.setPadding(new Insets(2.0));
        box.getStyleClass().add("combo-box-popup");
        box.setMaxWidth(getSkinnable().getWidth());

        return box;
    }

    @Override
    protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);

        if ("SHOWING".equals(p)) {

            SearchComboBox<T> scb = ((SearchComboBox<T>) getSkinnable());
            if (scb.isShowing()) {
                searchBox.clear();

                itemView.getSelectionModel().select(scb.getValue());
                itemView.requestFocus();
            }
        }
    }

}