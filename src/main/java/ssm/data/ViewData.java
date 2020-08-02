package ssm.data;

import com.sun.corba.se.spi.ior.ObjectKey;
import ssm.utils.WebContext;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @author itguang
 * @create 2018-04-05 22:16
 **/
public class ViewData {

//    private HttpServletRequest request;
//
//    public ViewData() {
//        initRequest();
//    }
//
//    private void initRequest(){
//        //从requestHodler中获取request对象
//        this.request = WebContext.requestHodler.get();
//    }
//
//    public void put(String name,Object value){
//        this.request.setAttribute(name, value);
//    }
    private HashMap<String, Object> data;
    ViewData(){
        this.data = new HashMap<String, Object>();
    }
    public ViewData(String name, Object value){
        this.data = new HashMap<String, Object>();
        data.put(name, value);
    }
    public void put(String name, Object value){
        this.data.put(name, value);
    }
    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }
}
