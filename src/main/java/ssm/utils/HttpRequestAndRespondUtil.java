package ssm.utils;

import javax.servlet.http.HttpServletRequest;

public class HttpRequestAndRespondUtil {
    /**
     * 从HttpRequest中解析出 请求路径,即 RequestMapping() 的value值.
     *
     * @param request
     * @return
     */
    public static String pareRequestURI(HttpServletRequest request) {

        String path = request.getContextPath() + "/";
        String requestUri = request.getRequestURI();
        String midUrl = requestUri.replace(path, "");
        String lastUrl = midUrl.substring(0, midUrl.lastIndexOf("."));
        return lastUrl;
    }
}
