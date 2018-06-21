var filter = new Vue({
    el: "#filter",
    data: {
        selectedList:[],
        conditionList:[
            {title:"类型", items:["整体标注", "标框标注", "边界标注"]},
            {title:"分类", items:getCategoriesList()},
            {title:"是否结束", items:["已结束", "进行中"]}],
        totalSize: 0
    },
    methods:{
        /**
         * 删除已选标签
         * @param index
         */
        deleteSelected: function (index) {
            this.selectedList.splice(index, 1);
        },
        /**
         * 添加标签项
         * @param row_index 哪行
         * @param index 哪项
         */
        addItem: function (row_index, index) {
            var item = this.conditionList[row_index].items[index];
            for(var i = 0;i < this.selectedList.length;i++){       //重复选无效
                if(this.selectedList[i] == item){
                    return;
                }
            }
            this.selectedList.push(item);
        },
        hideGroup: function () {
            $(".group").hide("fast");
            $(".collapse").hide();
            $(".expand").show();
        },
        showGroup: function () {
            $(".group").show("fast");
            $(".expand").hide();
            $(".collapse").show();
        }
    }
});