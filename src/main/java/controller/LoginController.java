package controller;

import ssm.annotation.myRequestMapping;
import ssm.annotation.myController;
import ssm.data.View;
import ssm.data.ViewData;
import ssm.utils.WebContext;

import javax.servlet.http.HttpServletRequest;

/**
 * @author itguang
 * @create 2018-04-06 09:26
 **/
@myController
public class LoginController {


    //使用RequestMapping注解指明forward1方法的访问路径
    @myRequestMapping("login2")
    public View forward1() {


        System.out.println("login2...");

//        HttpServletRequest request = WebContext.requestHodler.get();
//
//        String username = request.getParameter("username");
//        String password = request.getParameter("password");

        //执行完forward1方法之后返回的视图
        return new View("/Login2.jsp");
    }

    /**
     * 处理登录请求,接受参数
     * @return
     */
    @myRequestMapping("login")
    public View login(){

        System.out.println("login...");

        //首先获取当前线程的request对象
        HttpServletRequest request = WebContext.requestHodler.get();

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        // 相当于
        // request.setAttribute("msg","欢迎你"+username);


        return new View("/index.jsp");
    }






}
