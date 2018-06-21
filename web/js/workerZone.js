var worker_info_json = getworkerInfo();
loadRankData();
loadWorkerInfo()
function getworkerInfo() {
    var phonenum = getUserPhone();
    // console.log(phonenum)
    var worker_json = null;
    $.ajax({
        url:'/getMarkerDetail',
        type:'post',
        async:false,
        data:{
            phonenum:phonenum
        },
        success:function (data) {
            worker_json = JSON.parse(data);
        },
        error:function () {
            alert("fail")
        }
    });
    console.log("get",worker_json)
    return worker_json;
}

function loadRankData() {

    var rankStr = "";
    var user_array = new Array();
    var user_json_array = new Array();
    $.ajax({
        url:'/getUserRank',
        type:'post',
        async:false,
        success:function (data) {
            rankStr = data;
        },
        error:function () {
            alert("fail")
        }
    });
    console.log(rankStr)
    user_array = rankStr.split("#");
    var ranklist = document.getElementById("rankList");
    for (var i = 0;i<user_array.length;i++){

        user_json_array[i] = JSON.parse(user_array[i]);

        var rank = i+1;
        var username = user_json_array[i].username;
        var credits = user_json_array[i].credits;

        var rank_message = rank +". " + username +"         " +"🎁"+credits;
        var rank_li =document.createElement("li");
        var message_li = document.createTextNode(rank_message);

        rank_li.appendChild(message_li);
        ranklist.appendChild(rank_li);

    }





}
function loadWorkerInfo() {
    var worker = worker_info_json;

    var element_info = document.getElementsByClassName("list-right")[0];
    var worker_name_h1 = document.getElementById("worker_name");
    var credits_title = document.getElementById("credits_title");
    var my_rank = document.getElementById("my_rank");

    var phonenum_li = document.createElement("li");
    var exp_li = document.createElement("li");
    var credits_li = document.createElement("li");

    var phonenum_li_text = document.createTextNode(worker.phoneNum);
    var exp_li_text = document.createTextNode(worker.empiricalValue);
    var credits_li_text =document.createTextNode("🎁"+worker.credits);
    var worker_name_txt = document.createTextNode(worker.userName);
    var credits_title_text = document.createTextNode(worker.credits);
    var my_rank_text = document.createTextNode(getMyRank());

    element_info.appendChild(phonenum_li);
    phonenum_li.appendChild(phonenum_li_text);

    element_info.appendChild(exp_li);
    exp_li.appendChild(exp_li_text);

    element_info.appendChild(credits_li);
    credits_li.appendChild(credits_li_text);

    worker_name_h1.appendChild(worker_name_txt);

    credits_title.appendChild(credits_title_text);

    my_rank.appendChild(my_rank_text);

}

function getMyRank() {
    var phonenum = getUserPhone();
    var rank = "";
    $.ajax({
        url:'/getOwnRank',
        type:'post',
        async:false,
        data:{
            phonenum:phonenum
        },
        success:function (data) {
            rank = data;
        },
        error:function () {
            alert("fail")
        }
    });
    return rank;
}
