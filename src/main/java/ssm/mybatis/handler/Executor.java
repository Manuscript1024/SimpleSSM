/**
 * 
 */
package ssm.mybatis.handler;

import ssm.mybatis.data.MappedStatement;

import java.util.List;


/**
 * Executor.java
 * 
 * @author PLF
 * @date 2019年3月6日
 */
public interface Executor
{

    /**
     * 查询数据库
     * 
     * @param ms
     * @param parameter
     * @return 
     * @see 
     */
    <E> List<E> doQuery(MappedStatement ms, Object parameter);
    
    /**
     * 更新操作
     * 
     * @param ms
     * @param parameter 
     */
    void doUpdate(MappedStatement ms, Object parameter);
}
