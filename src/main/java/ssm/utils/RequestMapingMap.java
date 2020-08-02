package ssm.utils;

import com.sun.org.apache.xerces.internal.xs.StringList;
import ssm.data.BeanDefinition;
import ssm.mybatis.session.SqlSession;
import ssm.mybatis.session.impl.DefaultSqlSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存储方法的访问路径
 *
 * @author itguang
 * @create 2018-04-05 22:19
 **/
public class RequestMapingMap {
    /**
     * @Field: requesetMap
     *          用于存储方法的访问路径
     */
    //存储所有bean的名字
    private static List<String> beanNames = new ArrayList<String>();
    //////////////////////////////////存储所有bean的beanDefinition信息//////////////////////////////
    private static HashMap<String, BeanDefinition> beanClassMap = new HashMap<String, BeanDefinition>();
    public static void putBeanDefinition(String beanName, BeanDefinition beanDefinition){
        beanClassMap.put(beanName,beanDefinition);
    }
    public static BeanDefinition getBeanDefinition(String beanName){
        return beanClassMap.get(beanName);
    }
    public static  void addBeanName(String beanName){
        beanNames.add(beanName);
    }

    public static HashMap<String, BeanDefinition> getBeanClassMap() {
        return beanClassMap;
    }
    public static void setBeanClassMap(HashMap<String, BeanDefinition> beanClassMap) {
        RequestMapingMap.beanClassMap = beanClassMap;
    }
    public static List<String> getBeanNames() {
        return beanNames;
    }
    public static void setBeanNames(List<String> beanNames) {
        RequestMapingMap.beanNames = beanNames;
    }
    //////////////////////////////存储所有请求，对应的方法或类。///////////////////////////////////
    private static Map<String, Class<?>> requesetMap = new HashMap<String, Class<?>>();

    public static Class<?> getClassName(String path) {
        return requesetMap.get(path);
    }

    public static void put(String path, Class<?> className) {
        requesetMap.put(path, className);
    }

    public static Map<String, Class<?>> getRequesetMap() {
        return requesetMap;
    }

    ////////////////////////////依赖注入的3级缓存///////////////////////////////////////////////////
    private static HashMap<String, Object> singletonObjects = new HashMap();
    private static HashMap<String, Object> earlySingletonObjects = new HashMap();
    private static HashMap<String, Object> singletonFactories = new HashMap();
    private static List<String> singletonsCurrentlyInCreation = new ArrayList();
    //用于存储接口与实现类的对应关系《接口名， 实现类名》
    private static HashMap<String, String> interfaceMapClass = new HashMap<>();
    ///////////////////////////mybatis容器////////////////////////////////////////////////////////
    private static SqlSession sqlSession = null;

    public static SqlSession getSqlSession() {
        return sqlSession;
    }

    public static void setSqlSession(SqlSession sqlSession) {
        RequestMapingMap.sqlSession = sqlSession;
    }

    public static HashMap<String, Object> getSingletonObjects() {
        return singletonObjects;
    }

    public static void setSingletonObjects(HashMap<String, Object> singletonObjects) {
        RequestMapingMap.singletonObjects = singletonObjects;
    }

    public static HashMap<String, Object> getEarlySingletonObjects() {
        return earlySingletonObjects;
    }

    public static void setEarlySingletonObjects(HashMap<String, Object> earlySingletonObjects) {
        RequestMapingMap.earlySingletonObjects = earlySingletonObjects;
    }

    public static HashMap<String, Object> getSingletonFactories() {
        return singletonFactories;
    }

    public static void setSingletonFactories(HashMap<String, Object> singletonFactories) {
        RequestMapingMap.singletonFactories = singletonFactories;
    }

    public static List<String> getSingletonsCurrentlyInCreation() {
        return singletonsCurrentlyInCreation;
    }

    public static void setSingletonsCurrentlyInCreation(List<String> singletonsCurrentlyInCreation) {
        RequestMapingMap.singletonsCurrentlyInCreation = singletonsCurrentlyInCreation;
    }

    public static HashMap<String, String> getInterfaceMapClass() {
        return interfaceMapClass;
    }

    public static void setInterfaceMapClass(HashMap<String, String> interfaceMapClass) {
        RequestMapingMap.interfaceMapClass = interfaceMapClass;
    }
}
