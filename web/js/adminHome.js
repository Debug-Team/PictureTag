var userphone = getUserPhone();

var userData = getUserData();
var projectData = getProjectData();


/**
 * header
 * @type {Vue}
 */
var header = new Vue({
    el:"#header",
    data:{
        username:"",
    },
    created:function () {
        this.username = userphone;
    }
});

/**
 * 网站数据
 * @type {Vue}
 */
var siteData = new Vue({
    el:"#siteData",
    data:{
        wholeUserNum:0,
        workerNum:0,
        requestorNum:0,
        wholeProjectNum:0,
        doingProjectNum:0,
        finishedProjectNum:0
    },
    created:function () {
        //加载用户数据
        // var userData = getUserData();
        this.wholeUserNum = userData.totalNum;
        this.workerNum = userData.markerNum;
        this.requestorNum = userData.uploaderNum;

        //加载项目数据
        // var projectData = getProjectData();
        this.wholeProjectNum = projectData.all_total;
        this.doingProjectNum = projectData.all_running;
        this.finishedProjectNum = projectData.all_closed;
    }
});

/**
 * 获取网站的用户数据
 * @returns JSON {"markerNum":标记者人数 , "uploaderNum":上传者人数 , "totalNum":总人数}
 */
function getUserData() {
    var result = "";
    $.ajax({
        url:'/peopleCount',
        type:'post',
        async:false,
        data:{},
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

/**
 * 管理员查看所有项目统计信息
 * @return all_total 全部项目 all_closed 全部完成 all_running 全部进行中
 *          前缀1_ 2_ 3_ 分别对应1-整体标注 2-标框标注 3-标框标注
 */
function getProjectData() {
    var result = "";
    $.ajax({
        url:'/projectCount',
        type:'post',
        async:false,
        data:{},
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

/**
 * 管理员 用户数和标记张数统计
 * @return
 */
function getUserNumCrossTagNums(){
    var result = "";
    $.ajax({
        url:'/userNumCrossTagNums',
        type:'post',
        async:false,
        data:{},
        success: function (data) {
            console.log(data);
            // result = JSON.parse(data);

            var temp = data.split("-;-");
            result = [];
            for(var i = 0;i < temp.length;i++){
                result.push(JSON.parse(temp[i]));
            }
        },
        error: function () {
            alert("fail");
        }
    })
    return result;
}

/**
 * 标注张数和标注者数的统计图的vue
 */
var tagAndWorkerNum_vue = new Vue({
    el: '#tagAndWorkerNum_vue',
    data: {
        selectedType:'0'
    },
    methods: {
        changeType:function () {
            var type = this.selectedType;
            console.log(type)
            // initTagAndWorkerNumChart(type);
            setTagAndWorkerNumChart(type);
        }
    }
});