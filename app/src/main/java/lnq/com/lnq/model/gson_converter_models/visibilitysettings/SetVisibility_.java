
package lnq.com.lnq.model.gson_converter_models.visibilitysettings;

import java.util.HashMap;
import java.util.Map;

public class SetVisibility_ {

    private String user_id;
    private String visible_to;
    private String visible_at;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getVisible_to() {
        return visible_to;
    }

    public void setVisible_to(String visible_to) {
        this.visible_to = visible_to;
    }

    public String getVisible_at() {
        return visible_at;
    }

    public void setVisible_at(String visible_at) {
        this.visible_at = visible_at;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
