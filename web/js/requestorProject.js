window.onload = function () {
    loadUserCredits();
}

var searchBox = new Vue({
    el: '#searchBox',
    data: {
        searchKey : ""
    },
    watch: {
        searchKey: function (oldKey, newKey) {
            // console.log("here");
            project_list.searchKey = this.searchKey;
        }
    }
});

var project_list = new Vue({
    el: '#project_list',
    data: {
        projectList: [],
        searchKey: "",
        filterList:[]
    },
    /**
     * vue创建时
     */
    created: function () {
        var list = getProjectList();
        for(var i = 0;i < list.length;i++){
            var item = list[i];
            var data = {};
            data.previewPic = "../pic/"+item.id+"/"+item.picList[0];
            data.id = item.id;
            data.name = item.name;
            data.rate = item.process/(item.picList.length*item.markedPersonNum);
            if(item.type == 1){
                data.type = "整体标注";
            }
            else if(item.type == 2){
                data.type = "标框标注";
            }
            else if(item.type == 3){
                data.type = "边界标注";
            }
            data.classificationList = item.classificationList;
            data.categories = item.classificationList.toString();
            data.award = item.award;
            data.workerNum = item.markedPersonNum;
            data.workers = item.markerList.toString();
            data.markerList = item.markerList;
            // data.startTime = item.id;
            data.description = item.description;
            data.startTime = formatDate(item.startTime);
            data.endTime = formatDate(item.endTime);

            data.isEnded = item.isEnded;
            data.isEvaluated = item.rated;
            if(item.isEnded){
                data.state = "已结束";
            }
            else{
                data.state = "进行中";
            }
            this.projectList.push(data);
        }
        // var data = {};
        // data.previewPic = "../image/photo-1.jpg";
        // data.id = "test";
        // data.rate = 0.36;
        // data.type = "整体标注";
        // data.award = 300;
        // data.workerNum = 3;
        // data.workers = "zyb,zzw";
        // data.startTime = "2018年4月20日";
        // data.description = "测试用假项目";
        // data.state = "进行中";
        // this.projectList.push(data);
        //
        // var data2 = {};
        // data2.previewPic = "../image/photo-1.jpg";
        // data2.id = "test2";
        // data2.rate = 0.48;
        // data2.type = "边界标注";
        // data2.award = 300;
        // data2.workerNum = 3;
        // data2.workers = "zyb,zzw";
        // data2.startTime = "2018年4月20日";
        // data2.description = "测试用假项目";
        // data2.state = "进行中";
        // this.projectList.push(data2);
    },
    /**
     * created之后，对每个项目绘制进度图，解析地址传参的分类
     */
    mounted: function () {
        //解析地址传参
        var href = document.location.href;
        if(href.indexOf("?categoryList=") > 0){
            var param = href.substr(href.indexOf("?categoryList=")+14);
            var params = param.split("&");
            // console.log(params);
            for(var i = 0; i < params.length; i++){     //解码URI
                params[i] = decodeURI(params[i]);
            }
            filter.selectedList = params;
        }

        for(var i = 0;i < this.projectList.length;i++){
            printRate(this.projectList[i]);
            // clipPreviewImg(this.projectList[i]);
        }
    },
    /**
     * 数据变动时，刷新绘制进度图
     */
    updated: function () {
        for(var i = 0;i < this.filterList.length;i++){
            printRate(this.filterList[i]);
        }
    },
    methods:{
        /**
         * 关闭项目
         * @param index
         */
        closeProject: function (index) {
            // console.log("index:"+index)
            var id = this.projectList[index].id;

            $.ajax({
                url:'/closeproject',
                type:'post',
                async:false,
                data:{
                    id:id
                },
                success: function (data) {
                    console.log(data);
                    var result = JSON.parse(data);
                    // alert(result.retMessage);
                    // window.location.reload();
                    project_list.$message.success("结束项目成功");
                    setTimeout("window.location.reload()", 1000);
                },
                error: function () {
                    // alert("fail");
                    project_list.$message.error("结束项目失败");
                }
            })
        },
        /**
         * 评价项目
         * @param index
         */
        evaluateProject: function (index) {
            console.log("1")
            $("#overlay").show();
            $("#evaluateForm").show("fast");
            // console.log(this.projectList[index])
            evaluateForm.setToEvaluateProject(this.projectList[index]);     //设置将评价的项目
        },
        /**
         * 查看项目评价
         * @param index
         */
        checkEvaluateProject: function (index) {
            console.log("2")
            $("#overlay").show();
            $("#checkEvaluateForm").show("fast");
            checkEvaluateForm.setToEvaluateProject(this.projectList[index]);        //设置将查看的项目
        }
    },
    computed:{
        filterProjectList: function () {
            // var key = this.searchKey;
            // var after = this.projectList.filter(function (item) {
            //     // console.log(item);
            //     // console.log("searchKey"+key)
            //     return item.id.indexOf(key) >= 0        //搜索id
            //         || item.name.indexOf(key) >= 0      //搜索name
            //         || item.categories.indexOf(key) >= 0     //搜索分类
            //         || item.type.indexOf(key) >= 0       //搜索类型
            //         || item.workers.indexOf(key) >= 0    //搜索参与者
            //         || item.description.indexOf(key) >= 0      //搜索描述
            //         || item.state.indexOf(key) >= 0     //搜索状态
            // });
            var key = this.searchKey.split(" ");

            //筛选
            var filterItem = filter.selectedList;
            // console.log(filterItem);
            for(var i = 0; i < filterItem.length; i++){
                key.push(filterItem[i]);
            }
            console.log(key)

            var after = [];
            for(var i = 0;i < key.length;i++){
                var curKey = key[i];
                if(i == 0 && key.length > 1){
                    continue;
                }
                if(i != 0 && (curKey == null || curKey == "")){     //跳过最后多一个空格的情况
                    continue;
                }
                after.push.apply(after, this.projectList.filter(function (item) {
                    // console.log(item);
                    // console.log("searchKey"+key)
                    return item.id.indexOf(curKey) >= 0        //搜索id
                        || item.name.indexOf(curKey) >= 0      //搜索name
                        || item.categories.indexOf(curKey) >= 0     //搜索分类
                        || item.type.indexOf(curKey) >= 0       //搜索类型
                        || item.workers.indexOf(curKey) >= 0    //搜索参与者
                        || item.description.indexOf(curKey) >= 0      //搜索描述
                        || item.state.indexOf(curKey) >= 0     //搜索状态
                }));
                after = Array.from(new Set(after));
                // after = this.projectList.filter(function (item) {
                //         // console.log(item);
                //         // console.log("searchKey"+key)
                //         return item.id.indexOf(curKey) >= 0        //搜索id
                //             || item.name.indexOf(curKey) >= 0      //搜索name
                //             || item.categories.indexOf(curKey) >= 0     //搜索分类
                //             || item.type.indexOf(curKey) >= 0       //搜索类型
                //             || item.workers.indexOf(curKey) >= 0    //搜索参与者
                //             || item.description.indexOf(curKey) >= 0      //搜索描述
                //             || item.state.indexOf(curKey) >= 0     //搜索状态
                //     });
            }

            this.filterList = after;        //记录，方便刷新列表

            //更改显示总数
            filter.totalSize = after.length;

            return after;
            // return this.projectList.filter(function (item) {
            //     // console.log(item);
            //     // console.log("searchKey"+key)
            //     return item.id.indexOf(key) >= 0        //搜索id
            //         || item.type.indexOf(key) >= 0       //搜索类型
            //         || item.workers.indexOf(key) >= 0    //搜索参与者
            //         || item.description.indexOf(key) >= 0      //搜索描述
            //         || item.state.indexOf(key) >= 0     //搜索状态

            // })
        }
    }
});

