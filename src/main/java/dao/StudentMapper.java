/**
 * 
 */
package dao;



import model.Role;
import model.Student;

import java.util.List;


/**
 * UserMapper.java
 * 
 * @author PLF
 * @date 2019年3月6日
 */
public interface StudentMapper
{

    /**
     * 获取单个user
     * 
     * @param id
     * @return 
     * @see 
     */
    Student getStudent(String id);
    
    /**
     * 获取所有用户
     * 
     * @return 
     * @see 
     */
    List<Student> getAll();
    
    /**
     * 更新用户（功能未完成）
     * 
     * @param id 
     */
    void updateStudent(String id);
}
