package com.jjw.utils;

/**
 * User:jiaw.j
 * Date:2020/3/26 0026
 */
public class StringUtils {

    public static int calStrCenterLen(int totalLen,String tarStr,boolean startEndFlg){

        int tarLen = tarStr.length();

        int result = (totalLen - tarLen)/2;

        if(startEndFlg){
            return result;
        }

        if((totalLen - tarLen)%2 != 0){
            result += 1;
        }

        return result;

    }

}
