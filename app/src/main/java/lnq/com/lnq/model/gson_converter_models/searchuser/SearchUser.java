
package lnq.com.lnq.model.gson_converter_models.searchuser;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchUser {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("searchContactByName")
    @Expose
    private List<SearchContactByName> searchContactByName = null;

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

    public List<SearchContactByName> getSearchContactByName() {
        return searchContactByName;
    }

    public void setSearchContactByName(List<SearchContactByName> searchContactByName) {
        this.searchContactByName = searchContactByName;
    }

}
