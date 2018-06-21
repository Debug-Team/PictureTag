function gettasks() {
    var phonenum = getUserPhone();
    var task_jsons = null;
    $.ajax({
        url:'/getDailyMisson',
        type:'post',
        async:false,
        data:{
            phonenum:phonenum
        },
        success:function (data) {
            task_jsons = JSON.parse(data);
        },
        error:function () {
            alert("fail"+"fuck");
        }
    });
    console.log(task_jsons);
    return task_jsons;
}

function loadTaskDetail() {
    var tasks_jsons = gettasks();
    var tasks_untake = tasks_jsons.unTake;
    var tasks_isDoing = tasks_jsons.isDoing;
    var tasks_isDone = tasks_jsons.isDone;

    for(var i = 0;i<tasks_untake.length;i++){
        addTask(tasks_untake[i],"untake");
    }
    for (var i = 0;i<tasks_isDoing.length;i++){
        addTask(tasks_isDoing[i],"isdoing");
    }
    for (var i = 0;i<tasks_isDone.length;i++){
        if(tasks_isDone[i].isAwarded==false){
            addTask(tasks_isDone[i],"isdone");
        }else {
            addTask(tasks_isDone[i],"isaccept");
        }
    }
}

function addTask(task_json,type) {
   var div_all = document.createElement("div");
   div_all.classList.add("col-md-14");

   var div_panel = document.createElement("div");
   div_panel.classList.add("panel");

   var div_panel_head = document.createElement("div");
   div_panel_head.classList.add("panel-heading");

    var div_panel_footer = document.createElement("div");
    div_panel_footer.classList.add("panel-footer");

    var div_panel_head_p = document.createElement("p");
    var div_panel_head_p_txt = document.createTextNode(task_json.description);
    div_panel_head_p.appendChild(div_panel_head_p_txt);
    div_panel_head.appendChild(div_panel_head_p);

    if(type=="untake") {
        var div_panel_head_right = document.createElement("div");
        div_panel_head_right.classList.add("right");

        var div_panel_head_right_button = document.createElement("button");
        div_panel_head_right_button.setAttribute("onclick","acceptMission(this)");
        div_panel_head_right_button.setAttribute("value",task_json.type);

        var div_button_p = document.createElement("p");
        div_button_p.style = "color: limegreen;";
        var div_button_txt = document.createTextNode("接受");
        div_button_p.appendChild(div_button_txt);
        div_panel_head_right_button.appendChild(div_button_p);

        div_panel_head_right.appendChild(div_panel_head_right_button);
        div_panel_head.appendChild(div_panel_head_right);

    }else if(type=="isdone"){
        var div_panel_head_right = document.createElement("div");
        div_panel_head_right.classList.add("right");

        var div_panel_head_right_button = document.createElement("button");
        div_panel_head_right_button.setAttribute("onclick","receiveReward(this)");
        div_panel_head_right_button.setAttribute("value",task_json.type);

        var div_button_p = document.createElement("p");
        div_button_p.style = "color: limegreen;";
        var div_button_txt = document.createTextNode("领取奖励");
        div_button_p.appendChild(div_button_txt);
        div_panel_head_right_button.appendChild(div_button_p);

        div_panel_head_right.appendChild(div_panel_head_right_button);
        div_panel_head.appendChild(div_panel_head_right);
    }else if(type == "isaccept"){
        var div_panel_head_right = document.createElement("div");
        div_panel_head_right.classList.add("right");
        var div_panel_head_right_button = document.createElement("button");
        var div_button_p = document.createElement("p");
        div_button_p.style = "color: grey;";
        var div_button_txt = document.createTextNode("已领取");
        div_button_p.appendChild(div_button_txt);
        div_panel_head_right_button.appendChild(div_button_p);

        div_panel_head_right.appendChild(div_panel_head_right_button);
        div_panel_head.appendChild(div_panel_head_right);
    }

    div_panel.appendChild(div_panel_head);
    div_panel.appendChild(div_panel_footer);
    div_all.appendChild(div_panel);


    var p1 = document.createElement("p");
    p1.style = "float: left;margin:0px 20px;";
    var p1txt = document.createTextNode("奖励积分："+task_json.awardCredits);
    p1.appendChild(p1txt);

    var p2 = document.createElement("p");
    var p2txt = document.createTextNode("奖励经验："+task_json.awardEmpiricalValue);
    p2.appendChild(p2txt);

    div_panel_footer.appendChild(p1);
    div_panel_footer.appendChild(p2);

    if(type=="isdoing"||type=="isdone"||type=="isaccept"){
        p2.style = "float: left;margin:0px 20px;";
        var p3 = document.createElement("p");
        var p3txt = document.createTextNode("进度："+task_json.alreadyNum+"/"+task_json.neededNum);
        p3.appendChild(p3txt);
        div_panel_footer.appendChild(p3);
    }




    if(type=="untake"){
        var untakepanel = document.getElementById("taskUnTakePanel");
        untakepanel.appendChild(div_all);
    }else if(type=="isdoing"){
        var isdoingpanel = document.getElementById("isDoingPanel");
        isdoingpanel.appendChild(div_all);

    }else if(type=="isdone"){
        var isDonePanel = document.getElementById("isDonePanel");
        isDonePanel.appendChild(div_all);
    }else if(type == "isaccept") {
        var isDonePanel = document.getElementById("isDonePanel");
        isDonePanel.appendChild(div_all);
    }


}

