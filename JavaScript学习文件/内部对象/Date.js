"use strict";
var now = new Date();//Thu Mar 05 2020 17:15:32 GMT+0800 (中国标准时间)
now.getFullYear();//年:2020
now.getMonth();//月:2 (0~11);
now.getDate();//日:5
now.getDay();//星期:4
now.getHours();
now.getMilliseconds();
now.getSeconds();

now.toLocaleString();//转为本地时间

var now2 = now.getTime()//获取时间戳;
var time = new Date(now2);//时间戳转当前时间
alert(time);