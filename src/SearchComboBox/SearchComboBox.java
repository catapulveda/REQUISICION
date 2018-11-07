package SearchComboBox;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Skin;

/**
 *
 * @author Carmelo Marin Abrego
 */
public class SearchComboBox<T> extends ComboBox<T> {

    private FilteredList<T> filterList;
    private BiPredicate<T, String> filter;

    public SearchComboBox() {
        this(FXCollections.observableArrayList());
    }
    
    public SearchComboBox(ObservableList<T> items) {

        this.filterList = new FilteredList<>(items);
        this.filter = (i, s) -> true;

        super.setItems(items);
        super.itemsProperty().addListener((p, o, n) -> {
            this.filterList = new FilteredList<>(n);
        });
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new SearchComboBoxSkin<>(this);
    }

    public void setFilter(BiPredicate<T, String> filter) {
        this.filter = filter;
    }

    public BiPredicate<T, String> getFilter() {
        return filter;
    }

    public void setPredicateFilter(Predicate<T> predicate) {
        filterList.setPredicate(predicate);
    }

    public FilteredList<T> getFilterList() {
        return filterList;
    }

}