package ssm.data;

import ssm.utils.DispatchActionConstant;

/**
 * @author itguang
 * @create 2018-04-05 22:15
 **/
public class View {

    private String url;//跳转路径

    private String dispathAction = DispatchActionConstant.FORWARD;//跳转方式

    private Object data;
    public View(){};

    public View(String url) {
        this.url = url;
    }
    public View(Object value){this.data = value;}
    public View(String url,Object value) {
        this.url = url;
        this.data = value;
    }
    public View(String url,String dispathAction ,Object value) {
        this.dispathAction = dispathAction;
        this.url = url;
        this.data = value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDispathAction() {
        return dispathAction;
    }

    public void setDispathAction(String dispathAction) {
        this.dispathAction = dispathAction;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
