layui.use('layer', function(){
    var layer = layui.layer;

    //启动开关点击事件
    $("#boot-button").click(function () {
        var status = $(this).attr("status");

        var gameboxWP = $("#gamebox-welcome-pic");
        var orcgamePP = $("#orcgame-play-pic");
        if(status == "close"){
            //如果开关处于关闭状态
            gameboxWP.css({"display":"block","animation":"gamebox-welcome-pic-open-animation 2s"});
            gameboxWP.on("animationend",function () {
                //gameboxWP动画完成的回调函数
                gameboxWP.css("display","none");
                orcgamePP.css({"display":"block","animation":"orcgame-play-pic-open-animation 1s"});
                gameboxWP.off("animationend");
            });

            $(this).attr("status","open");
        }else{
            //如果开关处于打开状态
            orcgamePP.css("animation","orcgame-play-pic-close-animation 1s");
            orcgamePP.on("animationend",function () {
                orcgamePP.css("display","none");
                orcgamePP.off("animationend");
            });

            $(this).attr("status","close");
        }
    });

    //开始游戏点击事件
    $(".orcgame-play-word").click(function () {
        location.href = "http://localhost:8080/start";
    });
});