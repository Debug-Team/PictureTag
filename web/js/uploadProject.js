
/**
 * @author yyr
 */

/**
 * 窗口初始化时加载
 */
window.onload =function () {
    loadUserCredits();
}

/**
 * 最后项目要求的条件渲染
 */
var msform = new Vue({
    el: '#msform',
    data: {
        projectType: 'null',
        // colorData: [
        //     {color: '', description:''}
        // ]
        colorData: [],
        tagState: -1,
        categories: [],
        selectedCategory: "",
        selectedCategoryList: [],
        suggestCredits: {}
    },
    mounted: function () {
        this.categories = getCategoriesList();
    },
    methods:{
        /**
         * 选择项目类型的响应
         */
        selectType: function () {
            var typeSelect = document.getElementById("projectType");
            var type = typeSelect.options[typeSelect.selectedIndex].value;

            var submit = document.getElementById("submit");

            // alert(type);
            if(type == ""){
                this.projectType = "null";
                submit.disabled = true;
            }
            else{
                this.projectType = type;
                submit.disabled = false;
                //清零
                this.colorData = [];
                this.tagState = -1;
            }
        },
        /**
         * 点击选择文件的响应
         */
        clickFile: function () {
            var file = document.getElementById("file");
            var filename = document.getElementById("filename");
            file.click();
            file.onchange = function () {
                filename.value = file.files[0].name;
            }
        },
        /**
         * 选择内容分类的响应
         */
        selectCategory: function () {
            for(var i = 0;i < this.selectedCategoryList.length;i++){            //判断是否重复
                if(this.selectedCategoryList[i] == this.selectedCategory){
                    return;
                }
            }
            this.selectedCategoryList.push(this.selectedCategory);
        },
        /**
         * 删除已选分类
         * @param index
         */
        deleteCategory: function (index) {
            this.selectedCategoryList.splice(index,1);
        },
        /**
         * 点击开始输入积分时，计算给出推荐积分
         */
        clickPointToCalc: function () {
            $(".suggestPoint").show("fast");

            var type = parseInt(document.getElementById("projectType").value);
            var numOfPicture = parseInt(document.getElementById("numOfPicture").value);

            console.log("type:"+type+"  num:"+numOfPicture);
            this.suggestCredits = getSuggestCredits(type, numOfPicture, this.selectedCategoryList);
        },
        /**
         * 标框标注中点击添加颜色和对应属性的响应
         */
        clickAddColor: function () {
            var colorPicker = document.getElementById("colorPicker");
            var colorDescription = document.getElementById("colorDescription");
            // alert(colorPicker.value);
            var color = colorPicker.value;
            var description = colorDescription.value;
            // alert(description)
            // this.colorData.push({color:color, description:description});
            // this.colorData.push({data: color+': '+description});
            var data = {};
            data.color = color;
            data.description = description;
            this.colorData.push(data);
        },
        /**
         * 标框标注中点击删除按钮的响应
         * @param index
         */
        deleteData: function (index) {
            this.colorData.splice(index,1);
        },
        // /**
        //  * 边界标注中点击添加颜色和对应属性的响应
        //  */
        // clickAddColor_border: function () {
        //     var colorPicker = document.getElementById("colorPicker_border");
        //     var colorDescription = document.getElementById("colorDescription_border");
        //     var color = colorPicker.value;
        //     var description = colorDescription.value;
        //     var data = {};
        //     data.color = color;
        //     data.description = description;
        //     this.colorData.push(data);
        // },
        // /**
        //  * 边界标注中点击删除按钮的响应
        //  * @param index
        //  */
        // deleteData_border: function (index) {
        //     this.colorData.splice(index);
        // },
        /**
         * 边界标注中，选择具体的标注形式
         */
        selectTagState: function () {
            var tagStateSelect = document.getElementById("tagState");
            this.tagState = tagStateSelect.value;
            console.log(this.tagState);
        },
        /**
         * 提交
         */
        clickSubmit: function () {
            var type = parseInt(document.getElementById("projectType").value);
            // var cut = parseFloat(document.getElementById("divide").value)/100;
            var cut = 0.25;     //平台分成为自动调整，默认25%
            var award = parseInt(document.getElementById("point").value);
            var description = document.getElementById("projectDescription").value;
            var owner = getUserPhone();
            // var owner = document.cookie;
            var markedPersonNum = parseInt(document.getElementById("numOfWorker").value);
            var file = document.getElementById("file").files[0];
            var tags = "";
            var tagcolor = "";
            var tagstate = parseInt(this.tagState);
            var projectName = document.getElementById("projectName").value;
            var categories = this.selectedCategoryList;

            if(type == 1){    //分类标注
                tags = document.getElementById("tag_category").value;
            }
            else if(type == 2){   //标框标注
                var tempJson = JSON.parse('{"tagcolor":[]}');
                for(var i = 0; i < this.colorData.length;i++){
                    console.log(this.colorData[i]);
                    var item = this.colorData[i];
                    // tagcolor += item.color + ":" + item.description + ";";
                    var curJson = JSON.parse('{"' + item.description + '":"' + item.color + '"}');
                    tempJson.tagcolor.push(curJson);
                }
                tagcolor = JSON.stringify(tempJson);
                console.log(tagcolor);
            }
            else if(type == 3){
                if(tagstate == 1){
                    tags = document.getElementById("tag_border1").value;
                }
            }
            console.log(type + "," + cut + "," + award + "," + description + "," + owner + "," + markedPersonNum + "," + file + "," +  tags + "," +  tagcolor + "," +  tagstate);

            var result = submit(type, cut, award, description, owner, markedPersonNum, file, tags, tagcolor, tagstate, projectName, categories);
            console.log(result.state + " " + result.retMessage);
            alert(result.retMessage);
            if(result.state == 1){      //上传成功返回
                window.history.back();
            }
        }
    }
});

