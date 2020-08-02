package ssm.data;

import java.util.HashMap;
import java.util.List;

public class BeanDefinition {

    private String id;

    private String beanClassPath;

    private List<HashMap<String, String>> propertyList;

    public List<HashMap<String, String>> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(List<HashMap<String, String>> propertyList) {
        this.propertyList = propertyList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getbeanClassPath() {
        return beanClassPath;
    }

    public void setbeanClassPath(String beanClassPath) {
        this.beanClassPath = beanClassPath;
    }

    public Class<?> getBeanClass() throws Exception{
        return Class.forName(beanClassPath);
    }
}
