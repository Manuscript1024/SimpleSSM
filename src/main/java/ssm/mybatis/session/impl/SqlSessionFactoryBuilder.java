/**
 * 
 */
package ssm.mybatis.session.impl;


import ssm.mybatis.config.Configuration;
import ssm.mybatis.session.SqlSessionFactory;
import ssm.mybatis.session.impl.DefaultSqlSessionFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * SqlSessionFactoryBuilder.java
 * 
 * @author PLF
 * @date 2019年3月7日
 */
public class SqlSessionFactoryBuilder
{

    /**
     * build
     * 
     * @param fileName
     * @return 
     * @see 
     */
    public SqlSessionFactory build(String fileName) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fileName);
        }catch (Exception e){
            e.printStackTrace();
        }
        return build(inputStream);
    }

    /**
     * build
     * 
     * @param inputStream
     * @return
     * @see 
     */
    public SqlSessionFactory build(InputStream inputStream)
    {
        try
        { ;
            Configuration.getPROPS().load(inputStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Configuration configuration = new Configuration();
        return new DefaultSqlSessionFactory(configuration);
    }
}
