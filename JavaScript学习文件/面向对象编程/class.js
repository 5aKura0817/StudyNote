class person{
    // 构造器
    constructor(name, age, sex){
        this.name = name;
        this.age = 4;
        this.sex = sex;
    }
    // 方法
    sayHello(){
        alert(`hello I'm ${this.name}`);
    }
}

class student extends person{
    constructor(name,grade){
        super(name);
        this.grade = grade;
    }
    sayHello(){
        alert(`I'm a student My name is ${this.name}`);
    }
}

var sakura = new student("官宇辰",1);
student.prototype.bitrh = 2000;
console.log(sakura.bitrh) // 2000
