package controller;
import model.GetUserInfo;
import service.ServiceImpl.HomeService;
import ssm.annotation.myAutoWrited;
import ssm.annotation.myController;
import ssm.annotation.myRequestMapping;

import java.util.List;

@myController
public class HomeController {
    @myAutoWrited
    private HomeService homeService;

    @myRequestMapping("sayHi")
    public String sayHi() {
        return homeService.sayHi();
    }

    @myRequestMapping("getName")
    public List<String> getName(Integer id, String no) {
        return homeService.getName(id,no);
    }
    @myRequestMapping("getRequestBody")
    public String getRequestBody(Integer id, String no, GetUserInfo userInfo) {
        return homeService.getRequestBody(id,no,userInfo);
    }
}
