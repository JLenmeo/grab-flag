package com.jjw.element;

import java.util.List;

/**
 * User:jiaw.j
 * Date:2020/3/27 0027
 *
 *      方向
 *
 */
public enum  Direction {

    NONE(0,0,"原地"),
    N(-1,0,"上"),
    S(1,0,"下"),
    W(0,-1,"左"),
    E(0,1,"右"),
    E_N(-1,1,"右上"),
    E_S(1,1,"右下"),
    W_N(-1,-1,"左上"),
    W_S(1,-1,"左下");

    private int x;

    private int y;

    private String msg;

    Direction(int x,int y,String msg){

        this.x = x;
        this.y = y;
        this.msg = msg;

    }

    //合并方向
    public static Direction mergeDir(List<Direction> dirs){

        while(dirs.size() >= 2){
            clearOpposite(dirs);

            caculateDir(dirs);
        }

        if(dirs.size() == 0){
            return NONE;
        }else{
            return dirs.get(0);
        }

    }

    //清除方向列表中相对的方向
    private static void clearOpposite(List<Direction> dirs){

        boolean cleared = false;
        for(int i = 0;i < dirs.size() - 1;i++){
            Direction dirA = dirs.get(i);
            for(int j = i + 1;j < dirs.size();j++){
                Direction dirB = dirs.get(j);

                if(dirA.oppositeDir(dirB)){
                    dirs.remove(dirA);
                    dirs.remove(dirB);
                    cleared = true;
                    break;
                }
            }

            if(cleared){
                break;
            }

        }

        if(cleared){
            clearOpposite(dirs);
        }

    }

    //计算方向
    private static List<Direction> caculateDir(List<Direction> dirs){

        if(dirs.size() == 2){
            Direction dirA = dirs.remove(0);
            Direction dirB = dirs.remove(0);

            Direction resultDir = decideDir(dirA.getX() + dirB.getX(),dirA.getY() + dirB.getY());
            dirs.add(resultDir);
        }else{
            boolean caculated = false;
            for(int i = 0;i < dirs.size() - 1;i++){
                Direction dirA = dirs.get(i);
                for(int j = i + 1;j < dirs.size();j++){
                    Direction dirB = dirs.get(j);

                    if(dirA.canCaculate(dirB)){
                        Direction resultDir = decideDir(dirA.getX() + dirB.getX(),dirA.getY() + dirB.getY());
                        dirs.remove(dirA);
                        dirs.remove(dirB);
                        dirs.add(resultDir);
                        caculated = true;
                        break;
                    }else{
                        continue;
                    }
                }

                if(caculated){
                    break;
                }
            }

            if(!caculated){
                //如果在大于2个方向的基础上，还是没有计算结果，就强制计算
                Direction dirA = dirs.remove(0);
                Direction dirB = dirs.remove(0);

                Direction resultDir = decideDir(dirA.getX() + dirB.getX(),dirA.getY() + dirB.getY());
                dirs.add(resultDir);
            }
        }

        return dirs;

    }

    //计算规则
    private boolean canCaculate(Direction target){

        if(this.equals(N)){
            if(target.equals(W_N) || target.equals(E_N) || target.equals(N)){
                return false;
            }else{
                return true;
            }
        }else if(this.equals(E_N)){
            if(target.equals(N) || target.equals(E) || target.equals(E_N)){
                return false;
            }else{
                return true;
            }
        }else if(this.equals(E)){
            if(target.equals(E_N) || target.equals(E_S) || target.equals(E)){
                return false;
            }else{
                return true;
            }
        }else if(this.equals(E_S)){
            if(target.equals(E) || target.equals(S) || target.equals(E_S)){
                return false;
            }else{
                return true;
            }
        }else if(this.equals(S)){
            if(target.equals(W_S) || target.equals(E_S) || target.equals(S)){
                return false;
            }else{
                return true;
            }
        }else if(this.equals(W_S)){
            if(target.equals(W) || target.equals(S) || target.equals(W_S)){
                return false;
            }else{
                return true;
            }
        }else if(this.equals(W)){
            if(target.equals(W_S) || target.equals(W_N) || target.equals(W)){
                return false;
            }else{
                return true;
            }
        }else if(this.equals(W_N)){
            if(target.equals(W) || target.equals(N) || target.equals(W_N)){
                return false;
            }else{
                return true;
            }
        }else{
            return true;
        }

    }

    //确定方向
    public static Direction decideDir(int dirX,int dirY){

        if(dirX < 0 && dirY == 0){
            return N;
        }else if(dirX < 0 && dirY > 0){
            return E_N;
        }else if(dirX < 0 && dirY < 0){
            return W_N;
        }else if(dirX > 0 && dirY == 0){
            return S;
        }else if(dirX > 0 && dirY > 0){
            return E_S;
        }else if(dirX > 0 && dirY < 0){
            return W_S;
        }else if(dirX == 0 && dirY < 0){
            return W;
        }else if(dirX == 0 && dirY > 0){
            return E;
        }else{
            return NONE;
        }

    }

    //是否和目标方向是相对方向
    public boolean oppositeDir(Direction target){

        if(this.isNone() && target.isNone()){
            return false;
        }

        if((x + target.getX()) == 0 && (y + target.getY()) == 0){
            return true;
        }else{
            return false;
        }

    }

    //是不是没方向
    public boolean isNone(){

        if(x == 0 && y == 0){
            return true;
        }else{
            return false;
        }

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getMsg(){
        return msg;
    }

    @Override
    public String toString() {
        return msg;
    }

}
