package com.dam.utils.mail;

public class MailUtil {
    public final static String MATH_REGEX="^[a-zA-Z0-9._]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$";
    /**
     * 判断邮箱是否合法
     * @param mail
     * @return
     */
    public static boolean judgeWhetherTheMailIsLegal(String mail) {
        //正则表达式匹配
        if (mail==null||"".equals(mail)||!mail.matches(MATH_REGEX)){
            //--if--邮箱的格式不对
            return false;
        }else {
            return true;
        }
    }


}
