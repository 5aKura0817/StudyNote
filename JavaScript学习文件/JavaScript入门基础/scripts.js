'use strict';
alert("hello world");

// 定义变量 只有一个类型 var 
let num = 84;
console.log(num);
let string = "5akura";

// 流程控制
if (num > 60 && num < 70){
    alert("及格");
}else if (num>= 70 && num< 90) {
    alert("良好")
}else {
    alert("优秀")
}
// 数组
var arr = [1,2,3,'官宇辰',true,null];

// 浏览器控制台输出 相当于java sout
console.log(num);

// 对象创建
let person = {
    name:"官宇辰",
    age:20,
    sex:"男"
}
alert(person.name);