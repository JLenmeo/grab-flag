layui.use(["element","layer"], function(){
    var element = layui.element;
    var layer = layui.layer;

    $(function () {
        $.ajax({
            url:"http://localhost:8080/simple/start",
            success:function (response) {
                var request = {dataKind:"CUR"};
                showGame(request);
            }
        });
    });

    //下一轮点击事件
    $("#nextRound").click(function () {
        $.ajax({
            url:"http://localhost:8080/simple/nextRound",
            type:"post",
            dataType:"json",
            success:function (response) {
                var request = {dataKind:"CUR"};
                showGame(request);
            }
        });
    });

    //上翻历史点击事件
    $("#historyUp").click(function () {
        var request = {dataKind:"HISTORY"}
        var round = $("#roundCount").text().split(" ")[1];
        request.hisRound = parseInt(round) - 1;

        showGame(request);
    });


    //下翻历史点击事件
    $("#historyDown").click(function () {
        var request = {dataKind:"HISTORY"}
        var round = $("#roundCount").text().split(" ")[1];
        request.hisRound = parseInt(round) + 1;

        showGame(request);
    });

    //重新开始点击事件
    $("#restart").click(function () {
        $.ajax({
            url:"http://localhost:8080/simple/start",
            success:function (response) {
                var request = {dataKind:"CUR"};
                showGame(request);
            }
        });
    });

    //游戏结束处理
    function gameover() {
        layer.msg("Game Over",{
            offset:"220px",
            shade:0.3,
            shadeClose:true,
            time:0
        });
    }

    //处理后台返回的数据，更新地图
    function showGame(request) {
        $.ajax({
            url:"http://localhost:8080/simple/getGameDatas",
            type:"post",
            contentType:"application/json",
            data:JSON.stringify(request),
            dataType:"json",
            success:function (response) {
                var spaceSize = response.spaceSize;
                var orcNum = response.orcNum;
                var isGameOver = response.isGameOver;
                var round = response.round;
                var orcVos = response.orcVos;
                var settleInfos = response.settleInfos;

                //初始化一个spaceSize+2 x spaceSize+2 大小的二维数组
                var mapLength = spaceSize + 2;
                var map = new Array();
                for(var i = 0;i < mapLength;i++){
                    map[i] = new Array();

                    for(var k = 0;k < mapLength;k++){
                        var unit = {
                            isBorder:false,
                            noOne:true
                        };

                        if(i == 0 || k == 0 || i == mapLength - 1 || k == mapLength - 1){
                            unit.isBorder = true;
                        }

                        map[i][k] = unit;
                    }
                }

                //将兽人放入二维数组中
                for(var i = 0;i < orcVos.length;i++){
                    var orcVo = orcVos[i];

                    var x;
                    var y;
                    if(orcVo.transform){
                        x = orcVo.x + 1;
                        y = orcVo.y + 1;
                    }
                    var unit = map[x][y];
                    unit.num = orcVo.num;
                    unit.value = orcVo.value;
                    unit.haveFlag = orcVo.haveFlag;
                    unit.noOne = false;
                }

                //绘制地图
                var mapBlock = $(".map-display-block");
                mapBlock.empty();
                for(var i = 0;i < map.length;i++){
                    mapBlock.append("<div class='checkerboard-row'></div>");
                    var rows = mapBlock.children(".checkerboard-row");
                    var targetRows = $(rows[i]);
                    for(var k = 0;k < map[i].length;k++){
                        var unit = map[i][k];

                        if(unit.isBorder){
                            if(unit.noOne){
                                targetRows.append("<div class='wall-unit layui-col-lg1'></div>")
                            }else{
                                targetRows.append("<div class='wall-unit layui-col-lg1'>" +
                                    "<span class='unit-icon'>" + unit.num + "</span>" +
                                    "<div class='layui-progress unit-blood-bar' lay-filter='" + unit.num + "'>" +
                                    "<div class='layui-progress-bar unit-blood-bar' lay-percent='" + unit.value + "'></div>" +
                                    "</div>" +
                                    "<span class='my-progress-text'>" + unit.value + "</span>" +
                                    "</div>");
                            }
                        }else{
                            if(unit.noOne){
                                targetRows.append("<div class='normal-unit layui-col-lg1'></div>")
                            }else{
                                if(unit.haveFlag){
                                    targetRows.append("<div class='flag-unit layui-col-lg1'>" +
                                        "<span class='unit-icon'>" + unit.num + "</span>" +
                                        "<div class='layui-progress unit-blood-bar' lay-filter='" + unit.num + "'>" +
                                        "<div class='layui-progress-bar unit-blood-bar' lay-percent='" + unit.value + "'></div>" +
                                        "</div>" +
                                        "<span class='my-progress-text'>" + unit.value + "</span>" +
                                        "</div>");
                                }else{
                                    targetRows.append("<div class='normal-unit layui-col-lg1'>" +
                                        "<span class='unit-icon'>" + unit.num + "</span>" +
                                        "<div class='layui-progress unit-blood-bar' lay-filter='" + unit.num + "'>" +
                                        "<div class='layui-progress-bar unit-blood-bar' lay-percent='" + unit.value + "'></div>" +
                                        "</div>" +
                                        "<span class='my-progress-text'>" + unit.value + "</span>" +
                                        "</div>");
                                }
                            }
                        }

                        element.progress(unit.num, String(unit.value) + "%");
                    }
                }

                //绘制回合结算信息
                var infoBlock = $(".info-block");
                infoBlock.empty();
                infoBlock.append("<div id='roundCount' class='settle-info'>Round " + round + "</div>");
                infoBlock.append("<div class='settle-info'>兽人数量 X " + orcNum + "</div>");
                infoBlock.append("<div style='height: 15px;border-bottom: 1px solid #FFFFFF;'></div>");
                for(var i = 0;i < settleInfos.length;i++){
                    infoBlock.append("<div class='settle-info'>" + settleInfos[i] + "</div>");
                }

                //游戏结束处理
                if(isGameOver){
                    gameover();
                }
            }
        });
    }
});