/**
 * 上传项目
 * @param type 项目类型，1-整体标注 2-标框标注 3-轮廓标注
 * @param cut 平台分成，double
 * @param award 总积分奖励
 * @param description 项目描述
 * @param owner 上传者电话号码id
 * @param markedPersonNum 标记人数
 * @param file zip文件
 * @param tags 标记tag的list
 * @param tagcolor 分类标注tag-颜色
 * @param tagState 轮廓标注的状态字
 * @param projectName 项目名称
 * @param categories 项目分类
 * @return 上传结果 state:0,文件为空;state: 1, 上传成功;state:-1,需为zip格式;state:-2,格式错误;state:-3,压缩包内文件错误;
 */
function submit(type, cut, award, description, owner, markedPersonNum, file, tags, tagcolor, tagstate, projectName, categories) {
    var res = null;
    var formData = new FormData();
    formData.append("file", file);

    formData.append("type", type);
    formData.append("cut", cut);
    formData.append("award", award);
    formData.append("description", description);
    formData.append("owner", owner);
    formData.append("markedPersonNum", markedPersonNum);
    formData.append("tags", tags);
    formData.append("tagcolor", tagcolor);
    formData.append("tagstate", tagstate);
    formData.append("projectname", projectName);
    formData.append("categories", categories);

    console.log(formData)
    $.ajax({
        url: '/upload',
        type: 'post',
        async: false,
        // data: {
        //     type:type,
        //     cut:cut,
        //     award:award,
        //     description:description,
        //     owner:owner,
        //     markedPersonNum:markedPersonNum,
        //     file:formData,
        //     tags:tags,
        //     tagcolor:tagcolor,
        //     tagstate:tagstate
        // },
        data: formData,
        processData: false,
        contentType: false,
        enctype:'multipart/form-data',
        success: function (data) {
            res = JSON.parse(data)
        },
        error: function() {
            alert('fail')
        }
    })
    return res;
}

/**
 * 获取推荐设置的积分的ajax
 * @param type
 * @param amounts
 * @param classificationList
 */
function getSuggestCredits(type, amounts, classificationList){
    var res = "";

    var formData = new FormData();
    formData.append("type", type);
    formData.append("amounts", amounts);
    formData.append("classificationList", classificationList);

    $.ajax({
        url:'/predictCredits',
        type:'post',
        async:false,
        data: formData,
        processData: false,
        contentType: false,
        enctype:'multipart/form-data',
        success: function (data) {
            console.log(data);
            res = JSON.parse(data);
        },
        error: function () {
            alert("fail");
        }
    })
    return res;
}

// /**
//  * 屏蔽form提交
//  */
// $("#msform").on("submit", function (ev) {
//     ev.preventDefault();
//     msform.clickSubmit();
// })

/**
 * 界面动画js
 */
//jQuery time
var current_fs, next_fs, previous_fs; //fieldsets
var left, opacity, scale; //fieldset properties which we will animate
var animating; //flag to prevent quick multi-click glitches

$(".next").click(function(){
    if(animating) return false;
    animating = true;

    current_fs = $(this).parent();
    next_fs = $(this).parent().next();

    //activate next step on progressbar using the index of next_fs
    $("#progressbar li").eq($("fieldset").index(next_fs)).addClass("active");

    //show the next fieldset
    next_fs.show();
    //hide the current fieldset with style
    current_fs.animate({opacity: 0}, {
        step: function(now, mx) {
            //as the opacity of current_fs reduces to 0 - stored in "now"
            //1. scale current_fs down to 80%
            scale = 1 - (1 - now) * 0.2;
            //2. bring next_fs from the right(50%)
            left = (now * 50)+"%";
            //3. increase opacity of next_fs to 1 as it moves in
            opacity = 1 - now;
            current_fs.css({
                'transform': 'scale('+scale+')',
                'position': 'absolute'
            });
            next_fs.css({'left': left, 'opacity': opacity});
        },
        duration: 800,
        complete: function(){
            current_fs.hide();
            animating = false;
        },
        //this comes from the custom easing plugin
        easing: 'easeInOutBack'
    });
});

$(".previous").click(function(){
    if(animating) return false;
    animating = true;

    current_fs = $(this).parent();
    previous_fs = $(this).parent().prev();

    //de-activate current step on progressbar
    $("#progressbar li").eq($("fieldset").index(current_fs)).removeClass("active");

    //show the previous fieldset
    previous_fs.show();
    //hide the current fieldset with style
    current_fs.animate({opacity: 0}, {
        step: function(now, mx) {
            //as the opacity of current_fs reduces to 0 - stored in "now"
            //1. scale previous_fs from 80% to 100%
            scale = 0.8 + (1 - now) * 0.2;
            //2. take current_fs to the right(50%) - from 0%
            left = ((1-now) * 50)+"%";
            //3. increase opacity of previous_fs to 1 as it moves in
            opacity = 1 - now;
            current_fs.css({'left': left});
            previous_fs.css({'transform': 'scale('+scale+')', 'opacity': opacity});
        },
        duration: 800,
        complete: function(){
            current_fs.hide();
            animating = false;
        },
        //this comes from the custom easing plugin
        easing: 'easeInOutBack'
    });
});
