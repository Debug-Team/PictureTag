/**
 * 获取当前的用户号码
 */
function getUserPhone() {
    // var href = window.location.href;
    // var phone = href.substring(href.indexOf("?userphone="));
    // if(phone.indexOf("/") != -1){
    //     phone = phone.substring(0, phone.indexOf("/"));
    // }
    // var phone = "";
    // var cookieData = document.cookie.split(";");
    // for(var i = 0;i < cookieData.length;i++){
    //     var temp = cookieData[i].trim();
    //     if(temp.indexOf("userphone=") == 0){
    //         phone = temp.substring(10, temp.length);
    //     }
    // }
    //
    // return phone;
    return getCookie("userphone");
}

/**
 * 获取当前用户的类型
 */
function getUsertype() {
    return getCookie("usertype");
}

/**
 * 获取cookie
 * @param type 想要获取的cookie
 * @returns {string} 结果
 */
function getCookie(type) {
    var res = "";
    type += "=";
    var cookieData = document.cookie.split(";");
    for(var i = 0;i < cookieData.length;i++){
        var temp = cookieData[i].trim();
        if(temp.indexOf(type) == 0){
            res = temp.substring(type.length, temp.length);
        }
    }

    return res;
}
/**
 * 设置cookie
 * @param type
 * @param value
 */
function setCookie(type, value){
    var expires = new Date();
    expires.setMinutes(expires.getMinutes()+60);
    // console.log(expires.toUTCString());
    document.cookie = type + "=" + value + "; expires=" + expires.toUTCString();
    // var cookie = document.cookie;
    // alert("here")
    // alert(cookie.indexOf(type))
    // if(cookie.indexOf(type) >= 0){
    //     //去除type之前
    //     var temp = cookie.substring(cookie.indexOf(type), cookie.length);
    //     //截取type
    //     var phone = temp.substring(0,temp.indexOf(";"));
    //
    //     cookie = cookie.replace(phone, type+"="+value);
    //     alert(phone + " " + cookie)
    // }
    // else{
    //     cookie += type + "=" + value + ';'
    // }
    // document.cookie = cookie;
    // document.cookie += type+"="+value;
    console.log(document.cookie);
}

/**
 * logout
 */
function logout() {
    document.cookie = "userphone=; expires=Thu, 01 Jan 1970 00:00:00 GMT";
    document.cookie = "usertype=; expires=Thu, 01 Jan 1970 00:00:00 GMT";
    // console.log(getUserPhone());
    console.log("phone:"+getCookie("userphone"))
    console.log("type:"+getCookie("usertype"))
    console.log(document.cookie)
}

/**
 * 检查cookie验证登录
 */
function checkCookie() {
    console.log(document.cookie)
    console.log("phone:"+getUserPhone())
    console.log("type:"+getUsertype())
    if(getUserPhone() != ""){
        var usertype = getUsertype();

        var destination = "";

        if(usertype == "0"){
            console.log("标注者")
            destination = "workerHome.html";
        }
        else if(usertype == "1"){
            console.log("上传者")
            destination = "requestorHome.html";
        }
        else if(usertype == "-1"){
            console.log("管理员")
            destination = "adminHome.html";
        }
        // var href = window.location.href;
        // href = href.substring(0, href.indexOf("#"));
        // window.location.href = href + "html/" + destination;
        window.location.href = "html/" + destination;
    }
}

/**
 * 获取所有分类
 * @returns {Array<string>}
 */
function getCategoriesList() {
    var res = "";
    $.ajax({
        url:'/getClassification',
        type:'post',
        async:false,
        data:{},
        success: function (data) {
            console.log(data);
            res = JSON.parse(data);
            // credits = temp.credits;
        },
        error: function () {
            alert("fail");
        }
    })
    return res.categories;
}
