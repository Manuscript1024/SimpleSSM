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
import ssm.mybatis.proxy.MapperProxyFactory;
import ssm.mybatis.config.Configuration;
import ssm.mybatis.session.SqlSession;
import ssm.mybatis.session.SqlSessionFactory;
import ssm.mybatis.session.impl.SqlSessionFactoryBuilder;
import ssm.utils.BeanUtils;
import ssm.utils.RequestMapingMap;
import ssm.utils.ScanClassUtil;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class HandleServletInit {
    public void loadBeansDefinitionByAnnotations(String basePackage){
        String[] packageNameArr = basePackage.split(",");
        for (String packageName : packageNameArr) {
            //得到扫描包下的class
            Set<Class<?>> setClasses =  ScanClassUtil.getClasses(packageName);
            for (Class<?> clazz :setClasses) {
                BeanDefinition beanDefinition = new BeanDefinition();
                String beanName = "";
                List<HashMap<String, String>> propertyList = null;
                if (clazz.isAnnotationPresent(myController.class)) {
                    myController controller = (myController)clazz.getAnnotation(myController.class);
                    beanName = controller.value();
                    if(beanName == null || beanName.trim().length() == 0){
                        beanName = BeanUtils.firstLowerCase(clazz.getSimpleName());
                    }
                    propertyList =  getPropertyListForAnnotation(clazz);

                    //接下来处理requestMap的请求映射。
                    handerRequestMaping(clazz);
                }else if(clazz.isAnnotationPresent(myService.class)){
                    myService service = (myService)clazz.getAnnotation(myService.class);
                    beanName = service.value();
                    if(beanName == null || beanName.trim().length() == 0){
                        beanName = BeanUtils.firstLowerCase(clazz.getSimpleName());
                    }
                    propertyList =  getPropertyListForAnnotation(clazz);
                    //找到该类的接口,存储对应关系,便于后期注入时，找到对应的实现类。
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for(Class<?> inter: interfaces){
                        RequestMapingMap.getInterfaceMapClass().put(BeanUtils.firstLowerCase(inter.getSimpleName()), beanName);
                    }
                }else{
                    System.out.println("other");
                    continue;
                }
                RequestMapingMap.addBeanName(beanName);//记录bean的名字
                beanDefinition.setId(beanName);
                beanDefinition.setbeanClassPath(clazz.getName());
                beanDefinition.setPropertyList(propertyList);
                RequestMapingMap.putBeanDefinition(beanName,beanDefinition);
            }
        }
    }
    //获取依赖属性列表,服务于注解式注入。
    //////////////////////////////////////////////////////////////////////////////////////
    public void loadBeansDefinitionByXml(String location) throws Exception{
        // 加载xml文件，取出所有子节点列表
        location = this.getClass().getResource("/").getPath() + location;
        InputStream inputStream = new FileInputStream(location);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(inputStream);
        Element root = doc.getDocumentElement();
        NodeList nodes = root.getChildNodes();
        // 遍历所有子节点bean，将子节点bean的ID，class，属性加入到beanDefinition
        for (int i = 0; i < nodes.getLength(); i++){
            BeanDefinition beanDefinition = new BeanDefinition();
            // 如果该子节点有元素，则加入
            Node node = nodes.item(i);
            if (node instanceof Element){
                Element ele = (Element) node;
                String beanName = ele.getAttribute("id");
                String beanClassPath = ele.getAttribute("class");

                beanDefinition.setId(beanName);
                beanDefinition.setbeanClassPath(beanClassPath);
                // 获取属性
                NodeList properties = ele.getElementsByTagName("property");
                List<HashMap<String,String>> propertyList = new ArrayList<HashMap<String,String>>();
                HashMap<String,String> propertyMap;
                for (int j = 0; j < properties.getLength(); j++){
                    propertyMap = new HashMap<String, String>();
                    Node propertyNode = properties.item(j);
                    if (propertyNode instanceof Element){
                        Element propertyElement = (Element) propertyNode;
                        String name = propertyElement.getAttribute("name");
                        String value = propertyElement.getAttribute("value");
                        propertyMap.put("propertyName", name);
                        if (value != null && value.length() != 0){
                            propertyMap.put("propertyValue", value);
                            propertyMap.put("propertyType", "string");
                        } else {
                            String ref = propertyElement.getAttribute("ref");
                            propertyMap.put("propertyValue", ref);
                            propertyMap.put("propertyType", "ref");
                        }
                    }
                    propertyList.add(propertyMap);
                }
                beanDefinition.setPropertyList(propertyList);
                RequestMapingMap.addBeanName(beanName);
                RequestMapingMap.putBeanDefinition(beanName, beanDefinition);
            }
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////
    public void createMybatisProxyFactory(String location){
        location = this.getClass().getResource("/").getPath() + location;
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(location);
        SqlSession session = factory.openSession();
        RequestMapingMap.setSqlSession(session);
    }
    public void mergeMybatisBeanToSingletonObjects(){
        SqlSession sqlSession = RequestMapingMap.getSqlSession();//getConfiguration();
        Configuration configuration = sqlSession.getConfiguration();
        Map<Class<?>, MapperProxyFactory<?>> knownMappers = configuration.getMapperRegistry().getKnownMappers();
        for(Class<?> x : knownMappers.keySet()){
            String keyOfSingletonObjects = x.getSimpleName();
            keyOfSingletonObjects = BeanUtils.firstLowerCase(keyOfSingletonObjects);
            Object mapperProxyFactory = sqlSession.getMapper(x);
            RequestMapingMap.getSingletonObjects().put(keyOfSingletonObjects, mapperProxyFactory);
        }
    }
    public List<HashMap<String, String>> getPropertyListForAnnotation(Class<?> T){
        List<HashMap<String,String>> propertyList = new ArrayList<HashMap<String,String>>();
        Field[] fields = T.getDeclaredFields();
        for(Field field: fields){
            if(field.isAnnotationPresent(myAutoWrited.class)){
                HashMap<String, String> propertyMap = new HashMap<String, String>();
                myAutoWrited autoWrited = (myAutoWrited) field.getAnnotation(myAutoWrited.class);
                String propertyName = autoWrited.value();
                if(propertyName == null || propertyName.trim().length() == 0){
                    propertyName = BeanUtils.firstLowerCase(field.getType().getSimpleName());
                }
                propertyMap.put("propertyName", propertyName);
                propertyMap.put("propertyType", "ref");
                propertyMap.put("propertyValue", field.getType().getName());
                propertyList.add(propertyMap);
            }
        }
        return propertyList;
    }
    public void handerRequestMaping(Class<?> T){
        Method[] methods = BeanUtils.findDeclaredMethods(T);
        for(Method m:methods){//循环方法，找匹配的方法进行执行
            if(m.isAnnotationPresent(myRequestMapping.class)){
                String anoPath = m.getAnnotation(myRequestMapping.class).value();
                if(anoPath!=null && !"".equals(anoPath.trim())){
                    if (RequestMapingMap.getRequesetMap().containsKey(anoPath)) {
                        throw new RuntimeException("RequestMapping映射的地址不允许重复！");
                    }
                    //把所有的映射地址存储起来  映射路径--类
                    RequestMapingMap.getRequesetMap().put(anoPath, T);
                }
            }
        }
    }
}
