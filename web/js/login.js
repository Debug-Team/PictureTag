window.onload = function () {
    if(window.location.href.indexOf("?=logout") >= 0){
        window.location.href = "/";
        logout();
    }
    else{
        checkCookie();      //检查Cookie
    }
    // checkCookie();      //检查Cookie
}

/**
 * 登陆验证
 * @param userphone 手机号
 * @param password 密码
 * @return json状态信息
 */
function login(userphone,password) {
    var res = null;
    $.ajax({
        url: '/login',
        type: 'post',
        async: false,
        data: {
            userphone:userphone,
            password:password
        },
        success: function (data) {
            res = JSON.parse(data)
        },
        error: function() {
            alert('fail')
        }
    })
    return res
}

/**
 * 注册
 * @param username 用户名
 * @param password 密码
 * @param phone 手机号
 * @param type 用户类型 1为上传者，0为标记者
 * @return json状态信息
 */
function signup(username,password,phone,type) {
    var res = null;
    $.ajax({
        url: '/signup',
        type: 'post',
        async: false,
        data: {
            username:username,
            password:password,
            phone:phone,
            type:type
        },
        success: function (data) {
            res = JSON.parse(data)
        },
        error: function() {
            alert('fail')
        }
    })
    return res
}

$(".submit").submit = clickLogin;

/**
 * 点击登录按钮响应
 */
function clickLogin() {
    var userPhone_login = document.getElementById("userPhone_login");
    var password_login = document.getElementById("password_login");

    // alert(userPhone_login.value + " "+password_login.value);
    var userPhone = userPhone_login.value;
    var password = password_login.value;
    var result = login(userPhone, password);
    var state = result.state;
    // alert(type);
    // alert("state "+state);
    if(state == "1"){
        //cookie
        // document.cookie = "userphone="+userPhone;
        // setCookie("userphone", userPhone);
        // alert("cnm")
        var type = result.usertype;

        //设置Cookie
        setCookie("userphone", userPhone);
        setCookie("usertype", type);

        var destination = "";
        if(type == "0"){
            destination = "workerHome.html"
        }
        else if(type == "1"){
            destination = "requestorHome.html"
        }
        else if(type == "-1"){
            destination = "adminHome.html"
        }
        // var href = window.location.href;
        // href = href.substring(0, href.indexOf("#"));
        // // window.location.href = href + "html/" + destination + "?userphone=" + userPhone + "/";
        // window.location.href = href + "html/" + destination;
        window.location.href = "html/" + destination;
    }
    else{
        alert(result.retMessage);
    }
    // alert("clicklogin"+" "+result);
}

/**
 * 点击注册按钮响应
 */
function clickSignUp() {
    var name_signUp = document.getElementById("name_signUp");
    var userPhone_signUp = document.getElementById("userPhone_signUp");
    var password_signUp = document.getElementById("password_signUp");
    var type_signUp = document.getElementById("type_signUp");

    var name = name_signUp.value;
    var userPhone = userPhone_signUp.value;
    var password = password_signUp.value;
    var type = type_signUp.value;

    var result = signup(name, password, userPhone, type);

    var state = result.state;
    if(state == "1"){
        // setCookie("userphone", userPhone);
        //设置Cookie
        setCookie("userphone", userPhone);
        setCookie("usertype", type);

        var destination = "";
        if(type == 0){
            destination = "workerHome.html"
        }
        else if(type == 1){
            destination = "requestorHome.html"
        }
        // var href = window.location.href;
        // href = href.substring(0, href.indexOf("#"));
        // window.location.href = href + "html/" + destination;
        window.location.href = "html/" + destination;

        alert("注册成功");
    }
    else if(state == "-1"){
        alert("手机号已注册");
    }

}

/**
 * 屏蔽form提交
 */
$('#login_form').on("submit", function (ev) {
    // clickLogin();
    ev.preventDefault();
    clickLogin();
});
$('#signup_form').on("submit", function (ev) {
    // clickLogin();
    ev.preventDefault();
    clickSignUp();
});

// var signup_form = new Vue({
//     el:'#signup_form',
//     data:{
//         selectedType:""
//     }
// })
