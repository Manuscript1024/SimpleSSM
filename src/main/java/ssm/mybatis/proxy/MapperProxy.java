/**
 * 
 */
package ssm.mybatis.proxy;


import ssm.mybatis.data.MappedStatement;
import ssm.mybatis.session.SqlSession;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;


/**
 * Mapper代理
 * 
 * @author PLF
 * @date 2019年3月6日
 */
public class MapperProxy<T> implements InvocationHandler, Serializable
{

    private static final long serialVersionUID = -7861758496991319661L;

    private final SqlSession sqlSession;

    private final Class<T> mapperInterface;

    /**
     * 构造方法
     * 
     * @param sqlSession
     * @param mapperInterface
     */
    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface)
    {

        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }

    /**
     * 真正的执行方法
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable
    {
        try
        {
            Class class1 = Object.class;
            if (Object.class.equals(method.getDeclaringClass()))//如果是接口的父类object固有的方法，则直接执行，无须利用代理对象。如toString(), equals()等。
            {
                return method.invoke(this, args);
            }
            Object o = this.execute(method, args);
            return o;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * 根据SQL指令执行对应操作
     * 
     * @param method
     * @param args
     * @return 
     */
    private Object execute(Method method,  Object[] args)
    {
        String statementId = this.mapperInterface.getName() + "." + method.getName();
        MappedStatement ms = this.sqlSession.getConfiguration().getMappedStatement(statementId);
        
        Object result = null;
        switch (ms.getSqlCommandType())
        {
            case SELECT:
            {
                Class<?> returnType = method.getReturnType();
                
                // 如果返回的是list，应该调用查询多个结果的方法，否则只要查单条记录
                if (Collection.class.isAssignableFrom(returnType))
                {
                    //ID为mapper类全名+方法名
                    result = sqlSession.selectList(statementId, args);
                }
                else
                {
                    result = sqlSession.selectOne(statementId, args);
                }
                break;
            }
            case UPDATE:
            {
                sqlSession.update(statementId, args);
                break;
            }
            default:
            {
                //TODO 其他方法待实现
                break;
            }
        }
        
        return result;
    }
    
}
