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
            // alert("fail");
            window.location.href = "/";
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

/**
 * 购买积分
 */
function buyCredit() {
    var overlay_credit = document.createElement("div");
    overlay_credit.id = "overlay_credit";
    overlay_credit.className = "overlay_credit";
    document.body.appendChild(overlay_credit);

    var container_credit = document.createElement("div");
    container_credit.id = "container_credit";
    container_credit.className = "container_credit";
    document.body.appendChild(container_credit);

    document.getElementById("container_credit").innerHTML = "<form class=\"credit_form\">\n" +
        "            <img src=\"../image/close.gif\" class=\"closeImg\" onclick=\"closeForm()\">\n" +
        "            <h2>积分充值</h2>\n" +
        "            <label>充值金额：</label>\n" +
        "            <input id=\"num_buyCredit\" type=\"number\" min=\"1\" class=\"inputArea\" required/>\n" +
        "            <div>\n" +
        "                <button type=\"button\" onclick=\"closeForm()\">取消</button>\n" +
        "                <button type=\"submit\">充值</button>\n" +
        "            </div>\n" +
        "        </form>\n"

    /**
     * 屏蔽form提交
     */
    $('.credit_form').on("submit", function (ev) {
        // clickLogin();
        ev.preventDefault();
        submitBuyCredit();
    });
}

/**
 * 关闭购买积分窗口
 */
function closeForm() {
    var overlay_credit = document.getElementById("overlay_credit");
    var container_credit = document.getElementById("container_credit");
    overlay_credit.remove();
    container_credit.remove();
}

/**
 * 提交购买积分
 */
function submitBuyCredit() {
    var num = document.getElementById("num_buyCredit").value;
    // console.log(num);
    var userphone = getUserPhone();

    $.ajax({
        url:"/uploaderRecharge",
        type:'post',
        async:false,
        data:{
            userphone:userphone,
            amounts:num
        },
        success: function (data) {
            console.log(data);
            var res = JSON.parse(data);
            // credits = temp.credits;
            alert(res.retMessage);
        },
        error: function () {
            alert("充值失败");
        }
    });

    closeForm();        //关闭表格
    window.location.reload();
}
