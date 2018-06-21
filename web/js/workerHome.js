/**
 * 从后端获得预览项目的json 的array
 * @param phonenum 标注者账号
 * @param state 状态字 1-推荐项目 2-全部项目 3-我的项目
 * @return json状态信息
 */
function getProjects(phonenum,state) {
    // console.log(2);
    var projectsStr = null;
    var projects_get = new Array();
    var projects_json_array = new Array();
    $.ajax({
        url:'/getprojectlist',
        type:'post',
        async:false,
        data:{
            phonenum:phonenum,
            state:state
        },
        success:function (data) {
            projectsStr = data;
        },
        error:function () {
            alert("fail")
        }
    });
    console.log("?s",projectsStr);
    if(projectsStr!=""){
        projects_get = projectsStr.split("-");
        // console.log(projects_get)
        for (var x in projects_get){
            projects_json_array.push(JSON.parse(projects_get[x]))
        }
    }

    // console.log(3);
    return projects_json_array;

}

/**
 * @param projects_html_state 项目状态 状态字 1-推荐项目 2-全部项目 3-我的项目
 * 在html上加载项目信息等
 */
function loadAllProjects(projects_html_state) {
    // console.log(1);
    var phonenum = getUserPhone();                          //util中得到cookie中number的方法
    var projects_json_array = getProjects(phonenum,projects_html_state);      //暂定推荐 后面更具href尾确定type

    var pictures_display = document.getElementsByClassName("pictures_display");

    var add_html_all = "";
    for (var x in projects_json_array){
        var pro_json = projects_json_array[x];
        // console.log(4);

        var pro_image_src = "../"+"pic/" + pro_json.id + "/"+ pro_json.previewPic;
        // var pro_image_src = "https://images.unsplash.com/photo-1511189975737-b5939ef6a944?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=8b6af19ba770980521cdd98c2c60eef1&auto=format&fit=crop&w=500&q=60"
        // console.log(pro_image_src)
        var pro_type = "";
        if (pro_json.type == 1){
            pro_type = "整体标注";
        }else if(pro_json.type == 2){
            pro_type = "标框标注";
        }else{
            pro_type = "轮廓标注";
        }

        var pro_id = pro_json.id;
        var pro_discription = pro_json.discription;

        var isClosed = pro_json.ended;
        var project_preview_html = "";
        // if(isClosed){
        //     project_preview_html = " <li class = 'picture_single'>\n" +
        //         "            <a class='normal' href='#'>\n" +
        //         "\n" +
        //         "                    <img class= \"picture_introduce\" src = \"";
        //     project_preview_html = project_preview_html + pro_image_src;
        //     project_preview_html = project_preview_html + "\" >\n" +
        //         "            </a>\n" + "            <div style=\"top: 0px; left: 0px; height: 300px; width: 300px; position: absolute; background: rgba(0, 0, 0, 0.701961); color: rgb(245, 241, 229);\">\n" +
        //         "                <a >\n" +
        //         "                    <svg style=\"height: 300px;width: 300px\">\n" +
        //         "                        <!--<polyline points=\"55,80 80, 105 150,55\" stroke-width=\"6\" stroke=\"white\" fill=\"none\"></polyline>-->\n" +
        //         "                        <line x1=\"110\" y1=\"90\" x2=\"190 \" y2=\"170\" fill=\"white\" stroke=\"white\" stroke-width=\"20\"> </line>\n" +
        //         "                        <line x1=\"190\" y1=\"90\" x2=\"110 \" y2=\"170\" fill=\"white\" stroke=\"white\" stroke-width=\"20\"> </line>\n" +
        //         "                        <text x=\"105\" y=\"210\" fill=\"white\" font-size=\"30px\">已结束</text></svg>\n" +
        //         "            </a></div>" +
        //         "            <div class='info'>\n" +
        //         "                <h3>" + pro_type +
        //         "</h3>\n" +
        //         "                <p>"  + pro_id +
        //         "</p>\n" +
        //         "                <p>"  + pro_discription +
        //         "</p>\n" +
        //         "            </div>\n" +
        //         "        </li>";
        //
        // }else {

            project_preview_html = " <li class = 'picture_single'>\n" +
                "            <a class='normal' href='#'>\n" +
                "\n" +
                "                    <img class= \"picture_introduce\" src = \"";
            project_preview_html = project_preview_html + pro_image_src;
            project_preview_html = project_preview_html + "\" >\n" +
                "            </a>\n" +
                "            <div class='info'>\n" +
                "                <h3>" + pro_type +
                "</h3>\n" +
                "                <p>"  + pro_id +
                "</p>\n" +
                "                <p>"  + pro_discription +
                "</p>\n" +
                "            </div>\n" +
                "        </li>";
        // }


        add_html_all = add_html_all + project_preview_html;


        // console.log(5);
    }



    pictures_display[0].innerHTML = add_html_all;

    // for (var x in projects_json_array){
    //
    //     var pro_json = projects_json_array[x];
    //
    //     var li = document.createElement("li");
    //     var a = document.createElement("a");
    //     var img = document.createElement("img");
    //     var div = document.createElement("div");
    //     var pro_type_h3 = document.createElement("h3");
    //     var id_p = document.createElement("p");
    //     var discription_p = document.createElement("p");
    //
    //     var pro_type_h3_text = null;
    //         if (pro_json.type == 1){
    //             pro_type_h3_text =  document.createTextNode("整体标注");
    //         }else if(pro_json.type == 2){
    //             pro_type_h3_text =  document.createTextNode("标框标注");
    //         }else{
    //             pro_type_h3_text=  document.createTextNode("边缘标注");
    //         }
    //     var id_p_text = document.createTextNode(pro_json.id);
    //     var discription_p_text = document.createTextNode(pro_json.discription);
    //
    //
    //     li.classList.add("picture_single");
    //     a.classList.add("normal");
    //     div.classList.add("info");
    //     img.classList.add("picture_introduce");
    //     img.src = "../"+"pic/" + pro_json.id + "/"+ pro_json.previewPic;
    //
    //     pro_type_h3.appendChild(pro_type_h3_text);
    //     id_p.appendChild(id_p_text);
    //     discription_p.appendChild(discription_p_text);
    //     div.appendChild(pro_type_h3);
    //     div.appendChild(id_p);
    //     div.appendChild(discription_p);
    //     a.appendChild(img);
    //     li.appendChild(a);
    //     li.appendChild(div);
    //     pictures_display[0].appendChild(li);
    //
    //
    //
    // }

    // console.log(projects_html_state);
    var projects = document.getElementsByClassName("normal");
    for (var x in projects_json_array){
        var pro_json = projects_json_array[x];
        var pro_id = pro_json.id;

        projects[x].href = "projectOverview.html"+"?"+pro_id;


    }
    var imgs = document.getElementsByClassName("picture_introduce");


    // console.log(imgs)
    // for (var i = 0;i<imgs.length;i++){
    //     imgs[i].onload = function (ev) {
    //         // console.log(imgs[i].naturalWidth,imgs[i].naturalHeight);
    //         loadSpeciaEffects();
    //     }
    // }
    if(imgs.length!=0){
        for(var i = 0 ; i<imgs.length;i++){
            if(projects_json_array[i].ended == true){

                var isended_tip_div = document.createElement("div");
                var isended_tip_a = document.createElement("a");

                var svg_html = "<svg style=\"height: 300px;width: 300px\">\n" +
                    "                        <!--<polyline points=\"55,80 80, 105 150,55\" stroke-width=\"6\" stroke=\"white\" fill=\"none\"></polyline>-->\n" +
                    "                        <line x1=\"110\" y1=\"90\" x2=\"190 \" y2=\"170\" fill=\"white\" stroke=\"white\" stroke-width=\"20\"> </line>\n" +
                    "                        <line x1=\"190\" y1=\"90\" x2=\"110 \" y2=\"170\" fill=\"white\" stroke=\"white\" stroke-width=\"20\"> </line>\n" +
                    "                        <text x=\"105\" y=\"210\" fill=\"white\" font-size=\"30px\">已结束</text></svg>\n" +
                    "                ";
                isended_tip_div.style ="top: 0px; left: 0px; height: 300px; width: 300px; position: absolute; background: rgba(0, 0, 0, 0.701961); color: rgb(245, 241, 229);";
                isended_tip_a.innerHTML = svg_html;
                isended_tip_div.appendChild(isended_tip_a);
                imgs[i].parentNode.parentNode.appendChild(isended_tip_div);
                // imgs[i].onload = function () {
                //     clip();
                // }
                imgs[i].onload = function () {
                    clip();
                }
            }else {
                imgs[i].onload = function () {
                    loadSpeciaEffects();
                }
            }
        }

    }
    // setTimeout("loadSpeciaEffects()",50);


}
// var getPresentHref = function () {
//     // window.location.href = window.location.href.substring(0,index)
//     var location = new Array();
//     location = window.location.href.split("/");
//     var href = "";
//     for (var i= 0;i<location.length-1; i++){
//         href = href + location[i]+"/";
//     }
//     return href;
// }


