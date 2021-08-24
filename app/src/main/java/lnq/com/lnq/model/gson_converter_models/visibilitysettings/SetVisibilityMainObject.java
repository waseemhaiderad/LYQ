
package lnq.com.lnq.model.gson_converter_models.visibilitysettings;

import java.util.HashMap;
import java.util.Map;

public class SetVisibilityMainObject {

    private Integer status;
    private String message;
    private SetVisibility_ setVisibility;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

    public SetVisibility_ getSetVisibility() {
        return setVisibility;
    }

    public void setSetVisibility(SetVisibility_ setVisibility) {
        this.setVisibility = setVisibility;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
