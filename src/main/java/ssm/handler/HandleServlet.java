package ssm.handler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ssm.annotation.myAutoWrited;
import ssm.annotation.myController;
import ssm.annotation.myRequestMapping;
import ssm.annotation.myService;
import ssm.data.BeanDefinition;
import ssm.utils.*;
import ssm.data.View;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 自定义注解的核心处理器,负责调用目标业务方法处理用户请求,类似于SpringMvc的DespatcherServlet
 *
 * @author itguang
 * @create 2018-04-05 21:54
 **/
public class HandleServlet extends HttpServlet {
    private HandleServletRequestMap handleServletRequestMap = null;//请求处理类。

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException

    {
        System.out.println("AnnotationHandlerServlet-->doGet....");
        this.excute(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("AnnotationHandlerServlet-->doPost....");
        this.excute(request, response);
    }

    private void excute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //1.将当前 HttpRequest 放到ThreadLocal中,方便在Controller中使用
        WebContext.requestHodler.set(request);
        //将 HttpReponse 放到ThreadLocal中,方便在Controller中使用
        WebContext.responseHodler.set(response);
        //获取处理后返回的data.
        View view = this.handleServletRequestMap.handRequest(request,response);
        //如果有URL返回值,就代表用户需要返回视图
        if (view.getUrl() != null) {
            //判断要使用的跳转方式
            if(view.getData() != null){
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write(view.getData().toString());
            }
            if (view.getDispathAction().equals(DispatchActionConstant.REDIRECT)) {
                //使用客户端跳转方式
                response.sendRedirect(request.getContextPath() + view.getUrl());
            }else if(view.getDispathAction().equals(DispatchActionConstant.FORWARD)){
                //使用服务器端跳转方式
                request.getRequestDispatcher(view.getUrl()).forward(request, response);
            }else{
                //request.getRequestDispatcher(view.getUrl()).forward(request, response);
            }
        }else{
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(view.getData().toString());
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        /**
         * 重写了Servlet的init方法后一定要记得调用父类的init方法，
         * 否则在service/doGet/doPost方法中使用getServletContext()方法获取ServletContext对象时
         * 就会出现java.lang.NullPointerException异常
         */
        super.init(config);
        System.out.println("---初始化开始---");
        this.handleServletRequestMap = new HandleServletRequestMap();
        //获取web.xml中配置的要扫描的包
        String annotationPackageBeans = config.getInitParameter("basePackage");
        //扫描application.xml中配置的bean.
        String xmlBeans = config.getInitParameter("xmlBean");//加载beanXML路径
        String xmlMybatis = config.getInitParameter("xmlMybatis");//加载mybatisXML路径

        HandleServletInit handleServletInit = new HandleServletInit();
        HandleServletInject handleServletInject = new HandleServletInject();
        try {
            //扫描注解
            handleServletInit.loadBeansDefinitionByAnnotations(annotationPackageBeans);
            //扫描SpringBean配置的xml
            handleServletInit.loadBeansDefinitionByXml(xmlBeans);
            //扫描mybatis配置的xml，创建数据库连接,并将创建的代理对象，合入singletonObjects中。
            handleServletInit.createMybatisProxyFactory(xmlMybatis);
            handleServletInit.mergeMybatisBeanToSingletonObjects();
            //根据扫描结果，创建bean,并为注入bean属性。
            handleServletInject.doLoadBeanDefinitions();

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("----初始化结束---");
    }

//    扫描多个包，并配置。
    private void initRequestBeansByPackage(String basePackage){
        //如果配置了多个包，例如：<param-value>me.gacl.web.controller,me.gacl.web.UI</param-value>
        //按逗号进行分隔
        String[] packageNameArr = basePackage.split(",");
        for (String packageName : packageNameArr) {
            initRequestMapingMap(packageName);
        }
    }
    /**
     * @Method: initRequestMapingMap
     * @Description:添加使用了Controller注解的Class到RequestMapingMap中
     */
    private void initRequestMapingMap(String packageName){
        //得到扫描包下的class
        Set<Class<?>> setClasses =  ScanClassUtil.getClasses(packageName);
        for (Class<?> clazz :setClasses) {

            if (clazz.isAnnotationPresent(myController.class)) {
                Method [] methods = BeanUtils.findDeclaredMethods(clazz);
                for(Method m:methods){//循环方法，找匹配的方法进行执行
                    if(m.isAnnotationPresent(myRequestMapping.class)){
                        String anoPath = m.getAnnotation(myRequestMapping.class).value();
                        if(anoPath!=null && !"".equals(anoPath.trim())){
                            if (RequestMapingMap.getRequesetMap().containsKey(anoPath)) {
                                throw new RuntimeException("RequestMapping映射的地址不允许重复！");
                            }
                            //把所有的映射地址存储起来  映射路径--类
                            RequestMapingMap.put(anoPath, clazz);
                        }
                    }
                }
            }
        }
    }
    private void initRequestBeansByXml(String xmlPath){

    }



}
