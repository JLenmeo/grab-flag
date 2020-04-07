package com.jjw.element;

import com.jjw.utils.ComparedUtils;
import com.jjw.utils.StringUtils;
import com.jjw.vo.OrcVo;

import java.util.ArrayList;
import java.util.List;

/**
 * User:jiaw.j
 * Date:2020/3/26 0026
 *
 *      兽人
 *
 */
public class Orc implements Comparable<Orc> {

    //序号
    protected String num;

    //兽人的初始值
    private float origValue;

    //兽人的当前值
    protected float value;

    //一回合中兽人需变化的值
    private float changeValue;

    //是否保有旗帜，true是，false否
    protected boolean haveFlag;

    //是否存活，true是，false否
    protected boolean exist;

    //是否已判定过，true是，false否
    private boolean judge;

    //移动方向
    private Direction moveDir;

    //移动距离
    private int moveOff;

    //一回合的动作信息
    private String actionMsg;

    protected Position curPosition;

    public Orc(){}

    public Orc(String num,int spaceSize){

        this.num = num;
        this.origValue = (int) ((Math.random() * 100) + 1);
        this.value = origValue;
        this.haveFlag = false;
        this.exist = true;
        this.curPosition = new Position();
        randomBornPosition(spaceSize);

    }

    //随机出生点
    public void randomBornPosition(int spaceSize){

        this.curPosition.setX((int)(Math.random() * spaceSize));
        this.curPosition.setY((int)(Math.random() * spaceSize));

    }

    //计算与目标的正方向
    public Direction positiveDir(Orc target){

        int dirX = target.getCurPosition().getX() - curPosition.getX();
        int dirY = target.getCurPosition().getY() - curPosition.getY();

        return Direction.decideDir(dirX,dirY);

    }

    //计算与目标的反方向
    public Direction negativeDir(Orc target){

        int dirX = curPosition.getX() - target.getCurPosition().getX();
        int dirY = curPosition.getY() - target.getCurPosition().getY();

        return Direction.decideDir(dirX,dirY);

    }

    //决定逃离边界
    public Direction decideEscapeBorder(int spaceSize,List<Direction> ignoredBorder){

        if(curPosition.isCenter(spaceSize)){
            return Direction.NONE;
        }

        //距离边界
        int northDistance = -1;
        int southDistance = -1;
        int westDistance = -1;
        int eastDistance = -1;

        //计算各边界距离
        List<Integer> distances = new ArrayList<>();
        if(ignoredBorder == null || !ignoredBorder.contains(Direction.N)){
            northDistance = curPosition.getX();
            distances.add(northDistance);
        }

        if(ignoredBorder == null || !ignoredBorder.contains(Direction.S)){
            southDistance = spaceSize - curPosition.getX();
            distances.add(southDistance);
        }

        if(ignoredBorder == null || !ignoredBorder.contains(Direction.W)){
            westDistance = curPosition.getY();
            distances.add(westDistance);
        }

        if(ignoredBorder == null || !ignoredBorder.contains(Direction.E)){
            eastDistance = spaceSize - curPosition.getY();
            distances.add(eastDistance);
        }


        if(distances.size() == 0){
            return Direction.NONE;
        }
        //选出最小距离
        int minDistance = ComparedUtils.minInt(distances);


        List<Direction> dirs = new ArrayList<>();
        if(northDistance == minDistance){
            dirs.add(Direction.N);
        }

        if(southDistance == minDistance){
            dirs.add(Direction.S);
        }

        if(westDistance == minDistance){
            dirs.add(Direction.W);
        }

        if(eastDistance == minDistance){
            dirs.add(Direction.E);
        }


        int index = (int)((Math.random() * dirs.size()));

        return dirs.get(index);

    }

    //随机选择方向
    public Direction randomDirection(){

        int index = (int)((Math.random() * 4) + 1);

        if(index == 1){
            return Direction.N;
        }else if(index == 2){
            return Direction.E;
        }else if(index == 3){
            return Direction.S;
        }else{
            return Direction.W;
        }

    }

    //逃离移动动作
    public void escapeMoveAction(List<Direction> dirs,boolean haveEnemy){

        if(!haveEnemy){
            moveOff = 2;
        }
        moveDir = Direction.mergeDir(dirs);
        judge = true;

        actionMsg = num + "向" + moveDir.toString() + "方向逃离，移动" + moveOff + "格";

    }

    //夺旗动作
    public void grabFlagAction(Orc target){

        changeValue = changeValue - (value - 0.1f);
        haveFlag = true;
        moveDir = positiveDir(target);
        judge = true;

        target.setExist(false);

        if(target instanceof Flag){
            actionMsg = num + "向" + moveDir.toString() + "方向移动" + moveOff + "格，" + "并夺走了旗子";
        }else{
            actionMsg = num + "向" + moveDir.toString() + "方向移动" + moveOff + "格，" + "杀死了" + target.getNum() + "并夺走了它的旗子";
        }

    }

    //无法夺旗时绕旗动作
    public void aroundFlagAction(List<Orc> enemies){

        Orc maxEnemy = ComparedUtils.maxOrc(enemies);

        if(this == maxEnemy){
            //当前兽人是7x7内R值最大的兽人
            moveDir = positiveDir(enemies.get(enemies.size() - 2));
        }else{
            //当前兽人不是7x7内R值最大的兽人
            moveDir = negativeDir(maxEnemy);
        }

        judge = true;

        actionMsg = num + "无法夺旗，向" + moveDir.toString() + "方向移动" + moveOff + "格";

    }