function acceptMission(obj) {
    var type = obj.value
    var phonenum = getUserPhone();
    var task_jsons = null;
    $.ajax({
        url:'/acceptDailyMisson',
        type:'post',
        async:false,
        data:{
            phonenum:phonenum,
            type:type
        },
        success:function (data) {
            task_jsons = JSON.parse(data);
        },
        error:function () {
            alert("fail"+"fuck");
        }
    });
    location.reload();
}
function receiveReward(obj) {
    var type = obj.value
    var phonenum = getUserPhone();
    var task_jsons = null;
    $.ajax({
        url:'/acceptAward',
        type:'post',
        async:false,
        data:{
            phonenum:phonenum,
            type:type
        },
        success:function (data) {
            task_jsons = JSON.parse(data);
        },
        error:function () {
            alert("fail"+"fuck");
        }
    });
    location.reload();
}
loadTaskDetail();
loadWantedList();
function getWantedList() {
    var phonenum = getUserPhone();
    var wantedlist_jsons = null;
    $.ajax({
        url:'/getWantedList',
        type:'post',
        async:false,
        data:{
            phonenum:phonenum
        },
        success:function (data) {
            wantedlist_jsons = JSON.parse(data);
        },
        error:function () {
            alert("fail"+"fuck");
        }
    });
    return wantedlist_jsons;
}
function loadWantedList() {
    var wanted_list = getWantedList().data;
    var wanted_div = document.getElementById("wanted_div");
    for(var i = 0;i<wanted_list.length;i++){


        var div_all = document.createElement("div");
        div_all.classList.add("col-md-12");
        wanted_div.appendChild(div_all);

        var div_panel = document.createElement("div");
        div_panel.classList.add("panel");

        var div_panel_head = document.createElement("div");
        div_panel_head.classList.add("panel-heading");

        div_all.appendChild(div_panel);
        div_panel.appendChild(div_panel_head);

        var div_right = document.createElement("div");
        div_right.classList.add("right");
        div_panel_head.appendChild(div_right);

        var div_button = document.createElement("button");
        div_button.setAttribute("onclick","acceptWantedTask(this)");
        div_button.setAttribute("value",wanted_list[i].id);
        div_right.appendChild(div_button);

        var p_button = document.createElement("p");
        p_button.style = "color: dodgerblue;";
        var p_button_txt = document.createTextNode("接受");
        p_button.appendChild(p_button_txt);
        div_button.appendChild(p_button);



        var p_pro_name = document.createElement("p");
        var p_pro_name_txt = document.createTextNode("项目名称："+wanted_list[i].name);
        p_pro_name.appendChild(p_pro_name_txt);
        div_panel_head.appendChild(p_pro_name);

        var p_pro_award = document.createElement("p");
        var p_pro_award_txt = document.createTextNode("项目奖励："+wanted_list[i].award);
        p_pro_award.appendChild(p_pro_award_txt);
        div_panel_head.appendChild(p_pro_award);

        var p_pro_classificationList = document.createElement("p");
        var classificationList = "";
        for(var j = 0;j<wanted_list[i].classificationList.length;j++){
            classificationList += wanted_list[i].classificationList[j]+"; ";
        }
        var p_pro_classificationList_txt = document.createTextNode("项目分类："+ classificationList);
        p_pro_classificationList.appendChild(p_pro_classificationList_txt);
        div_panel_head.appendChild(p_pro_classificationList);




    }
}
function acceptWantedTask(obj) {
    // alert("领取悬赏任务成功")
    var id = obj.value
    var phonenum = getUserPhone();
    var result_jsons = null;
    $.ajax({
        url:'/startNewWantedJob',
        type:'post',
        async:false,
        data:{
            phonenum:phonenum,
            id:id
        },
        success:function (data) {
            result_jsons = JSON.parse(data);
        },
        error:function () {
            alert("fail");
        }
    });
    location.reload();
}