/**
 * 窗体初始化时默认加载的方法
 */
window.onload = function (ev) {
    stateChoose(1);
    // loadSpeciaEffects();

}
/**
 * 根据项目所处状态（推荐 全部 标注个人）点击 跳转到不同项目预览界面的方法
 */
function stateChoose(state_num) {
    var state_recommend_pro = document.getElementById("state_recommend_pro");
    var state_all_pro = document.getElementById("state_all_pro");
    var state_my_pro = document.getElementById("state_my_pro");
    loadAllProjects(state_num);
    if(state_num==1){
        state_recommend_pro.classList.add("_3_FPZ");
        state_all_pro.classList.remove("_3_FPZ");
        state_my_pro.classList.remove("_3_FPZ");
        state_all_pro.style.cursor = "pointer";
        state_my_pro.style.cursor = "pointer";

    }else if(state_num == 2){
        state_all_pro.classList.add("_3_FPZ");
        state_recommend_pro.classList.remove("_3_FPZ");
        state_my_pro.classList.remove("_3_FPZ");
        state_recommend_pro.style.cursor = "pointer";
        state_my_pro.style.cursor = "pointer";

    }else {
        state_recommend_pro.classList.remove("_3_FPZ");
        state_all_pro.classList.remove("_3_FPZ");
        state_my_pro.classList.add("_3_FPZ");
        state_all_pro.style.cursor = "pointer";
        state_recommend_pro.style.cursor = "pointer";

    }
}

