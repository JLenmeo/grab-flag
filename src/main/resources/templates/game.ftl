<!DOCTYPE html>
<html>
<head>
    <#include "commons/header.ftl">
    <link rel="stylesheet" href="/custom/css/game.css"/>
</head>
<body>
<#include "commons/gamebox-header.ftl">
    <#--地图显示区域-->
    <div class="layui-col-lg1 map-display-block"></div>
    <#--信息区域-->
    <div class="layui-col-lg1 info-block">
        <div class="game-info-block">
            <#--回合显示区域-->
            <div class="round-block">
                <span style="color: #FFFFFF;font-size: 30px;font-weight: bolder;font-family: STCaiyun;"></span>
            </div>
            <#--兽人数量显示区域-->
            <div class="orcNum-block">
                <div class="layui-col-lg1 orcNum-pic-block">
                    <img src="/pic/orc.jpg" height="100%" width="100%">
                </div>
                <div class="layui-col-lg1 orcNum-icon-block">
                    <i class="layui-icon layui-icon-close" style="color: #FFFFFF;font-size: 40px;font-weight: bolder;"></i>
                </div>
                <div class="layui-col-lg1 orcNum-num-block">
                    <span style="color: #FFFFFF;font-size: 30px;font-weight: bolder;font-family: STCaiyun;"></span>
                </div>
            </div>
        </div>
        <#--回合结算信息区域-->
        <div class="settle-block"></div>
    </div>
<#include "commons/gamebox-footer.ftl">
<#include "commons/footer.ftl">
<script src="/custom/js/game.js"></script>
</body>
</html>