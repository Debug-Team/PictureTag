
var credit_detail = new Vue({
    el: '#credit_detail',
    data: {
        curPage: 1,
        perPage: 10,
        totalPage: 0,
        detailList: []
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
        for(var i = 0; i < res_json.time.length; i++){
            var data = {};
            data.time = res_json.time[i];
            data.detail = res_json.detail[i];
            data.credits = res_json.credits[i];
            list.push(data);
        }
        console.log(list.length/this.perPage);
        this.totalPage = Math.ceil(list.length/this.perPage);
        this.detailList = list;
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
    var phonenum = getUserPhone();
    var usertype = getUsertype();

    var target = '/';
    if(usertype == 1){
        target = '/getUploaderCreditsDetail';
    }
    else if(usertype == 0){
        target = '/getMarkerCreditsDetail';
    }

    var res = null;
    $.ajax({
        url:target,
        type:'post',
        async:false,
        data:{
            userphone:phonenum
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