/**
 * 加载特效的代码
 */
function loadSpeciaEffects() {

    var nodes = document.querySelectorAll('.picture_single'),
        _nodes = [].slice.call(nodes, 0);

    var getDirection = function (ev, obj) {
        // console.log("getDirection()",ev,obj)
        var w = obj.offsetWidth,
            h = obj.offsetHeight,
            x = (ev.pageX - obj.offsetLeft - (w / 2) * (w > h ? (h / w) : 1)),
            y = (ev.pageY - obj.offsetTop - (h / 2) * (h > w ? (w / h) : 1)),
            d = Math.round(Math.atan2(y, x) / 1.57079633 + 5) % 4;
        // console.log(w,h,x,y,d)

        return d;
    };

    var addClass = function (ev, obj, state) {
        // console.log("addclass",ev,obj,state)
        var direction = getDirection(ev, obj),
            class_suffix = "";

        obj.className = "";

        switch (direction) {
            case 0 :
                class_suffix = '-top';
                break;
            case 1 :
                class_suffix = '-right';
                break;
            case 2 :
                class_suffix = '-bottom';
                break;
            case 3 :
                class_suffix = '-left';
                break;
        }

        obj.classList.add(state + class_suffix);
    };

// bind events
    _nodes.forEach(function (el) {
        el.addEventListener('mouseover', function (ev) {
            addClass(ev, this, 'in');
        }, false);

        el.addEventListener('mouseout', function (ev) {
            addClass(ev, this, 'out');
        }, false);
    });

   // setTimeout("clip();",50)


    clip();


}

/**
 * 加载图片时预设宽高 利于裁剪
 * @type {HTMLCollectionOf<Element>}
 */
function clip() {
    var pictures = document.getElementsByClassName("picture_introduce");
    var i;
    for (i = 0; i < pictures.length; i++) {
        var kuan = pictures[i].width;
        var gao = pictures[i].height;
        // console.log(kuan)
        // console.log(kuan,gao,1)
        if (kuan > gao) {

            pictures[i].height = 300;
        } else {

            pictures[i].width = 300;

        }
        pictures[i].style = "position:absolute; top:0px; left:0px; clip:rect(0px 300px 300px 0px);"
    }
}