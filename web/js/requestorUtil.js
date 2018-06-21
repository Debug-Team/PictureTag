/**
 * @author yyr
 * 上传者的通用方法
 */

/**
 * 获得上传者用户信息
 * @returns JSON
 */
function getUserInfo() {
    var phone = getUserPhone();
    var res = "";
    $.ajax({
        url:'/getCredits',
        type:'post',
        async:false,
        data:{
            phonenum:phone
        },
        success: function (data) {
            console.log(data);
            res = JSON.parse(data);
            // credits = temp.credits;
        },
        error: function () {
            alert("fail");
        }
    })
    return res;
}

/**
 * 加载用户积分
 */
function loadUserCredits() {
    var a_credits = document.getElementById("credits");
    var credits = getUserInfo().credits;
    a_credits.innerText = "积分:"+credits;
}

/**
 * 获取用户积分
 * @returns {number|*}
 */
function getUserCredits() {
    var credits = getUserInfo().credits;
    return credits;
}
/**
 * 获取用户名
 * @returns username
 */
function getUserName() {
    var username = getUserInfo().username;
    return username;
}
