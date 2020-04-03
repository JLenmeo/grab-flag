package com.jjw.element;

import com.jjw.utils.ComparedUtils;
import com.jjw.vo.OrcVo;

import java.util.*;

/**
 * User:jiaw.j
 * Date:2020/3/26 0026
 *
 *      棋盘
 *
 */
public class Checkerboard {

    //场地大小
    private int spaceSize;

    //初始兽人数量
    private int orcNum;

    //存活兽人列表
    private List<Orc> orcList = new ArrayList<>();

    //标志旗帜是否已被夺走
    private boolean alreadyGrab;

    //标志游戏是否结束
    private boolean gameover;

    //以当前兽人为中心11x11范围内的其他单位
    private Map<Orc,List<Orc>> relation11x11 = new HashMap<>();

    //以当前兽人为中心9x9范围内的其他单位
    private Map<Orc,List<Orc>> relation9x9 = new HashMap<>();

    //以当前兽人为中心7x7范围内的其他单位
    private Map<Orc,List<Orc>> relation7x7 = new HashMap<>();

    //以当前兽人为中心5x5范围内的其他单位
    private Map<Orc,List<Orc>> relation5x5 = new HashMap<>();

    //以当前兽人为中心3x3范围内的其他单位
    private Map<Orc,List<Orc>> relation3x3 = new HashMap<>();

    //回合结算信息
    private List<String> settleInfos = new ArrayList<>();

    public Checkerboard(){}

    public Checkerboard(int size,int orcNum){

        this.spaceSize = size;
        this.orcNum = orcNum;
        this.alreadyGrab = false;
        this.gameover = false;

        init();

    }

    //初始化棋盘布局
    private void init(){

        //生成旗帜
        Flag flag = new Flag("F",spaceSize);
        orcList.add(flag);

        //生成兽人
        Set<String> usedPosition = new HashSet<>();
        usedPosition.add(flag.getCurPosition().toString());
        for(int i = 1;i <= orcNum;i++){
            Orc orc = new Orc("P" + i,spaceSize);
            while(usedPosition.contains(orc.curPosition.toString())){
                orc.randomBornPosition(spaceSize);
            }
            usedPosition.add(orc.getCurPosition().toString());
            orcList.add(orc);
        }

    }

    //判定阶段
    public void judgeStage(){

        clearStatus();
        calculateRelation();

        escapeJudge();
        grabFlagJudge();
        grabFlagMoveJudge();
        killJudge();
        killMoveJudge();
        normalMoveJudge();
        restoreJudge();

    }

    //结算阶段
    public void settleStage(){

        StringBuilder dieMsg = new StringBuilder("");
        settleInfos.clear();
        List<Orc> dieOrc = new ArrayList<>();

        for(Orc orc : orcList){
            if(!orc.isExist()){
                dieOrc.add(orc);
                if(!(orc instanceof Flag)){
                    dieMsg.append("兽人").append(orc.getNum()).append("被淘汰；");
                }
                continue;
            }

            orc.settle(spaceSize);
            settleInfos.add(orc.getActionMsg());

            if(orc.isHaveFlag() && orc.curPosition.isOutside(spaceSize)){
                gameover = true;
            }
        }

        if(dieMsg.length() != 0){
            settleInfos.add(dieMsg.toString());
        }
        orcList.removeAll(dieOrc);

    }

    //初始每回合兽人的状态
    private void clearStatus(){

        for(Orc orc : orcList){
            orc.setChangeValue(0.0f);
            orc.setJudge(false);
            orc.setMoveDir(Direction.NONE);
            orc.setMoveOff(1);
            orc.setActionMsg("");
        }

    }

    //计算各单位间的关系
    private void calculateRelation(){

        calculateRelation(relation3x3,3);
        calculateRelation(relation5x5,5);
        calculateRelation(relation7x7,7);
        calculateRelation(relation9x9,9);
        calculateRelation(relation11x11,11);

    }

    private void calculateRelation(Map<Orc,List<Orc>> relation,int range){

        relation.clear();

        for(Orc orcA : orcList){

            List<Orc> targetList = new ArrayList<>();

            for(Orc orcB : orcList){
                if (orcA == orcB){
                    continue;
                }

                if(orcA.getCurPosition().range(orcB.getCurPosition(),range)){
                    targetList.add(orcB);
                }

            }

            relation.put(orcA,targetList);

        }

    }

