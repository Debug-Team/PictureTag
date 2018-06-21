(function ($) {
    //初始化参数
    var mySelect=function(ele,options){
        this.ele=ele;
        this.defaults={
            mult:false
        };
        this.options=$.extend({},this.defaults,options);
        this.result=[];
    };
    mySelect.prototype={
        init:function(){//初始化函数
            this.pubFunction();
            this.initOption();
            this.closeSelectEvent();
            this.addEvent();
        },
        closeSelectEvent:function(){
            var that=this;
            this.ele.find(".inputWrap").on("click",function(event){
                event.stopPropagation();
                if(that.ele.find(".inputWrap>i").hasClass("fa-angle-down")){
                    that.ele.find(".inputWrap>i").removeClass("fa-angle-down").addClass("fa-angle-up");
                    that.ele.find(".mySelect-option").animate({height:"400px",opacity:"1",overflow:"auto"},"fast","swing")
                    var ss = document.getElementsByClassName("mySelect-option");
                    for (var i = 0;i<ss.length;i++){
                        ss[i].style = "overflow-y:scroll";
                    }
                }else{
                    that.ele.find(".inputWrap>i").removeClass("fa-angle-up").addClass("fa-angle-down");
                    that.ele.find(".mySelect-option").animate({height:"0",opacity:"0"},"fast","swing")
                }
            });
            $("html").on("click",function(){
                that.ele.find(".inputWrap>i").removeClass("fa-angle-up").addClass("fa-angle-down");
                that.ele.find(".mySelect-option").animate({height:"0",opacity:"0"},"fast","swing")
            })
        },
        pubFunction:function(){
            Array.prototype.contains = function(obj) {
                var i = this.length;
                while (i--) {
                    if (this[i] === obj) {
                        return i;  // 返回的这个 i 就是元素的索引下标，
                    }
                }
                return false;
            }
        },
        initOption: function () {
            //初始化输入框和option
            this.ele.append('<div class="inputWrap"><ul></ul><i class="fa fa-angle-down"></i></div>');
            this.ele.append('<div class="mySelect-option"></div>');
            for(var i= 0;i<this.options.option.length;i++){
                this.ele.find(".mySelect-option").append('<div data-value="'+this.options.option[i].value+'"><span>'+this.options.option[i].label+'</span><i class="fa fa-check"></i></div>')
            }
        },
        addEvent:function(){
            var that=this;
            this.ele.find(".mySelect-option").find("div").on("click", function (event) {
                event.stopPropagation();
                if(that.options.mult){
                    if($(this).hasClass("selected")){
                        $(this).removeClass("selected");
                        that.result.splice(that.result.contains($(this).attr("data-value")),1)
                    }else{
                        $(this).addClass("selected");
                        that.result.push($(this).attr("data-value"))
                    }
                    that.refreshInput();
                }else{
                    if($(this).hasClass("selected")){
                        $(this).removeClass("selected");
                        that.result='';
                    }else{
                        that.ele.find(".mySelect-option").find("div").removeClass("selected");
                        $(this).addClass("selected");
                        that.result=$(this).attr("data-value");
                        that.ele.find(".inputWrap>i").removeClass("fa-angle-up").addClass("fa-angle-down");
                        that.ele.find(".mySelect-option").animate({height:"0",opacity:"0"},"fast","swing")
                    }
                    that.refreshInput($(this).find("span").text());
                }
                that.options.onChange(that.result)
            });
        },
        inputResultRemoveEvent:function(){
            var that=this;
            this.ele.find(".inputWrap ul li i").on("click",function(event){
                event.stopPropagation();
                that.result.splice(that.result.contains($(this).attr("data-value")),1);
                that.refreshInput();
                that.removeOptionStyle($(this).attr("data-value"));
                that.options.onChange(that.result);
            })
        },
        removeOptionStyle:function(val){
            this.ele.find(".mySelect-option").find("div").each(function(){
                if($(this).attr("data-value")==val){
                    $(this).removeClass("selected")
                }
            })
        },
        refreshInput:function(label){
            this.ele.find(".inputWrap ul").empty();
            if(this.options.mult){
                for(var i=0;i<this.options.option.length;i++){
                    for(var j=0;j<this.result.length;j++){
                        if(this.result[j]==this.options.option[i].value){
                            this.ele.find(".inputWrap ul").append('<li><span>'+this.options.option[i].label+'</span>&nbsp;&nbsp;<i data-value="'+this.options.option[i].value+'" class="fa fa-close"></i></li>')
                        }
                    }
                }
            }else{
                if(this.result==''){
                    this.ele.find(".inputWrap ul").empty()
                }else{
                    this.ele.find(".inputWrap ul").append('<li><span>'+label+'</span>&nbsp;&nbsp;</li>')
                }

            }
            this.inputResultRemoveEvent();
        },
        setResult:function(res){
            this.result=res;
            if(this.options.mult){
                if(res instanceof Array){
                    this.refreshInput();
                    this.ele.find(".mySelect-option").find("div").each(function(){
                        for(var i=0;i<res.length;i++){
                            if($(this).attr("data-value")==res[i]){
                                $(this).addClass("selected")
                            }
                        }

                    })
                }else{
                    alert("参数必须是数组")
                }

            }else{
                for(var i=0;i<this.options.option.length;i++){
                    if(this.options.option[i].value==res){
                        this.refreshInput(this.options.option[i].label)
                    }
                }
                this.ele.find(".mySelect-option").find("div").each(function(){
                    if($(this).attr("data-value")==res){
                        $(this).addClass("selected")
                    }
                })
            }

        },
        getResult:function(){
            return this.result;
        }
    };
    $.fn.mySelect=function(options){
        var select=new mySelect(this,options);
        select.init();
        return select;
    };
})(jQuery);

