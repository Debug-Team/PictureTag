function getProject() {
    var proid = window.location.href.split("?")[1];
    var allGetStr = null;
    var pro_json_whole = null;
    console.log(proid)
    $.ajax({
        url:'/getproject',
        type:'post',
        async:false,
        data:{
            id:proid
        },
        success:function (data) {
            allGetStr = data;
        },
        error:function () {
            alert("fail");
        }

    });

    pro_json_whole = JSON.parse(allGetStr);
    return pro_json_whole;

}

function getMarkerWorked(phonenum,pro_id) {
    var getAllStr = null
    var markerworked = null;

    $.ajax({
        url:'/getJobDetail',
        type:'post',
        async:false,
        data:{
            phonenum:phonenum,
            id:pro_id

        },
        success:function (data) {
            getAllStr = data;
        },
        error:function () {
            alert("fail");
        }

    });

    markerworked = JSON.parse(getAllStr);
    return markerworked;

}

function loadProInfo() {

    var proid = window.location.href.split("?")[1];
    var project_json = pro_detail_json;

    var projectTitle = document.getElementById("projectTitle");
    var titleMessage = "";
    var pro_type = project_json.type;
    if(pro_type == 1){
        titleMessage = "整体标注";
    }else if(pro_type==2){
        titleMessage = "标框标注";
    }else {
        titleMessage = "轮廓标注";
    }
    titleMessage = titleMessage + "  ID:" + project_json.id;
    projectTitle.innerHTML = titleMessage;

    var award = document.getElementById("award");
    award.innerHTML = "奖励积分：🎁" + parseInt(project_json.award*(1-project_json.cut));

    var discription = document.getElementById("discription");
    discription.innerHTML = "项目描述：" + project_json.description;

    var tagRule = document.getElementById("tagRule");
    tagRule.innerHTML = "怎么标注的信息";

    var pictures_array = project_json.picList;
    var pic_display_column1 = document.getElementById("pic_display_column1");
    var pic_display_column2 = document.getElementById("pic_display_column2");
    var pic_display_column3 = document.getElementById("pic_display_column3");
    var pic_display_column4 = document.getElementById("pic_display_column4");
    var pic_display_column5 = document.getElementById("pic_display_column5");

    if(pictures_array.length>20){
        full_load_bool = false;
    }
    for (var i= 0;i<pictures_array.length; i++){
        if(i<20) {                                  //出事加载20张
            var pic_image_src = "../" + "pic/" + project_json.id + "/" + pictures_array[i];
            console.log(pic_image_src)
            var li = document.createElement("li");
            var a = document.createElement("a");
            var img = document.createElement("img");

            li.classList.add("picture_single");
            a.classList.add("normal");
            img.classList.add("picture_introduce");

            li.appendChild(a);
            a.appendChild(img);
            if (isMypro()){
                a.href = "workerMarking.html"+"?"+proid+"="+pictures_array[i].split(".")[0];

            }
            console.log(a.href);
            img.src = pic_image_src;
            img.id = pictures_array[i].split(".")[0];

            var count = (i + 1) % 5;
            switch (count) {
                case 1:
                    pic_display_column1.appendChild(li);
                    break;
                case 2:
                    pic_display_column2.appendChild(li);
                    break;
                case 3:
                    pic_display_column3.appendChild(li);
                    break;
                case 4:
                    pic_display_column4.appendChild(li);
                    break;
                case 0:
                    pic_display_column5.appendChild(li);
                    break;

            }
            img.onload = function (ev) { clip(); }

        }
    }


}