    //逃离判定
    private void escapeJudge(){

        if(!isAlreadyGrab()){
            //旗子还未被任何兽人夺走
            return;
        }

        for(Orc escapeOrc : orcList){
            if(!escapeOrc.isHaveFlag()){
                continue;
            }

            boolean haveEnemy = true;
            List<Direction> dirs = new ArrayList<>();

            //确定首要逃离边界
            Direction escapeBorder = escapeOrc.decideEscapeBorder(spaceSize,null);
            dirs.add(escapeBorder);

            if(relation3x3.get(escapeOrc).size() > 0){
                //离自己1格内有敌人
                caculateEscapeDir(relation3x3,escapeOrc,escapeBorder,dirs);
            }else if(relation5x5.get(escapeOrc).size() > 0){
                //离自己2格内有敌人
                caculateEscapeDir(relation5x5,escapeOrc,escapeBorder,dirs);
            }else if(relation7x7.get(escapeOrc).size() > 0){
                //离自己3格内有敌人
                caculateEscapeDir(relation7x7,escapeOrc,escapeBorder,dirs);
            }else if(relation9x9.get(escapeOrc).size() > 0){
                //离自己4格内有敌人
                caculateEscapeDir(relation9x9,escapeOrc,escapeBorder,dirs);
            }else if(relation11x11.get(escapeOrc).size() > 0){
                //离自己5格内有敌人
                caculateEscapeDir(relation11x11,escapeOrc,escapeBorder,dirs);
            }else{
                if(escapeOrc.getCurPosition().isCenter(spaceSize)){
                    //周围没有敌人且在棋盘中心
                    dirs.add(escapeOrc.randomDirection());
                }

                haveEnemy = false;
            }

            escapeOrc.escapeMoveAction(dirs,haveEnemy);

            return;
        }

    }

    //计算逃离方向
    private void caculateEscapeDir(Map<Orc,List<Orc>> relation,Orc escapeOrc,Direction escapeBorder,List<Direction> dirs){

        List<Orc> enemies = relation.get(escapeOrc);
        List<Direction> ignoredBorder = new ArrayList<>();

        for(Orc enemy : enemies){
            if(escapeBorder == escapeOrc.positiveDir(enemy)){
                //在首要逃离边界的方向上出现正向的敌人，将首要逃离边界改为次要逃离边界
                dirs.remove(0);
                ignoredBorder.add(escapeBorder);
                dirs.add(escapeOrc.decideEscapeBorder(spaceSize,ignoredBorder));
            }

            dirs.add(escapeOrc.negativeDir(enemy));
        }

    }

    //夺旗判定
    private void grabFlagJudge(){

        for(Orc orcA : orcList){
            if(orcA.isJudge() || orcA instanceof Flag){
                //已行动过
                continue;
            }

            List<Orc> targets = relation3x3.get(orcA);

            if(targets.size() == 0){
                continue;
            }

            //旗子R值总是最小的
            Orc minTarget = ComparedUtils.minOrc(targets);
            if(minTarget.isHaveFlag()){
                List<Orc> enemies = relation7x7.get(minTarget);

                if(enemies.size() == 1){
                    //目标7x7范围内无敌人，夺旗
                    orcA.grabFlagAction(minTarget);
                    if(minTarget instanceof Flag){
                        alreadyGrab = true;
                    }
                    break;
                }else{
                    //目标7x7范围内有其他兽人
                    orcA.aroundFlagAction(enemies);
                }
            }
        }

    }

    //夺旗移动判定
    private void grabFlagMoveJudge(){

        for(Orc orcA : orcList){
            if(orcA.isJudge() || orcA instanceof Flag){
                //已行动过
                continue;
            }

            List<Orc> targets = relation11x11.get(orcA);

            if(targets.size() == 0){
                continue;
            }

            //找出当前兽人11x11内最小的目标
            Orc minTarget = ComparedUtils.minOrc(targets);
            if(minTarget.isHaveFlag()){
                //最小目标带有旗子
                orcA.grabFlagMoveAction(minTarget);
            }
        }

    }

