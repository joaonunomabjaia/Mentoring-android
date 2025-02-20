package mz.org.csaude.mentoring.base.viewModel;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.base.searchparams.AbstractSearchParams;

public interface SearchPaginator<T extends BaseModel>{

    List<T> doSearch(long offset, long limit) throws Exception;

    void displaySearchResults();

    AbstractSearchParams<T> initSearchParams();

    default void doOnlineSearch(long offset, long limit) throws Exception {}

    default void createPdfDocument() {}

    default Collection<?> doBeforeDisplay(List<T> objects) {
        return null;
    }

}
