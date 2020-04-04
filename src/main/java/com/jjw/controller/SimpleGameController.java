package com.jjw.controller;

import com.alibaba.fastjson.JSONObject;
import com.jjw.element.Checkerboard;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * User:jiaw.j
 * Date:2020/4/3 0003
 *
 *      简陋版
 *
 */
@Controller
@RequestMapping("/simple")
public class SimpleGameController {

    public static final int MAX_SPACE_SIZE = 101;

    public static final int MIN_SPACE_SIZE = 19;

    private int spaceSize = 19;

    private int orcNum = 4;

    private int round;

    private Checkerboard checkerboard;

    private Map<Integer,Checkerboard> histories = new HashMap<>();

    //简陋版页面
    @RequestMapping("/index")
    public String simplePage(){
        return "simple";
    }

    @RequestMapping("/changeSpaceSize")
    @ResponseBody
    public JSONObject changeSpaceSize(@RequestBody JSONObject request){

        JSONObject response = new JSONObject();
        try{
            int changeSize = Integer.parseInt(request.getString("spaceSize"));

            if(changeSize > MAX_SPACE_SIZE){
                changeSize = MAX_SPACE_SIZE;
            }else if(changeSize < MIN_SPACE_SIZE){
                changeSize = MIN_SPACE_SIZE;
            }

            spaceSize = changeSize;

            if(spaceSize == MAX_SPACE_SIZE){
                orcNum = 100;
            }else if(spaceSize == MIN_SPACE_SIZE){
                orcNum = 4;
            }else if((spaceSize * spaceSize) % 100 >= 50){
                orcNum = ((spaceSize * spaceSize) / 100) + 1;
            }else{
                orcNum = (spaceSize * spaceSize) / 100;
            }

            response.put("code","success");
            return response;
        }catch (NumberFormatException e){
            response.put("code","fail");
            response.put("msg","数字格式不正确");
            return response;
        }

    }

    //开始和重新开始游戏
    @RequestMapping("/start")
    @ResponseBody
    public String start(){

        histories.clear();

        round = 1;
        checkerboard = new Checkerboard(spaceSize,orcNum);

        addHistory();

        return "success";
    }

    //计算下一轮
    @RequestMapping("/nextRound")
    @ResponseBody
    public JSONObject nextRound(){

        JSONObject response = new JSONObject();
        if(!checkerboard.isGameover()){
            checkerboard.judgeStage();
            checkerboard.settleStage();

            round++;

            addHistory();
        }

        return response;

    }

    //获取棋盘数据
    @RequestMapping("/getGameDatas")
    @ResponseBody
    public JSONObject getGameDatas(@RequestBody JSONObject request){

        String dataKind = request.getString("dataKind");

        JSONObject response = new JSONObject();
        if("CUR".equals(dataKind)){
            response.put("spaceSize",spaceSize);
            response.put("orcNum",checkerboard.getCurOrcNum());
            response.put("isGameOver",checkerboard.isGameover());
            response.put("round",round);
            response.put("orcVos",checkerboard.getOrcVos());
            response.put("settleInfos",checkerboard.getSettleInfos());
        }else if("HISTORY".equals(dataKind)){
            int hisRound = request.getInteger("hisRound");
            if(hisRound < 1){
                hisRound = 1;
            }else if(hisRound > round){
                hisRound = round;
            }
            Checkerboard hisCheckboard = histories.get(hisRound);

            response.put("spaceSize",spaceSize);
            response.put("orcNum",hisCheckboard.getCurOrcNum());
            response.put("isGameOver",hisCheckboard.isGameover());
            response.put("round",hisRound);
            response.put("orcVos",hisCheckboard.getOrcVos());
            response.put("settleInfos",hisCheckboard.getSettleInfos());
        }

        return response;

    }

    //将上轮棋盘数据保存
    private void addHistory(){

        Checkerboard history = new Checkerboard();
        history.copy(checkerboard);

        histories.put(round,history);

    }

}