    //杀戮判定
    private void killJudge(){

        for(Orc orcA : orcList){
            if(orcA.isJudge() || orcA instanceof Flag){
                //已行动过
                continue;
            }


            List<Orc> targets = relation3x3.get(orcA);

            if(targets.size() == 0){
                continue;
            }

            //找出当前兽人3x3内最小的目标
            Orc minTarget = ComparedUtils.minOrc(targets);
            if(ComparedUtils.biggerOrc(minTarget,orcA)){
                //比最小的目标还小，直接不用判定
                continue;
            }

            //找出最小目标5x5内的兽人
            List<Orc> enemies = relation5x5.get(minTarget);
            if(enemies.size() == 1){
                //目标5x5内无其他兽人，杀
                orcA.killAction(minTarget);
            }else{
                //目标5x5内有其他兽人
                Orc maxEnemy = ComparedUtils.maxOrc(enemies);
                if(orcA == maxEnemy){
                    //当前兽人是目标5x5内R值最大的兽人
                    float tmpValue = orcA.getValue() - minTarget.getValue();
                    if(tmpValue >= enemies.get(enemies.size() - 2).getValue()){
                        //若当前兽人杀戮后，R值大于目标5x5内次大兽人的R值，杀
                        orcA.killAction(minTarget);
                    }
                }
            }
        }

    }

    //杀戮移动判定
    private void killMoveJudge(){

        for(Orc orcA : orcList){
            if(orcA.isJudge() || orcA instanceof Flag){
                //已行动过
                continue;
            }


            List<Orc> targets = relation7x7.get(orcA);

            if(targets.size() == 0){
                continue;
            }

            //排序，以R值最小且能杀的为移动目标
            Orc minTarget = ComparedUtils.minOrc(targets);
            if(ComparedUtils.biggerOrc(minTarget,orcA)){
                //比最小的目标还小，直接不用判定
                continue;
            }
            for(Orc target : targets){
                if(ComparedUtils.biggerOrc(target,orcA)){
                    //当目标的值开始大于当前兽人时，那也可以不用对之后的目标判定了
                    break;
                }
                List<Orc> enemies = relation5x5.get(target);

                float tmpValue = orcA.getValue() - target.getValue();
                if(enemies.size() == 0){
                    //目标5x5内没有兽人
                    orcA.killMoveAction(target);
                    break;
                }else if(enemies.size() == 1){
                    //目标5x5内有一个兽人
                    Orc enemy = enemies.get(0);

                    if(orcA == enemy){
                        //该兽人为当前兽人
                        orcA.killMoveAction(target);
                        break;
                    }else{
                        //该兽人不是当前兽人
                        if(tmpValue >= enemy.getValue()){
                            //若当前兽人杀戮后，R值大于该敌人R值，移动
                            orcA.killMoveAction(target);
                            break;
                        }else{
                            continue;
                        }
                    }
                }else{
                    //目标5x5内有多名兽人
                    Orc maxEnemy = ComparedUtils.maxOrc(enemies);

                    if(orcA == maxEnemy){
                        //当前兽人是目标5x5内R值最大兽人
                        if(tmpValue >= enemies.get(enemies.size() - 2).getValue()){
                            //若当前兽人杀戮后，R值大于目标5x5内次大兽人的R值，移动
                            orcA.killMoveAction(target);
                            break;
                        }else{
                            continue;
                        }
                    }else{
                        //当前兽人不是目标5x5内R置最大兽人
                        if(tmpValue >= maxEnemy.getValue()){
                            //若当前兽人杀戮后，R值大于目标5x5内最大兽人的R值，移动
                            orcA.killMoveAction(target);
                            break;
                        }else{
                            continue;
                        }
                    }
                }
            }
        }

    }

    //普通移动判定
    private void normalMoveJudge(){

        for(Orc orcA : orcList){
            if(orcA.isJudge() || orcA instanceof Flag){
                //已行动过
                continue;
            }

            Map<String,Direction> dirs = new HashMap<>();

            caculateNormalDir(dirs,orcA,relation3x3.get(orcA));
            caculateNormalDir(dirs,orcA,relation5x5.get(orcA));
            caculateNormalDir(dirs,orcA,relation7x7.get(orcA));
            caculateNormalDir(dirs,orcA,relation9x9.get(orcA));
            caculateNormalDir(dirs,orcA,relation11x11.get(orcA));

            orcA.normalMoveAction(dirs.get("positiveDir"),dirs.get("negativeDir"));
        }

    }

