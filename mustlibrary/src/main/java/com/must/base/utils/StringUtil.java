package com.must.base.utils;

/**
 * Description:
 * 一款字符串帮助类
 * 不断会更新，因为不知道会如何操作字符串
 * @author lilu
 * @date 2019/8/22
 * This is a simple function, how do I do it.
 */

public class StringUtil {


    /**
     * 判断字符串是否为空
     * @param str 目标字符串
     * @return 是否为空
     */
    public static boolean isNullOrEmpty(String str){
        if(str == null){
            //为空
            return true;
        }else{
            if("".equals(str)){
                //空字符串
                return true;
            }else{
                //有值
                return false;
            }
        }
    }
}
