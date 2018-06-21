/**
 * @author yyr
 */

/**
 * 窗口初始化时加载
 */
window.onload =function () {
    loadUserCredits();
}

var userphone = getUserPhone();
var projectData = getProjectData();
var username = getUserName();

/**
 * header
 * @type {Vue}
 */
var header = new Vue({
    el:"#header",
    data:{
        username:"",
        userphone:"",
        credits:0
    },
    created:function () {
        this.username = getUserName();
        this.userphone = userphone;
        this.credits = getUserCredits();
    }
});

/**
 * 网站数据
 * @type {Vue}
 */
var projectDataVue = new Vue({
    el:"#projectData",
    data:{
        wholeProjectNum:0,
        doingProjectNum:0,
        finishedProjectNum:0
    },
    created:function () {
        //加载项目数据
        this.wholeProjectNum = projectData.all_total;
        this.doingProjectNum = projectData.all_running;
        this.finishedProjectNum = projectData.all_closed;
    }
});

/**
 * 获取当前用户的任务数据
 * @returns JSON
 */
function getProjectData() {
    var result = "";
    $.ajax({
        url:'/get3type',
        type:'post',
        async:false,
        data:{
            phonenum:userphone
        },
        success: function (data) {
            console.log(data);
            result = JSON.parse(data);
        },
        error: function () {
            alert("fail");
        }
    })
    return result;
}