    //计算普通移动的方向
    private void caculateNormalDir(Map<String,Direction> dirs,Orc orcA,List<Orc> targets){

        if(targets.size() == 0 || (dirs.get("positiveDir") != null && dirs.get("negativeDir") != null)){
            return;
        }

        if(targets.size() == 1){
            Orc target = targets.get(0);
            if(ComparedUtils.biggerOrc(orcA,target)){
                //当前范围内有一个比自己小的兽人
                if(dirs.get("positiveDir") == null){
                    dirs.put("positiveDir",orcA.positiveDir(target));
                }
            }else{
                //当前范围内有一个比自己大的兽人
                if(dirs.get("negativeDir") == null){
                    dirs.put("negativeDir",orcA.negativeDir(target));
                }
            }
        }else{
            Orc maxOrc = ComparedUtils.maxOrc(targets);
            Orc minOrc = targets.get(0);

            if(ComparedUtils.biggerOrc(orcA,maxOrc)){
                //当前范围内自己是最大的兽人
                if(dirs.get("positiveDir") == null){
                    dirs.put("positiveDir",orcA.positiveDir(minOrc));
                }
            }else if(ComparedUtils.biggerOrc(minOrc,orcA)){
                //当前范围内自己是最小的兽人
                if(dirs.get("negativeDir") == null){
                    dirs.put("negativeDir",orcA.negativeDir(maxOrc));
                }
            }else{
                //当前范围内自己是中等的兽人
                if(dirs.get("positiveDir") == null){
                    dirs.put("positiveDir",orcA.positiveDir(minOrc));
                }
                if(dirs.get("negativeDir") == null){
                    dirs.put("negativeDir",orcA.negativeDir(maxOrc));
                }
            }
        }

    }

    //恢复判定
    private void restoreJudge(){

        for(Orc orcA : orcList){
            orcA.restoreAction();
        }

    }

    public boolean isAlreadyGrab() {
        return alreadyGrab;
    }

    public boolean isGameover() {
        return gameover;
    }

    public int getSpaceSize() {
        return spaceSize;
    }

    public int getOrcNum() {
        return orcNum;
    }

    public List<Orc> getOrcList() {
        return orcList;
    }

    public Map<Orc, List<Orc>> getRelation11x11() {
        return relation11x11;
    }

    public Map<Orc, List<Orc>> getRelation9x9() {
        return relation9x9;
    }

    public Map<Orc, List<Orc>> getRelation7x7() {
        return relation7x7;
    }

    public Map<Orc, List<Orc>> getRelation5x5() {
        return relation5x5;
    }

    public Map<Orc, List<Orc>> getRelation3x3() {
        return relation3x3;
    }

    public List<String> getSettleInfos() {
        return settleInfos;
    }

    //获取当前存活兽人数量
    public int getCurOrcNum(){

        if(isAlreadyGrab()){
            return orcList.size();
        }else{
            return orcList.size() - 1;
        }


    }

    public List<OrcVo> getOrcVos(){

        List<OrcVo> orcVos = new ArrayList<>();
        for(Orc orc : orcList){
            orcVos.add(orc.toOrcVo(spaceSize));
        }

        return orcVos;

    }

    //复制目标棋盘状态
    public void copy(Checkerboard target){

        spaceSize = target.getSpaceSize();
        orcNum = target.getOrcNum();
        alreadyGrab = target.isAlreadyGrab();
        gameover = target.isGameover();

        for(Orc orc : target.getOrcList()){
            if(orc instanceof Flag){
                Flag cloneFlag = new Flag();
                cloneFlag.copy((Flag)orc);
                orcList.add(cloneFlag);
            }else{
                Orc cloneOrc = new Orc();
                cloneOrc.copy(orc);
                orcList.add(cloneOrc);
            }
        }

        for(String settleInfo : target.getSettleInfos()){
            settleInfos.add(settleInfo);
        }

    }


}