var evaluateForm = new Vue({
    el:"#evaluateForm",
    data:{
        toEvaluateProject: {},
        scoreList: []
    },
    methods:{
        /**
         * 设置将被评价的项目
         * @param project
         */
        setToEvaluateProject: function (project) {
            this.toEvaluateProject = project;
            //初始化scoreList
            this.scoreList = new Array(project.markerList.lenth);
        },
        /**
         * 检查并修正评分
         * @param index
         */
        checkScore: function (index) {
            console.log("here")
            if(this.scoreList[index] < 0){
                this.scoreList[index] = 0;
            }
            else if(this.scoreList[index] > 5){
                this.scoreList[index] = 5;
            }
            console.log(this.scoreList[index])
        },
        /**
         * 关闭评价表
         */
        closeForm: function () {
            $(".evaluateForm").hide();
            $(".overlay").hide();
            //清空要评价的项目
            this.toEvaluateProject = {};
            this.scoreList = [];
            window.location.reload();
        },
        /**
         * 提交评价表
         */
        submitForm: function () {
            var id = this.toEvaluateProject.id;
            var scoreList = this.scoreList;
            var formData = new FormData();
            formData.append("id", id);
            formData.append("scoreList", scoreList);

            $.ajax({
                url:'/ratingProject',
                type:'post',
                async:false,
                data: formData,
                processData: false,
                contentType: false,
                enctype:'multipart/form-data',
                success: function (data) {
                    console.log(data);
                    var result = JSON.parse(data);
                    // alert(result.retMessage);
                    evaluateForm.$message.success('提交评价成功');
                    evaluateForm.closeForm();
                },
                error: function () {
                    // alert("fail");
                    evaluateForm.$message.error('提交评价失败')
                }
            })
        }
    }
});

/**
 * 查看评价
 */
