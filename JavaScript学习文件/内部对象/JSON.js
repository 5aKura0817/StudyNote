var user = {
    name:"官宇辰",
    age:20,
    sex:"男"
}

// 对象转化为json字符串
var user_json = JSON.stringify(user);

//json字符串 转化为 对象
var sakura = JSON.parse('{"name":"官宇辰","age":19,"sex":"男"}');