package ssm.handler;

import com.alibaba.fastjson.JSONObject;
import ssm.annotation.myController;
import ssm.annotation.myRequestMapping;
import ssm.data.View;
import ssm.data.ViewData;
import ssm.utils.BeanUtils;
import ssm.utils.HttpRequestAndRespondUtil;
import ssm.utils.RequestMapingMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

public class HandleServletRequestMap {
    public View handRequest(HttpServletRequest request, HttpServletResponse response){
        //2.解析请求的url
        String requestUrl = HttpRequestAndRespondUtil.pareRequestURI(request);
        //检查请求是否合法
        if (!RequestMapingMap.getRequesetMap().containsKey(requestUrl)) {
            try{
                response.getWriter().write("404! url is not found!");
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                return null;
            }
        }
        //3.根据 请求的url获取要使用的类
        Class<?> clazz = RequestMapingMap.getClassName(requestUrl);
        //4.从容器中获取类的实例
        myController controller = (myController)clazz.getAnnotation(myController.class);
        String beanName = controller.value();
        if(beanName == null || beanName.trim().length() == 0){
            beanName = BeanUtils.firstLowerCase(clazz.getSimpleName());
        }
        Object classInstance = RequestMapingMap.getSingletonObjects().get(beanName);
        if(classInstance == null){
            new Exception("容器中不存在Controller:"+beanName+"的实例对象");
        }

        //5.获取类中定义的方法
        Method[] methods = BeanUtils.findDeclaredMethods(clazz);

        //遍历所有方法,找出url与RequestMapping注解的value值相匹配的方法
        Method method = null;
        for (Method m : methods) {
            if (m.isAnnotationPresent(myRequestMapping.class)) {
                String value = m.getAnnotation(myRequestMapping.class).value();
                if (value != null && !"".equals(value.trim()) && requestUrl.equals(value.trim())) {
                    //找到要执行的目标方法
                    method = m;
                    break;
                }

            }

        }
        Object[] args ;
        if ("POST".equals(request.getMethod()) && request.getContentType().contains("json")) {
            String str = getJson(request);
            args = getRequestParam(str, method);
        } else {
            args = getRequestParam(request.getParameterMap(), method);
        }
        //调用目标方法
        Object retObject = null;
        View view = new View();
        try {
            retObject = method.invoke(classInstance, args);
            String returnType = method.getReturnType().getTypeName();
            if(returnType.equals(View.class.getName())){
                view = (View)retObject;
            }else{
                view.setData(retObject);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }
    //获取json参数
    public String getJson(HttpServletRequest req) {
        String param = null;
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            param = responseStrBuilder.toString();
            System.out.println("request param="+param);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return param;
    }
    //解析json参数
    public Object[] getRequestParam(String json, Method method) {
        if (null == json || json.isEmpty()) {
            return null;
        }
        Parameter[] parameters = method.getParameters();
        Object[] requestParam = new Object[parameters.length];
        JSONObject jsonObject = JSONObject.parseObject(json);
        int i = 0;
        for (Parameter p : parameters) {
            Object val = jsonObject.getObject(p.getName(), p.getType());
            requestParam[i] = val;
            i++;
        }
        return requestParam;
    }

    public Object[] getRequestParam(Map<String, String[]> map, Method method) {
        if (null == map || map.size() == 0) {
            return null;
        }
        Parameter[] parameters = method.getParameters();
        int i = 0;
        Object[] requestParam = new Object[parameters.length];
        for (Parameter p : parameters) {
            String name = p.getName();
            if (!map.containsKey(p.getName())) {
                requestParam[i] = null;
                i++;
                continue;
            }
            try {
                Class typeClass = p.getType();
                String[] val = map.get(p.getName());
                if (null == val) {
                    requestParam[i] = null;
                    i++;
                    continue;
                }
                Constructor con = null;
                try {
                    con = typeClass.getConstructor(val[0].getClass());
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                Object obj = null;
                try {
                    obj = con.newInstance(val[0]);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                requestParam[i] = obj;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


            i++;
        }
        return requestParam;
    }
}
