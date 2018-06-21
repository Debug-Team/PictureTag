// window.Vue = Vue;
var logSign_detail = new Vue({
    el: '#logSign_detail',
    data: {
        curPage: 1,
        perPage: 10,
        totalPage: 0,
        detailList: [],
        actionType: ""
    },
    watch:{
        perPage: function (oldKey, newKey) {
            this.totalPage = Math.ceil(this.detailList.length/this.perPage);
            this.curPage = 1;
        }
    },
    mounted: function(){
        var res_json = getDetailList();
        var list = [];
        for(var i = 0; i < res_json.userphone.length; i++){
            var data = {};
            data.id = res_json.userphone[i];
            data.name = res_json.username[i];
            data.time = res_json.time[i];
            data.type = res_json.usertype[i];
            list.push(data);
        }
        console.log(list.length/this.perPage);
        this.totalPage = Math.ceil(list.length/this.perPage);
        this.detailList = list;

        this.actionType = getActionType()+"情况";
    },
    computed:{
        /**
         * 计算当前页需要显示哪些
         * @returns {Array}
         */
        curPageList: function () {
            var curList = [];
            var startI = (this.curPage-1)*this.perPage;
            for(var i = 0; startI+i < this.detailList.length && i < this.perPage; i++){
                curList.push(this.detailList[startI+i])
            }
            return curList;
        }
    },
    methods:{
        /**
         * 点击下一页
         */
        downPage: function () {
            var test = this.curPage * this.perPage;
            if(test >= this.detailList.length){
                return;
            }
            this.curPage++;
        },
        /**
         * 点击上一页
         */
        upPage: function () {
            var test = (this.curPage-1) * this.perPage;
            if(test <= 0){
                return;
            }
            this.curPage--;
        }
    }
});

/**
 * 获取用户的积分明细
 * @return {Json} time, detail, credits 均为数组
 */
function getDetailList() {
    var action_type = document.location.href.split("?")[1].split("&")[0];
    var date = document.location.href.split("?")[1].split("&")[1];
    var url_temp = "/daily" + action_type + "Detail"
    var res = {};
    $.ajax({
        url:url_temp,
        type:'post',
        async:false,
        data:{
            date:date
        },
        success: function (data) {
            // console.log(data);
            res = JSON.parse(data);
            // credits = temp.credits;
        },
        error: function () {
            alert("fail");
        }
    })
    return res;
}
function getActionType() {
    var action_en = document.location.href.split("?")[1].split("&")[0];
    var action_ch = (action_en=="SignUp")?"注册":"登录";
    return action_ch;
}
