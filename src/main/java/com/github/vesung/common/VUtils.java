package com.github.vesung.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author wangjing.dc@qq.com
 * @since 2019/7/21
 */
public class VUtils {
    private static final Logger log = LoggerFactory.getLogger(VUtils.class);

    public static boolean isAjax(HttpServletRequest request){
        Enumeration<String> hnames = request.getHeaderNames();
        log.info(".........................................................");
        while(hnames.hasMoreElements()){
            String name = hnames.nextElement();
            log.info(name + "," + request.getHeader(name));
        }
        log.info(".........................................................");

        String contentType = request.getHeader("Content-Type");
        return  (request.getHeader("Content-Type") != null
                &&  (contentType.toLowerCase().indexOf("application/json") > -1
                        || contentType.toLowerCase().indexOf("x-www-form-urlencoded") > -1
        ) ) ;
    }

    /**
     * 解码html字符串
     * @param html
     * @return
     */
    public static String decodeHtml(String html) {
        if(html == null){
            return "";
        }

        html = html.replaceAll("& ", "&");
        return HtmlUtils.htmlUnescape(html);
    }

    /**
     * 字符串非空判断
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        if(str == null)
            return false;

        if("".equals(str)){
            return false;
        }

        return true;
    }
}
