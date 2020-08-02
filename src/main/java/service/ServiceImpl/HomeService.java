package service.ServiceImpl;

import dao.StudentMapper;
import model.GetUserInfo;
import model.Student;
import service.IStudentService;
import ssm.annotation.myAutoWrited;
import ssm.annotation.myService;
import service.IHomeService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: test-pro
 * @Package com.honstat.spring.service
 * @Description: TODO
 * @date 2019/8/13 16:40
 */
@myService
public class HomeService  implements IHomeService {

    @myAutoWrited
    IStudentService iStudentService;
    @myAutoWrited
    StudentMapper studentMapper;
    public String sayHi() {
        studentMapper.updateStudent("09");
        List<Student> list = studentMapper.getAll();
        StringBuilder show = new StringBuilder();
        show.append("数据库中有：");
        for(Student x: list){
            show.append(x.getS_name());
            show.append('-');
        }
        show.append("的信息");
        show.append("   注解产生的循环依赖问题解决效果：");
        show.append(iStudentService.sayHi());
        return  show.toString();
    }

    @Override
    public List<String> getName(Integer id, String no) {
        List<String> list = new ArrayList<>();
        list.add("Hello! ");
        list.add(String.valueOf(id)+" ");
        list.add(no);
        return list;
    }
    @Override
    public String getRequestBody(Integer id, String no, GetUserInfo userInfo) {
        return "userName="+userInfo.getName()+" no="+no;
    }
}