var test = function () {

     var lis = document.getElementsByClassName("picture_single");
     console.log(lis.length)
     for (var i = 0; i<lis.length;i++){
         var tagged_tip_div = document.createElement("div");
         var tagged_tip_a = document.createElement("a");

         var svg_html = " <svg style=\"height: 200px;width: 200px\">\n" +
             "                            <polyline points=\"55,80 80, 105 150,55\" stroke-width=\"6\" stroke=\"white\" fill=\"none\"></polyline>\n" +
             "                            <text x=\"65\" y=\"135\" fill=\"white\" font-size=\"20px\">已标注</text></svg>\n";
         tagged_tip_a.innerHTML = svg_html;
         tagged_tip_div.appendChild(tagged_tip_a);

         tagged_tip_div.style = "top: 0;left: 0;height:200px;width:200px;position: absolute;background: rgba(0,0,0,0.7);color: #f5f1e5;";


         lis[i].appendChild(tagged_tip_div);

     }
}

function loadMarkerJob() {


    var phonenum = getUserPhone();
    var pro_id = window.location.href.split("?")[1];
    var markerjob_json = getMarkerWorked(phonenum,pro_id);
    var tagged_pic_array = markerjob_json.markedPicList;
    console.log(markerjob_json)

    for (var i = 0;i<tagged_pic_array.length;i++){

        var tagged_tip_div = document.createElement("div");
        var tagged_tip_a = document.createElement("a");

        var svg_html = " <svg style=\"height: 200px;width: 200px\">\n" +
            "                            <polyline points=\"55,80 80, 105 150,55\" stroke-width=\"6\" stroke=\"white\" fill=\"none\"></polyline>\n" +
            "                            <text x=\"65\" y=\"135\" fill=\"white\" font-size=\"20px\">已标注</text></svg>\n";
        tagged_tip_a.innerHTML = svg_html;
        tagged_tip_div.appendChild(tagged_tip_a);

        tagged_tip_div.style = "top: 0;left: 0;height:200px;width:200px;position: absolute;background: rgba(0,0,0,0.7);color: #f5f1e5;";



        var id_target = tagged_pic_array[i].split(".")[0];
        var img_target = document.getElementById(id_target);
        var a_target = img_target.parentNode;
        tagged_tip_a.href = a_target.href;
        console.log(111,a_target.href);
        var li_target = a_target.parentNode;
        console.log(li_target)
        li_target.appendChild(tagged_tip_div);


    }


}

var pro_detail_json = getProject();
var full_load_bool = true;
var current_load_process = 20;
window.onload = function (ev) {
    loadProInfo();
    if(isMypro()){
        console.log("ss")
        loadMarkerJob();  //待添加逻辑 只有是自己已经接受的项目才调用loadMarkerJOB

    }
    // setTimeout("clip();",50)

}
// test();

function startNewJob() {
    var phonenum = getUserPhone();
    var id = window.location.href.split("?")[1];
    var result_json = null;
    $.ajax({
        url:'/startNewJob',
        type:'post',
        async:false,
        data:{
            phonenum:phonenum,
                id:id
        },
        success:function(data) {
            result_json = JSON.parse(data);
        },
        error:function () {
            alert("fail")
        }
    });
    // alert((result_json.state==1)?"成功":"失败");
    if(result_json!=null){
        alert("完成时间限制：10天，逾期将被踢出项目并降低信誉度");
    }
}

function isMypro() {
    var result = false;
    var markerlist = pro_detail_json.markerList;
    var phonenum = getUserPhone();

    for(var i = 0;i<markerlist.length;i++){
        if(markerlist[i]==phonenum){
            result = true;
            break;
        }
    }


    return result;
}

function loadhref() {

    var pro__id = window.location.href.split("?")[1];
    var pic_display_img = document.getElementsByClassName("picture_introduce");
    for (var i= 0;i<pic_display_img.length; i++){
        pic_display_img[i].parentNode.href = "workerMarking.html"+"?"+pro__id+"="+ pic_display_img[i].id;
        console.log(pic_display_img.id,88)
    }
}

function finishMarkerJob() {

    var phonenum = getUserPhone();
    var proid = window.location.href.split("?")[1];
    var result_json = {};
    $.ajax({
        url:'/finishMakerJob',
        type:'post',
        async:false,
        data:{
            phonenum:phonenum,
            id:proid
        },
        success:function(data) {
            result_json = JSON.parse(data)
        },
        error:function () {
            alert("fail")
        }
    });
    return result_json;
}