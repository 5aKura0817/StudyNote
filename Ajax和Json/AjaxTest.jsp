<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>AjaxTest</title>
    <script src="${pageContext.request.contextPath}/static/jquery-3.4.1.js"></script>
    <script>
        function a1() {
            // 所有参数
            // url:待载入的页面的地址
            // data:待发送的key:value 参数
            // success:载入成功的回调函数
                // data: 封装了服务器返回的数据
                // status: 状态

            $.post({
                url:"${pageContext.request.contextPath}/ajaxtest",
                data:{"username":$("#username").val()},
                success:function (data) {
                    if (data.toString()=="OK"){
                        $("#msg").css("color","yellowgreen");
                    } else {
                        $("#msg").css("color","red");
                    }
                    $("#msg").html(data);
                }
            })
        }
    </script>
</head>
<body>
    <%--失去焦点时执行a1()--%>
    <input type="text" id="username" onblur="a1()">
    <%--用于动态插入数据--%>
    <span id="msg" ></span>
</body>
</html>
