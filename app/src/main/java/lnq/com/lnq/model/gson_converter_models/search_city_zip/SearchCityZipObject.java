
package lnq.com.lnq.model.gson_converter_models.search_city_zip;

import java.util.List;

public class SearchCityZipObject {

    private Integer status;
    private String message;
    private List<SearchCityZipData> searchState = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SearchCityZipData> getSearchState() {
        return searchState;
    }

    public void setSearchState(List<SearchCityZipData> searchState) {
        this.searchState = searchState;
    }

}
