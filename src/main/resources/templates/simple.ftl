<!DOCTYPE html>
<html>
<head>
    <#include "commons/header.ftl">
    <link rel="stylesheet" href="/custom/css/simple.css"/>
</head>
<body>
    <div class="layui-col-lg1 map-display-block"></div>
    <div class="layui-col-lg1 info-block"></div>
    <div class="layui-col-lg1 controller-block">
        <div class="settle-info">修改地图大小</div>
        <div class="change-spacesize-block">
            <input type="text" name="spaceSize" placeholder="地图大小" class="layui-input" style="display: inline-block;width: 50%;margin-right: 5px;">
            <button id="changeSpaceSize" class="layui-btn layui-btn-radius layui-btn-danger">修改</button>
        </div>
        <div style='height: 15px;border-bottom: 1px solid #FFFFFF;'></div>
        <div class="button-unit">
            <button id="nextRound" class="layui-btn layui-btn-radius layui-btn-danger">下一轮</button>
        </div>
        <div class="button-unit">
            <button id="historyUp" class="layui-btn layui-btn-radius layui-btn-danger">上翻历史</button>
        </div>
        <div class="button-unit">
            <button id="historyDown" class="layui-btn layui-btn-radius layui-btn-danger">下翻历史</button>
        </div>
        <div class="button-unit">
            <button id="restart" class="layui-btn layui-btn-radius layui-btn-danger">重新开始</button>
        </div>
    </div>
<#include "commons/footer.ftl">
<script src="/custom/js/simple.js"></script>
</body>
</html>