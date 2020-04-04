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

    public static final int SPACE_SIZE = 17;

    public static final int ORC_NUM = 4;

    private int round;

    private Checkerboard checkerboard;

    private Map<Integer,Checkerboard> histories = new HashMap<>();

    //简陋版页面
    @RequestMapping("/index")
    public String simplePage(){
        return "simple";
    }

    //开始和重新开始游戏
    @RequestMapping("/start")
    @ResponseBody
    public String start(){

        histories.clear();

        round = 1;
        checkerboard = new Checkerboard(SPACE_SIZE,ORC_NUM);

        addHistory();

        return "game";
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
            response.put("spaceSize",SPACE_SIZE);
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

            response.put("spaceSize",SPACE_SIZE);
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
