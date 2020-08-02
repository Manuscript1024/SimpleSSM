package service.ServiceImpl;

import service.IStudentService;
import ssm.annotation.myAutoWrited;
import ssm.annotation.myService;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: test-pro
 * @Package com.honstat.spring.service.impl
 * @Description: TODO
 * @date 2019/8/13 16:42
 */
@myService
public class StudentService  implements IStudentService {
    @myAutoWrited
    private HomeService homeService;
    @Override
    public String sayHi(){
        return "Hello world!";
    }
}
