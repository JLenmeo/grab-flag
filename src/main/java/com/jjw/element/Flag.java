package com.jjw.element;

import com.jjw.utils.StringUtils;

/**
 * User:jiaw.j
 * Date:2020/3/26 0026
 */
public class Flag extends Orc {

    public Flag(){}

    public Flag(String num,int spaceSize){

        this.num = num;
        this.value = 0.1f;
        this.haveFlag = true;
        this.exist = true;

        spaceSize = spaceSize - 1;
        this.curPosition = new Position(spaceSize / 2,spaceSize / 2);

    }

    @Override
    public String toString() {
        String str = "Flag-" + value;
        return String.format("[%" + StringUtils.calStrCenterLen(20,str,true) + "s%s%" + StringUtils.calStrCenterLen(20,str,false) + "s]","",str,"");
    }

    public void copy(Flag target){

        num = target.getNum();
        value = target.getValue();
        haveFlag = target.isHaveFlag();
        exist = target.isExist();
        curPosition = new Position(target.getCurPosition().getX(),target.getCurPosition().getY());

    }

}
