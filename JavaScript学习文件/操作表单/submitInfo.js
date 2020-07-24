function VerifyInfo(){
    var uname = document.getElementById("username");
    var pwd = document.getElementById("input-password");
    var md5pwd = document.getElementById("md5-password");
    md5pwd.value = hex_md5(pwd.value);

    if(name.value===""||pwd.value===""){
        alert("用户名和密码不能为空");
        return false;
    }
    return true;
}

function submitInfo(){
    alert("提交成功 ，等待验证");
}