    //夺旗移动动作
    public void grabFlagMoveAction(Orc target){

        moveDir = positiveDir(target);
        judge = true;

        actionMsg = num + "向" + moveDir.toString() + "方向移动" + moveOff + "格，靠近旗子目标" + target.getNum();

    }

    //杀戮动作
    public void killAction(Orc target){

        changeValue = changeValue - target.getValue();
        moveDir = positiveDir(target);
        judge = true;

        target.setExist(false);

        actionMsg = num + "向" + moveDir.toString() + "方向移动" + moveOff + "格，并杀死了" + target.getNum();

    }

    //杀戮移动动作
    public void killMoveAction(Orc target){

        moveDir = positiveDir(target);
        judge = true;

        actionMsg = num + "向" + moveDir.toString() + "方向移动" + moveOff + "格，靠近杀戮目标" + target.getNum();

    }

    //普通移动动作
    public void normalMoveAction(Direction positiveDir,Direction negativeDir){

        if(positiveDir == null && negativeDir == null){
            moveDir = randomDirection();
            moveOff = 2;
        }else{
            if(positiveDir == null){
                positiveDir = Direction.NONE;
            }

            if(negativeDir == null){
                negativeDir = Direction.NONE;
            }

            if(positiveDir.oppositeDir(negativeDir)){
                positiveDir = Direction.NONE;
            }

            List<Direction> dirs = new ArrayList<>();
            dirs.add(positiveDir);
            dirs.add(negativeDir);

            moveDir = Direction.mergeDir(dirs);
        }
        judge = true;

        actionMsg = num + "向" + moveDir.toString() + "方向移动" + moveOff + "格";

    }

    //恢复动作
    public void restoreAction(){

        if(!haveFlag && (value < origValue || changeValue < 0)){
            changeValue = changeValue + 1;
        }

    }

    //结算一回合的变动
    public void settle(int spaceSize){

        if(isHaveFlag()){
            value = 0.1f;
        }else{
            value = value + changeValue;
        }
        curPosition.move(moveDir,moveOff);

        if(!haveFlag && curPosition.isOutside(spaceSize)){
            reposition(spaceSize);
        }

    }

    //若出界了，需复位
    private void reposition(int spaceSize){

        int x = curPosition.getX();
        int y = curPosition.getY();

        if(x < 0){
            x = 0;
        }else if(x >= spaceSize){
            x = spaceSize - 1;
        }

        if(y < 0){
            y = 0;
        }else if(y >= spaceSize){
            y = spaceSize - 1;
        }

        curPosition.setX(x);
        curPosition.setY(y);

    }

    public String getNum() {
        return num;
    }

    public float getOrigValue() {
        return origValue;
    }

    public Position getCurPosition() {
        return curPosition;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public boolean isHaveFlag() {
        return haveFlag;
    }

    public void setHaveFlag(boolean haveFlag) {
        this.haveFlag = haveFlag;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public boolean isJudge() {
        return judge;
    }

    public void setJudge(boolean judge) {
        this.judge = judge;
    }

    public float getChangeValue() {
        return changeValue;
    }

    public void setChangeValue(float changeValue) {
        this.changeValue = changeValue;
    }

    public Direction getMoveDir() {
        return moveDir;
    }

    public void setMoveDir(Direction moveDir) {
        this.moveDir = moveDir;
    }

    public int getMoveOff() {
        return moveOff;
    }

    public void setMoveOff(int moveOff) {
        this.moveOff = moveOff;
    }

    public void setActionMsg(String actionMsg) {
        this.actionMsg = actionMsg;
    }

    public String getActionMsg() {
        return actionMsg;
    }

    @Override
    public String toString() {

        String str;
        if(haveFlag){
            str = num + "Flag-" + value;
        }else{
            str = num + "-" + value;
        }

        return String.format("[%" + StringUtils.calStrCenterLen(20,str,true) + "s%s%" + StringUtils.calStrCenterLen(20,str,false) + "s]","",str,"");

    }

    @Override
    public int compareTo(Orc target) {

        if(this == target){
            return 0;
        }

        if(this.value > target.getValue()){
            return 1;
        }else if(this.value == target.getValue()){
            return 0;
        }else{
            return -1;
        }

    }

    public OrcVo toOrcVo(int spaceSize){

        OrcVo orcVo = new OrcVo();
        orcVo.setNum(num);
        orcVo.setValue(value);
        orcVo.setHaveFlag(haveFlag);
        orcVo.setX(curPosition.getX());
        orcVo.setY(curPosition.getY());

        return orcVo;

    }

    //复制目标兽人状态
    public void copy(Orc target){

        num = target.getNum();
        origValue = target.getOrigValue();
        value = target.getValue();
        changeValue = target.getChangeValue();
        haveFlag = target.isHaveFlag();
        exist = target.isExist();
        judge = target.isJudge();
        moveDir = target.getMoveDir();
        moveOff = target.moveOff;
        actionMsg = target.getActionMsg();
        curPosition = new Position(target.getCurPosition().getX(),target.getCurPosition().getY());

    }

}
