var p1 = document.getElementById("p1");
var h1 = document.getElementsByTagName('h1');
var p2 = document.getElementsByClassName('paragraph');
var father = document.getElementById("father");
var pp = document.getElementById("pp");
var body = document.getElementsByTagName('body');

for (let i = 0; i < 6; i++) {
    let elememt = document.createElement('h1');
    elememt.innerText = "Hello World";
    father.appendChild(elememt);
}