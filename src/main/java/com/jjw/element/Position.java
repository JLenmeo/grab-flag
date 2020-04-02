package com.jjw.element;

/**
 * User:jiaw.j
 * Date:2020/3/26 0026
 *
 *      坐标
 *
 */
public class Position {

    private int x;

    private int y;

    public Position(){}

    public Position(int x,int y){

        this.x = x;
        this.y = y;

    }

    //计算以当前位置为中心的range x range范围内是否存在target
    public boolean range(Position target,int range){

        //范围的偏移量
        int off = (range - 1) / 2;

        int tX = target.getX();
        int tY = target.getY();

        if(!(Math.abs( x - tX) <= off)){
            return false;
        }else if(!(Math.abs(y - tY) <= off)){
            return false;
        }else{
            return true;
        }

    }

    //当前坐标是否位于棋盘中心
    public boolean isCenter(int spaceSize){

        int centerPoint = spaceSize / 2;

        if(x == centerPoint && y == centerPoint){
            return true;
        }else{
            return false;
        }

    }

    //当前坐标是否出界
    public boolean isOutside(int spaceSize){

        if(x < 0 || x >= spaceSize){
            return true;
        }else if(y < 0 || y >= spaceSize){
            return true;
        }else{
            return false;
        }

    }

    //坐标移动
    public void move(Direction moveDir,int moveOff){

        if(moveDir.equals(Direction.N)){
            x = x - moveOff;
        }else if(moveDir.equals(Direction.E_N)){
            x = x - moveOff;
            y = y + moveOff;
        }else if(moveDir.equals(Direction.E)){
            y = y + moveOff;
        }else if(moveDir.equals(Direction.E_S)){
            x = x + moveOff;
            y = y + moveOff;
        }else if(moveDir.equals(Direction.S)){
            x = x + moveOff;
        }else if(moveDir.equals(Direction.W_S)){
            x = x + moveOff;
            y = y - moveOff;
        }else if(moveDir.equals(Direction.W)){
            y = y - moveOff;
        }else if(moveDir.equals(Direction.W_N)){
            x = x - moveOff;
            y = y - moveOff;
        }

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "[" + x + "," + y + "]";
    }
}
