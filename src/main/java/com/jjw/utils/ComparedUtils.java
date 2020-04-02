package com.jjw.utils;

import com.jjw.element.Orc;

import java.util.Collections;
import java.util.List;

/**
 * User:jiaw.j
 * Date:2020/3/30 0030
 */
public class ComparedUtils {

    //比较source是否大于target
    public static boolean biggerOrc(Orc source, Orc target){

        if(source.compareTo(target) == 1){
            return true;
        }else{
            return false;
        }

    }

    //找出最小的整数
    public static int minInt(List<Integer> params){

        Collections.sort(params);
        return params.get(0);

    }

    //找出最大的兽人
    public static Orc maxOrc(List<Orc> orcs){

        Collections.sort(orcs);
        return orcs.get(orcs.size() - 1);

    }

    public static Orc minOrc(List<Orc> orcs){

        Collections.sort(orcs);
        return orcs.get(0);

    }

}