function getInterests() {
    var phonenum = getUserPhone();
    var result_json = {};
    $.ajax({
        url:'/getMarkerInterests',
        type:'post',
        async:false,
        data:{
            userphone:phonenum

        },
        success:function (data) {
            result_json = JSON.parse(data);
        },
        error:function () {
            alert("fail");
        }

    });
    console.log(result_json);
    return result_json;
}
// setInterests();
function setInterests() {

    var formData = new FormData();
    var result_json = {};
    var userPhone = getUserPhone();
    var type = 0;
    var amounts = 0;
    var classificationList = new Array();

    var selected_html_array = document.getElementsByClassName("inputWrap")[0].childNodes[0].childNodes;

    for(var i = 0;i<selected_html_array.length;i++){
        var txt_temp = selected_html_array[i].textContent;
        txt_temp   =   txt_temp.replace(/\s+/g,"");
        classificationList.push(txt_temp);
    }

    var type_txt = document.getElementsByClassName("inputWrap")[1].childNodes[0].textContent;
    type_txt   =   type_txt.replace(/\s+/g,"");
    if(type_txt=="整体标注"){
        type = 1;
    }else if(type_txt=="标框标注"){
        type = 2;
    }else if(type_txt=="边界标注"){
        type = 3;
    }

    var amounts_txt = document.getElementsByClassName("inputWrap")[2].childNodes[0].textContent;
    amounts_txt   =   amounts_txt.replace(/\s+/g,"");
    if(amounts_txt == "1-50"){
        amounts = 1;
    }else if(amounts_txt=="51-100"){
        amounts = 2;
    }else if(amounts_txt=="101-200"){
        amounts = 3;
    }else if(amounts_txt=="201-..."){
        amounts = 4;
    }

    formData.append("userphone",userPhone);
    formData.append("type",type);
    formData.append("amounts",amounts);
    formData.append("classificationList",classificationList);

    // console.log("ww",userPhone,type,amounts,classificationList)

    $.ajax({
        url:'/setMarkerInterests',
        type:'post',
        async:false,
        data:formData,
        processData: false,
        contentType: false,
        enctype:'multipart/form-data',
        success:function (data) {
            result_json = JSON.parse(data);
        },
        error:function () {
            alert("fail");
        }

    });
    console.log("ssww",result_json);
    $("#popup").hide();
    $("#mask_shadow").hide();
}

function loadInterests(mySelect,mySelect2,mySelect3){
    var interests_json = getInterests();
    var interestNum = interests_json.amounts;
    var classificationList = interests_json.classificationList;
    var interestType = interests_json.type;

    console.log(333,interests_json)

    mySelect2.setResult(interestType);
    mySelect3.setResult(interestNum);

    var all_catefories = getClassification();
    var index_array = new Array();
    for(var i = 0;i<classificationList.length;i++){
        for(var j = 0;j<all_catefories.length;j++){
            if(classificationList[i] == all_catefories[j]){
                index_array.push(""+j);
            }
        }
    }
    console.log(3333,index_array,interestNum,interestType);
    mySelect.setResult(index_array);
}


$(function(){
    var all_option = getClassification();
    var options_select = new Array();
    for(var i = 0; i<all_option.length;i++){
        var json_temp = {};
        json_temp.label = all_option[i];
        json_temp.value = i;
        options_select.push(json_temp);
    }
    // console.log(options_select);
    var mySelect= $("#mySelect").mySelect({
        mult:true,//true为多选,false为单选
        option:options_select,
        onChange:function(res){//选择框值变化返回结果
            console.log(res)
        }
    });
    // mySelect.setResult(["1","2"]);
    var mySelect2= $("#mySelect2").mySelect({
        mult:false,
        option:[
            {label:"整体标注",value:1},
            {label:"标框标注",value:2},
            {label:"边界标注",value:3}
        ],
        onChange:function(res){
            console.log(res)
        }
    });
    // mySelect2.setResult(1);

    var mySelect3= $("#mySelect3").mySelect({
        mult:false,
        option:[
            {label:"1-50",value:1},
            {label:"51-100",value:2},
            {label:"101-200",value:3},
            {label:"201-...",value:4}
        ],
        onChange:function(res){
            console.log(res)
        }
    });
    // mySelect3.setResult(3)
    // console.log(mySelect.options.option);
    loadInterests(mySelect,mySelect2,mySelect3);

})


function getClassification() {
    var result_json = {};
    var result_array = new Array();
    $.ajax({
        url:'/getClassification',
        type:'post',
        async:false,
        success:function (data) {
            result_json = JSON.parse(data);
        },
        error:function () {
            alert("fail");
        }

    });
    result_array = result_json.categories;
    console.log(result_array);
    return result_array;
}