function abs(x) {
    if (x > 0) {
        return x;
    } else {
        return -x;
    }
}

var abs2 = function (x) {
    if (typeof x !== "number") {
        throw "Not a Number";
    }
    console.log(`argument:${arguments}`);
    
    if (x > 0) {
        return x;
    } else {
        return -x;
    }
};

var abs3 = function (x,...rest) {
    if (typeof x !== "number") {
        throw "Not a Number";
    }
    console.log(`rest:${rest}`);
    console.log(rest);
    
    if (x > 0) {
        return x;
    } else {
        return -x;
    }
};

// 自定义用户空间
var sakura = {};
// 添加用户自己的变量
sakura.x = 817;
sakura.add = function (x, y){
    return x + y;
}

var x = 5;

// function f(){
//     for(var i = 0; i < 100; i++){
//         console.log(i); //0,1,...,99
//     }
//     console.log(i);// 100!?  i出了作用域还可以使用
// }

function f(){
    for(let i = 0; i < 100; i++){
        console.log(i); //0,1,...,99
    }
    console.log(i);// 100!?  i出了作用域还可以使用
}

// 对象
var person = {
    name : "SAKURA",
    birth : 2000,
    // 方法
    age : function(){
        return new Date().getFullYear() - this.birth;
    }
} 