var checkEvaluateForm = new Vue({
    el: "#checkEvaluateForm",
    data: {
        toEvaluateProject: {},
        scoreList: []
    },
    methods:{
        /**
         * 设置将查看评价的项目
         * @param project
         */
        setToEvaluateProject: function (project) {
            this.toEvaluateProject = project;
            //初始化scoreList
            this.scoreList = getProjectRate(project.id);
        },
        /**
         * 关闭评价表
         */
        closeForm: function () {
            $("#checkEvaluateForm").hide();
            $("#overlay").hide();
            //清空要评价的项目
            this.toEvaluateProject = {};
            this.scoreList = [];
            window.location.reload();
        }
    }
});

/**
 * @return 装有project的对象数组
 */
function getProjectList() {
    var phone = getUserPhone();
    // console.log(phone);
    var result = [];
    $.ajax({
        url:'/previewproject',
        type:'post',
        async: false,
        data:{
            phonenum:phone
        },
        success: function (data) {
            console.log(data);
            var temp = data.split("-;-");
            for(var i = 0;i < temp.length;i++){
                if(temp == ""){
                    break;
                }
                result.push(JSON.parse(temp[i]));
            }
        },
        error: function () {
            // alert("fail");
            project_list.$message.error("获取项目列表失败");
        }
    })
    return result;
}

/**
 * 获取项目的评价
 * @param projectId
 * @returns {Array} 评分数组
 */
function getProjectRate(projectId) {
    var result = [];
    $.ajax({
        url:'/getProjectRate',
        type:'post',
        async: false,
        data:{
            id:projectId
        },
        success: function (data) {
            console.log(data);
            var json = JSON.parse(data);
            result = json.scorelist;
        },
        error: function () {
            // alert("获取项目评价失败");
            evaluateForm.$message.error("获取项目评价失败");
        }
    })
    return result;
}

/**
 * 格式化时间
 * @param time {string}
 * @returns {string}
 */
function formatDate(time){
    if(time == null || time == ""){
        return "";
    }
    var res = time.substr(0, 4) + "年" + time.substr(4,2) + "月" + time.substr(6,2) + "日 " + time.substr(8,2) + ":" + time.substr(10,2);
    return res;
}

/**
 * 裁剪图片
 */
function clipPreviewImg(data) {
    var img = document.getElementById("img"+data.id);
    var width = $(document.dt).width();
    var picWidth = $(".img"+data.id).width();
    var dif = -1*(picWidth-width)/2

    console.log("width:"+width +"picWidth:"+picWidth+ "  dif:" + dif);
    $(".img"+data.id).css("top",dif);
    $(".img"+data.id).css("width",width);
}

/**
 * 绘制项目的进度百分比
 * @param data 该项目的data项
 */
function printRate(data) {
    var canvas = document.getElementById('canvas'+data.id),  //获取canvas元素
        context = canvas.getContext('2d'),  //获取画图环境，指明为2d
        centerX = canvas.width/2,   //Canvas中心点x轴坐标
        centerY = canvas.height/2,  //Canvas中心点y轴坐标
        rad = Math.PI*2/100, //将360度分成100份，那么每一份就是rad度
        speed = 0.1; //加载的快慢就靠它了
    //绘制蓝色外圈
    function blueCircle(n){
        context.save();
        context.strokeStyle = "#fff"; //设置描边样式
        context.lineWidth = 5; //设置线宽
        context.beginPath(); //路径开始
        context.arc(centerX, centerY, 50 , -Math.PI/2, -Math.PI/2 +n*rad, false); //用于绘制圆弧context.arc(x坐标，y坐标，半径，起始角度，终止角度，顺时针/逆时针)
        context.stroke(); //绘制
        context.closePath(); //路径结束
        context.restore();
    }
    //绘制白色外圈
    function whiteCircle(){
        context.save();
        context.beginPath();
        context.strokeStyle = "white";
        context.arc(centerX, centerY, 50 , 0, Math.PI*2, false);
        context.stroke();
        context.closePath();
        context.restore();
    }
    //百分比文字绘制
    function text(n){
        context.save(); //save和restore可以保证样式属性只运用于该段canvas元素
        context.strokeStyle = "#fff"; //设置描边样式
        context.font = "40px Arial"; //设置字体大小和字体
        //绘制字体，并且指定位置
        context.strokeText(n.toFixed(0)+"%", centerX-35, centerY+10);
        context.stroke(); //执行绘制
        context.restore();
    }
    //动画循环
    window.requestAnimationFrame(drawFrame);
    function drawFrame(){
        context.clearRect(0, 0, canvas.width, canvas.height);
        whiteCircle();
        text(speed);
        blueCircle(speed);
        // console.log(speed)
        // console.log(data.rate)
        if(speed/100 < data.rate){
            speed += 0.5;
            // console.log("1");
            window.requestAnimationFrame(drawFrame);
        }
    };
}

/**
 * 屏蔽form提交
 */
$('.evaluateForm').on("submit", function (ev) {
    // clickLogin();
    ev.preventDefault();
    evaluateForm.submitForm();
});
