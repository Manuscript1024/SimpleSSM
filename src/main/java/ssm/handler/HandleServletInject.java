package ssm.handler;

import dao.StudentMapper;
import ssm.annotation.myRequestMapping;
import ssm.data.BeanDefinition;
import ssm.utils.BeanUtils;
import ssm.utils.RequestMapingMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HandleServletInject {
    private List<String> beanNames;
    private HashMap<String, BeanDefinition> beanClassMap;
    private HashMap<String, Object> singletonObjects;
    private HashMap<String, Object> earlySingletonObjects;
    private HashMap<String, Object> singletonFactories;
    private List<String> singletonsCurrentlyInCreation;
    private Map<String, Class<?>> requesetMap;
    HandleServletInject(){
        this.beanNames = RequestMapingMap.getBeanNames();
        this.beanClassMap = RequestMapingMap.getBeanClassMap();
        this.singletonObjects = RequestMapingMap.getSingletonObjects();
        this.earlySingletonObjects = RequestMapingMap.getEarlySingletonObjects();
        this.singletonFactories = RequestMapingMap.getSingletonFactories();
        this.singletonsCurrentlyInCreation = RequestMapingMap.getSingletonsCurrentlyInCreation();
        this.requesetMap = RequestMapingMap.getRequesetMap();
    }
    public void doLoadBeanDefinitions(){
        Iterator var1 = this.beanNames.iterator();

        while(var1.hasNext()) {
            String beanName = (String)var1.next();
            BeanDefinition beanDefinition = (BeanDefinition)this.beanClassMap.get(beanName);
            try{
                this.doGetBean(beanDefinition);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    private Object doGetBean(BeanDefinition beanDefinition){
        Object bean = null;
        String beanName = beanDefinition.getId();
        Object shareInstance = this.getSingleton(beanName, true);
        if (shareInstance != null) {
            bean = shareInstance;
        } else {
            Object singletonObject = this.getSingleton(beanDefinition);
            bean = singletonObject;
        }

        return bean;
    }

    private Object getSingleton(String beanName, boolean allowEarlyReference){//用于检测有没有
        Object beanObject = this.singletonObjects.get(beanName);
        if (beanObject == null && this.singletonsCurrentlyInCreation.contains(beanName)) {
            beanObject = this.earlySingletonObjects.get(beanName);
            if (beanObject == null && allowEarlyReference) {
                Object singletonFactory = this.singletonFactories.get(beanName);
                if (singletonFactory != null) {
                    beanObject = singletonFactory;
                    this.singletonFactories.remove(beanName);
                    this.earlySingletonObjects.put(beanName, singletonFactory);
                }
            }
        }

        return beanObject;
    }

    private Object getSingleton(BeanDefinition beanDefinition){//用于创建bean。
        String beanName = beanDefinition.getId();
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null) {
            singletonObject = this.createBean(beanDefinition);
            this.singletonObjects.put(beanName, singletonObject);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);

            //如果有接口，自动注入它的实现类
            try {
                Class<?>[] interfaces = beanDefinition.getBeanClass().getInterfaces();
                for (Class<?> c : interfaces) {
                    this.singletonObjects.put(BeanUtils.firstLowerCase(c.getSimpleName()), singletonObject);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return singletonObject;
    }

    private Object createBean(BeanDefinition beanDefinition){
        String beanName = beanDefinition.getId();
        this.singletonsCurrentlyInCreation.add(beanName);
        Object bean = null;
        try{
            bean = beanDefinition.getBeanClass().newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
        if (!this.singletonObjects.containsKey(beanName)) {
            this.singletonFactories.put(beanName, bean);
            this.earlySingletonObjects.remove(beanName);
        }

        this.populateBean(bean, beanDefinition.getPropertyList());
        return bean;
    }

    private void populateBean(Object bean, List<HashMap<String, String>> pvs){//为bean属性赋值
        try {
            for(int i = 0; i < pvs.size(); ++i) {
                HashMap<String, String> property = (HashMap)pvs.get(i);
                String propName = (String)property.get("propertyName");
                String propValue = (String)property.get("propertyValue");
                String propType = (String)property.get("propertyType");
                Field declaredField = bean.getClass().getDeclaredField(propName);
                if ("string".equals(propType)) {
                    declaredField.set(bean, propValue);
                } else {
                    //如果是个接口，则转换为其实现类名。
                    if(RequestMapingMap.getInterfaceMapClass().containsKey(propName)){
                        propName = RequestMapingMap.getInterfaceMapClass().get(propName);
                    }
                    Object beanObject = this.singletonObjects.get(propName);
                    declaredField.setAccessible(true);
                    if (beanObject != null) {
                        StudentMapper studentMapper = (StudentMapper) beanObject;
                        declaredField.set(bean, studentMapper);
                    } else {
                        Object refBean = this.doGetBean((BeanDefinition)this.beanClassMap.get(propName));//递归调用，创建bean.
                        declaredField.set(bean, refBean);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public Object getBean(String id) throws Exception {
        return this.singletonObjects.get(id);
    }

}
