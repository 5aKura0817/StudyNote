"use strict";
console.log("a");
console.log("a");

// 模板字符串
let name = "官宇辰";
let age = 20;

console.log(`你好，我是${name},我今年${age}岁`);

var person = {
  name: "官宇辰",
  age: 19,
  sex: "男"
};

let arr = ["aaa", "bbb", 1, 2, 3, null];
arr.name = "223";

// forEach
arr.forEach(function(temp) {
  console.log(temp);
});

//for-of
for(let element of arr){
    console.log(element);
}

// for-in
for(let index in arr){
  console.log(arr[index]);
}

var students = new Map([
  ["tom", 98],
  ["Jerry", 94],
  ["Jack", 84],
  ['Bob',89]
]);

for(let x of students){
  console.log(x);
}

let set = new Set([1,2,2,3,3]);

for(let x of set){
  console.log(x);
}