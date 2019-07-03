var loadPicture = function (hot_x,hot_y,length_x,length_y) {
    var markerjob_json = markerjob;
    console.log(markerjob_json);
    var pic_array = markerjob_json.picList;
    console.log(pic_array);
    //
    var picname = window.location.href.split("?")[1].split("=")[1];
    for (var i = 0;i<pic_array.length;i++){
        var picnametm = pic_array[i].split(".")[0];
        if (picname==picnametm){
            picname = pic_array[i];
            break;
        }
    }
    console.log(picname)
    /**
     * @author 周正伟
     *
     * 预加载图片（onload冲突下的写法）
     * img.onload按照图片原始比例正确缩放图片适应canvas大小的算法
     */
    var img = new Image();
// img.src = "https://images.unsplash.com/photo-1510007552638-e1c0c4c67ee0?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=95ed13af4e929ecf4617003a8e056214&auto=format&fit=crop&w=500&q=60";          //  得到的图片路径
    img.src= "../" + "pic/" + window.location.href.split("?")[1].split("=")[0] + "/" + picname;
    img.onload = function (ev) {

        var dx;
        var dy;
        var dw;
        var dh;
        //drawimage 的四个参数

        var width_img = img.width;
        var height_img = img.height;

        var width_canvas = canvas_img.width;
        var height_canvas = canvas_img.height;

        if(width_img>height_img){
            dw = width_canvas;
            dh = height_img*(width_canvas/width_img);
            if(dh>height_canvas){
                dw = dw * (height_canvas/dh);
                dh = height_canvas;
            }
        }else{
            dh = height_canvas;
            dw = width_img*(height_canvas/height_img);
            if(dw>width_canvas){
                dh = dh * (width_canvas/dw);
                dw = width_canvas;
            }
        }
        // console.log(width_img,height_img);
        // console.log(width_canvas,height_canvas);
        dx = (width_canvas - dw)/2;
        dy = (height_canvas - dh)/2;

        canvas_img_ctx.drawImage(img,dx,dy,dw,dh);

        // canvas_img_ctx.fillStyle = "rgba(0,255,0,0.2)";
        // canvas_img_ctx.beginPath();
        // canvas_img_ctx.rect(hot_x,hot_y,length_x,length_y);
        // canvas_img_ctx.fill();
        // canvas_img_ctx.closePath();
        console.log(project_detail);
        if(project_detail.type==1){
            loadMarkedHotArea(picname);
        }


        // canvas_img_ctx.scale(570/img.height,570/img.height);
    }


}

/**
 * @author 周正伟
 *
 * 窗口加载完成后默认启动的函数
 */

window.onload = function (ev) {
    loadPicture();
    loadMarksInfo();
    loadOverView();
    start_time = new Date();
    // marks("border");
}
var markerjob = getmarkerjob();  //全局的一个markerjob
var project_detail = getProinfo();  //全局的一个项目信息json
var start_time = null;
var end_time = null;

// marks("border");
function getmarkerjob() {
    var detailmessage = window.location.href.split("?")[1];
    var pro_id = detailmessage.split("=")[0];
    var phonenum = getUserPhone();

    var markerjob_json = null;

    $.ajax({
        url:'/getJobDetail',
        type:'post',
        async:false,
        data:{
            phonenum:phonenum,
            id:pro_id

        },
        success:function (data) {
            markerjob_json = JSON.parse(data);
        },
        error:function () {
            alert("fail");
        }

    });
    return markerjob_json;

}


function getProinfo() {
    var pro_id = window.location.href.split("?")[1].split("=")[0];
    var pro_json = null;


    $.ajax({
        url:'/getproject',
        type:'post',
        async:false,
        data:{
            id:pro_id

        },
        success:function (data) {
            pro_json = JSON.parse(data);
        },
        error:function () {
            alert("fail");
        }

    });

    return pro_json;
}

function getMarkInfo(picname) {
    var detailmessage = window.location.href.split("?")[1];
    var pro_id = detailmessage.split("=")[0];
    var phonenum = getUserPhone();

    var pic_inital_json = null;
    var res = null;
    $.ajax({
        url:'/getPictureTag',
        type:'post',
        async:false,
        data:{
            phonenum:phonenum,
            id:pro_id,
            pic:picname

        },
        success:function (data) {
            res = data;
        },
        error:function () {
            alert("fail"+"fuck");
        }

    });

    pic_inital_json = res;
    return (pic_inital_json=="")?"":JSON.parse(pic_inital_json);

}

/**
 * @author 周正伟
 *
 * 修改时加载已保存的标注信息,加载界面上有关该项目的提示（标注工作）
 */
var loadMarksInfo = function () {
    var project_type = project_detail.type;
    document.getElementById("pro_dis").innerHTML = project_detail.description;

    if(project_type==1){                //整体标注

        marks('classify');//能进行整体标注

        // var revoke = document.getElementById("revoke");
        // var rubber = document.getElementById("rubber");
        // var pencil = document.getElementById("pencil");
        //
        // revoke.parentNode.parentNode.parentNode.removeChild(revoke.parentNode.parentNode);
        // rubber.parentNode.parentNode.parentNode.removeChild(rubber.parentNode.parentNode);
        // pencil.parentNode.parentNode.parentNode.removeChild(pencil.parentNode.parentNode);

        document.getElementById("pro_type").innerHTML = "整体标注";


        var tag_provide_div = document.getElementById("classify_tags_provided");
        var tags_provide_list = project_detail.tags;
        for(var i = 0;i<tags_provide_list.length;i++){

            var tag_span = document.createElement("span");
            var tag_a = document.createElement("a");
            var tag_text = document.createTextNode(tags_provide_list[i]);

            tag_a.classList.add("tag");

            tag_span.appendChild(tag_a);
            tag_a.appendChild(tag_text);

            tag_provide_div.appendChild(tag_span);

        }

        var tag_operate_div = document.getElementById("tags_operate");
        for(var i = 0; i<tags_provide_list.length;i++){

            var tag_operate_input_div = document.createElement("div");
            var tag_operate_input = document.createElement("input");
            var tag_operate_label = document.createElement("label");
            var input_div1 = document.createElement("div");
            var input_div2 = document.createElement("div");
            var input_div3 = document.createElement("div");
            var tags_tips = document.createTextNode(tags_provide_list[i]+"：");


            tag_operate_input_div.classList.add("rowElem");
            input_div1.classList.add("jqTransformInputWrapper");
            input_div1.classList.add("jqTransformSafari");
            input_div1.style = "width: 170px";
            input_div2.classList.add("jqTransformInputInner");
            tag_operate_input.type = "text";
            tag_operate_input.id = "data"+ i;
            tag_operate_input.setAttribute("onchange","savetags(this)");
            tag_operate_input.setAttribute("placeholder","请在这里输入");
            tag_operate_input.classList.add("jqtranformdone");
            tag_operate_input.classList.add("jqTransformInput");
            tag_operate_label.setAttribute("for","data"+i);
            tag_operate_label.setAttribute("style","width:100px;text-align: left;");

            tag_operate_input_div.appendChild(tag_operate_label);
            tag_operate_input_div.appendChild(input_div1);
            tag_operate_label.appendChild(tags_tips);
            input_div1.appendChild(input_div2);
            input_div2.appendChild(input_div3);
            input_div3.appendChild(tag_operate_input);

            tag_operate_div.appendChild(tag_operate_input_div);


        }

        var markerjob_json = markerjob;
        var pic_array = markerjob_json.picList;

        var picname = window.location.href.split("?")[1].split("=")[1];
        for (var i = 0;i<pic_array.length;i++){
            var picnametm = pic_array[i].split(".")[0];
            if (picname==picnametm){
                picname = pic_array[i];
                break;
            }
        }
        var pic_init_json = getMarkInfo(picname);

        console.log("拿到的json",pic_init_json);

        if(pic_init_json==""){                     //第一次打开这张图
            // loadMarkedHotArea(picname);
        }else {
            var tags_decision_json = pic_init_json.tags_decision_annotator;
            var txt_input = document.getElementsByClassName("jqTransformInput");

            for (var i = 0;i<txt_input.length;i++){
                console.log(4,txt_input[i].value);
                var tags_tip = $('label[for="'+ txt_input[i].id +'"]').text();
                // console.log(tags_tip)
                for(var tag in tags_decision_json){
                    if(tags_tip== tag){
                        txt_input[i].value = tags_decision_json[tag];
                    }

                }
            // console.log(txt_input.value);
                console.log(4,txt_input[i].value);
            }
            console.log("past",json_classify_saved);
            var coordinates_json = pic_init_json.coordinates;
            canvas_tag_ctx.lineWidth = 5;
            canvas_tag_ctx.strokeStyle = "#0099e5";
            canvas_tag_ctx.strokeRect(coordinates_json.start_x,coordinates_json.start_y,(coordinates_json.end_x-coordinates_json.start_x),(coordinates_json.end_y-coordinates_json.start_y));

            json_classify_saved = pic_init_json;
            console.log("now",json_classify_saved);


        }





    }else if(project_type==2){          //标框标注

        document.getElementById("pro_type").innerHTML = "标框标注";

        buttonData.$data.isRevert = true;
        // var parent_div = document.getElementById("tools");
        // var revoke_div1 = document.createElement("div");
        // revoke_div1.classList.add("like-button");
        //
        // var revoke_a = document.createElement("a");
        // revoke_a.classList.add("button");
        // revoke_a.id = "revoke";
        //
        // var revoke_a_text = document.createTextNode("撤销");
        //
        // revoke_a.appendChild(revoke_a_text);
        // revoke_div1.appendChild(revoke_a);
        //
        // parent_div.appendChild(revoke_div1);


        marks("rect");



        // var rubber = document.getElementById("rubber");
        // var pencil = document.getElementById("pencil");
        //
        //
        // rubber.parentNode.parentNode.parentNode.removeChild(rubber.parentNode.parentNode);
        // pencil.parentNode.parentNode.parentNode.removeChild(pencil.parentNode.parentNode);

        var color_rect_json = JSON.parse(project_detail.tagColor);
        // var tag_color = color_rect_json.
        console.log(color_rect_json)
        var tag_color_array = color_rect_json.tagcolor;

        var tags_operate_div =document.getElementById("tags_operate");
        for(var i= 0;i<tag_color_array.length;i++){
            var color_message_single = tag_color_array[i];
            var message_single = "";
            var color_single = "";
            // console.log(color_message_single);
            for (var message in color_message_single){
                console.log(message,color_message_single[message])
                message_single = message;
                color_single = color_message_single[message];
            }

            var radio_div = document.createElement("div");
            var radio_input = document.createElement("input");
            var radio_label = document.createElement("label");
            var radio_text = document.createTextNode(message_single);

            radio_div.classList.add("radio-check");
            // radio_div.value = color_single;
            radio_div.style = "    color: "+color_single+";"+"margin-left:16px";
            radio_input.type = "radio";
            radio_input.value = color_single;
            radio_input.classList.add("radio_input");
            radio_input.id = "radio"+i;
            radio_input.name = "test";
            radio_label.setAttribute("for","radio"+i);

            radio_label.appendChild(radio_text);
            radio_div.appendChild(radio_input);
            radio_div.appendChild(radio_label);
            if(i==0){
                radio_input.checked = true;
            }

            tags_operate_div.appendChild(radio_div);

        }

        var markerjob_json = markerjob;
        var pic_array = markerjob_json.picList;

        var picname = window.location.href.split("?")[1].split("=")[1];
        for (var i = 0;i<pic_array.length;i++){
            var picnametm = pic_array[i].split(".")[0];
            if (picname==picnametm){
                picname = pic_array[i];
                break;
            }
        }

        var pic_init_json = getMarkInfo(picname);
        console.log("拿到的json",pic_init_json);

        if(pic_init_json==""){                     //第一次打开这张图

        }else {
            var rects_array = pic_init_json.rects_info;
            for (var i = 0; i<rects_array.length;i++){
                canvas_tag_ctx.lineWidth = 5;
                canvas_tag_ctx.strokeStyle = rects_array[i].color;
                canvas_tag_ctx.strokeRect(rects_array[i].start_x,rects_array[i].start_y,(rects_array[i].end_x-rects_array[i].start_x),(rects_array[i].end_y- rects_array[i].start_y));

            }

            json_rect_saved = pic_init_json;
        }





    }else {                             //轮廓标注

        document.getElementById("pro_type").innerHTML = "轮廓标注";


        // var parent_div = document.getElementById("tools");
        //
        // // var rubber_div1 = document.createElement("div");
        // // rubber_div1.classList.add("like-button");
        // // var rubber_a = document.createElement("a");
        // // rubber_a.classList.add("button");
        // // rubber_a.id = "rubber";
        // // var rubber_a_text = document.createTextNode("橡皮擦");
        // // rubber_a.appendChild(rubber_a_text);
        // // rubber_div1.appendChild(rubber_a);
        // // parent_div.appendChild(rubber_div1);
        //
        // // var pencil_div1 = document.createElement("div");
        // // pencil_div1.classList.add("like-button");
        // // var pencil_a = document.createElement("a");
        // // pencil_a.classList.add("button");
        // // pencil_a.id = "pencil";
        // // var pencil_a_text = document.createTextNode("铅笔");
        // // pencil_a.appendChild(pencil_a_text);
        // // pencil_div1.appendChild(pencil_a);
        // // parent_div.appendChild(pencil_div1);
        //
        // var undo_div1 = document.createElement("div");
        // undo_div1.classList.add("like-button");
        // var undo_a = document.createElement("a");
        // undo_a.classList.add("button");
        // undo_a.id = "undo";
        // var undo_a_text = document.createTextNode("撤销");
        // undo_a.appendChild(undo_a_text);
        // undo_div1.appendChild(undo_a);
        // parent_div.appendChild(undo_div1);

        marks("border");


        // var revoke = document.getElementById("revoke");
        //
        //
        // revoke.parentNode.parentNode.parentNode.removeChild(revoke.parentNode.parentNode);

        var markerjob_json = markerjob;
        var pic_array = markerjob_json.picList;

        var picname = window.location.href.split("?")[1].split("=")[1];
        for (var i = 0;i<pic_array.length;i++){
            var picnametm = pic_array[i].split(".")[0];
            if (picname==picnametm){
                picname = pic_array[i];
                break;
            }
        }

        var pic_init_json = getMarkInfo(picname);
        console.log("拿到的json",pic_init_json);



        if(project_detail.tagState==0){             //什么都不做型
            if(pic_init_json==""){                     //第一次打开这张图

            }else {
                var stroke_array = pic_init_json.coordinates;
                for (var i = 0;i<stroke_array.length;i++){
                    canvas_tag_ctx.moveTo(stroke_array[i][0].x,stroke_array[i][0].y);canvas_tag_ctx.strokeStyle = "#0099e5";
                    canvas_tag_ctx.strokeStyle = "#0099e5";
                    canvas_tag_ctx.lineWidth = 5;
                    for (var j = 1;j<stroke_array[i].length;j++){
                        canvas_tag_ctx.lineTo(stroke_array[i][j].x,stroke_array[i][j].y);
                        canvas_tag_ctx.stroke();
                    }
                }

                json_border_saved = pic_init_json;
                limit_undo_num = json_border_saved.coordinates.length;
            }

        }else if(project_detail.tagState==2){

            var tags_operate = document.getElementById("tags_operate");
            var textarea_html = "                                        <div class=\"rowElem\">\n" +
                "                                            <label style=\"cursor: pointer;\">描述信息：</label>\n" +
                "                                            <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"jqTransformTextarea\">\n" +
                "                                                <tbody>\n" +
                "                                                <tr>\n" +
                "                                                    <td id=\"jqTransformTextarea-mm\" class=\"jqTransformSafariTextarea\">\n" +
                "                                                        <div style=\"height: 192px; width: 261px;\">\n" +
                "                                                            <textarea cols=\"40\" rows=\"12\" name=\"mytext\" class=\"jqtransformdone\" id=\"description_textarea\" onchange=\"savetextdiscription()\"></textarea>\n" +
                "                                                        </div>\n" +
                "                                                    </td>\n" +
                "                                                 </tr>\n" +
                "                                                </tbody>\n" +
                "                                            </table>\n" +
                "                                        </div>";
            tags_operate.innerHTML = textarea_html;


            if(pic_init_json==""){                     //第一次打开这张图

            }else {
                var stroke_array = pic_init_json.coordinates;
                for (var i = 0;i<stroke_array.length;i++){
                    canvas_tag_ctx.moveTo(stroke_array[i][0].x,stroke_array[i][0].y);
                    canvas_tag_ctx.strokeStyle = "#0099e5";
                    canvas_tag_ctx.lineWidth = 5;
                    for (var j = 1;j<stroke_array[i].length;j++){
                        canvas_tag_ctx.lineTo(stroke_array[i][j].x,stroke_array[i][j].y);
                        canvas_tag_ctx.stroke();
                    }
                }


                var pic_discription = pic_init_json.discription;

                var text_disc = document.getElementById("description_textarea");
                text_disc.innerHTML = pic_discription;


                json_border_saved = pic_init_json;

            }



        }else {

            var tag_provide_div = document.getElementById("classify_tags_provided");
            var tags_provide_list = project_detail.tags;
            for(var i = 0;i<tags_provide_list.length;i++){

                var tag_span = document.createElement("span");
                var tag_a = document.createElement("a");
                var tag_text = document.createTextNode(tags_provide_list[i]);

                tag_a.classList.add("tag");

                tag_span.appendChild(tag_a);
                tag_a.appendChild(tag_text);

                tag_provide_div.appendChild(tag_span);

            }

            var tag_operate_div = document.getElementById("tags_operate");
            for(var i = 0; i<tags_provide_list.length;i++){

                var tag_operate_input_div = document.createElement("div");
                var tag_operate_input = document.createElement("input");
                var tag_operate_label = document.createElement("label");
                var input_div1 = document.createElement("div");
                var input_div2 = document.createElement("div");
                var input_div3 = document.createElement("div");
                var tags_tips = document.createTextNode(tags_provide_list[i]);


                tag_operate_input_div.classList.add("rowElem");
                input_div1.classList.add("jqTransformInputWrapper");
                input_div1.classList.add("jqTransformSafari");
                input_div1.style = "width: 170px";
                input_div2.classList.add("jqTransformInputInner");
                tag_operate_input.type = "text";
                tag_operate_input.id = "data"+ i;
                tag_operate_input.setAttribute("onchange","savetags(this)")
                tag_operate_input.classList.add("jqtranformdone");
                tag_operate_input.classList.add("jqTransformInput");
                tag_operate_label.setAttribute("for","data"+i);

                tag_operate_input_div.appendChild(tag_operate_label);
                tag_operate_input_div.appendChild(input_div1);
                tag_operate_label.appendChild(tags_tips);
                input_div1.appendChild(input_div2);
                input_div2.appendChild(input_div3);
                input_div3.appendChild(tag_operate_input);

                tag_operate_div.appendChild(tag_operate_input_div);


            }

            if(pic_init_json==""){                     //第一次打开这张图

            }else {
                var stroke_array = pic_init_json.coordinates;
                for (var i = 0;i<stroke_array.length;i++){
                    canvas_tag_ctx.moveTo(stroke_array[i][0].x,stroke_array[i][0].y);canvas_tag_ctx.strokeStyle = "#0099e5";
                    canvas_tag_ctx.strokeStyle = "#0099e5";
                    canvas_tag_ctx.lineWidth = 5;
                    for (var j = 1;j<stroke_array[i].length;j++){
                        canvas_tag_ctx.lineTo(stroke_array[i][j].x,stroke_array[i][j].y);
                        canvas_tag_ctx.stroke();
                    }
                }

                var tags_decision_json = pic_init_json.tags_decision_annotator;
                var txt_input = document.getElementsByClassName("jqTransformInput");

                for (var i = 0;i<txt_input.length;i++){
                    console.log(4,txt_input[i].value);
                    var tags_tip = $('label[for="'+ txt_input[i].id +'"]').text();
                    // console.log(tags_tip)
                    for(var tag in tags_decision_json){
                        if(tags_tip== tag){
                            txt_input[i].value = tags_decision_json[tag];
                        }

                    }
                    // console.log(txt_input.value);
                    console.log(4,txt_input[i].value);
                }

                json_border_saved = pic_init_json;



            }




        }


    }





}

/**
 * 加载预览图
 *
 */
function loadOverView() {
    var markerjob_json = markerjob;



    var picL = markerjob_json.picList;
    var markedPicL = markerjob_json.markedPicList;
    var res = picL.filter(function (value) {
        return !(markedPicL.includes(value))
    })
    var res1 = res.concat(markedPicL);

    //修改全局 的markerjob 和 projectdetail
    markerjob.picList = res1;
    project_detail.picList = res1;


    var pic_array = res1;
    var ul = document.getElementById("mTS_1_container")
    // console.log(1,pic_array)
    for(var i = 0;i<pic_array.length;i++) {
        var src = "../" + "pic/" + window.location.href.split("?")[1].split("=")[0] + "/" + pic_array[i];


        var li = document.createElement("li")
        var a = document.createElement("a")
        var img = document.createElement("img");

        img.src = src;
        a.setAttribute("onclick","designatedPic(\""+pic_array[i].split(".")[0]+"\")") ;
        a.appendChild(img);
        li.appendChild(a);
        li.setAttribute("style","position: relative;")

        if(markedPicL.includes(pic_array[i])){
            var zhezhao = document.createElement("div");
            (function (ul,li,zhezhao,img,s) {
                img.onload = function (ev) {
                    var zhezhaoheight = img.height;
                    zhezhao.setAttribute("style","  top: 0px;left: 0px; width: 160px;height:100%;position: absolute;background: rgba(0, 0, 0, 0.7);border: 1px solid white;")
                    zhezhao.setAttribute("onclick","designatedPic(\""+s.split(".")[0]+"\")")
                    li.appendChild(zhezhao);
                    ul.appendChild(li);
                }

            })(ul,li,zhezhao,img,pic_array[i]);


        }else {
            ul.appendChild(li);
        }




    }
}


/**
 * 得到之前的热点区域并标注在当前画布上
 */
function loadMarkedHotArea(picname) {
    var detailmessage = window.location.href.split("?")[1];
    var pro_id = detailmessage.split("=")[0];
    var result;
    var userphonum = getUserPhone();
    $.ajax({
        url:'/hotTagArea',
        type:'post',
        async:false,
        data:{
            id:pro_id,
            pic:picname,
            userphone:userphonum
        },
        success:function (data) {
            result = data;
        },
        error:function () {
            alert("fail1");
        }

    });

    var rect_hot_json = JSON.parse(result);
    // console.log(result);
    // var rect_temp = {};
    // var rect_now_json = {};
    // for(var i = 0;i<tags_hot_area_array.length;i++){
    //     rect_now_json = JSON.parse(tags_hot_area_array[i]);
    //     if(i==0){
    //         rect_temp = rect_now_json.coordinates;
    //     }else {
    //         console.log("ssss"+rect_now_json);
    //         var start_x_now = rect_now_json.coordinates.start_x;
    //         var start_y_now = rect_now_json.coordinates.start_y;
    //         var end_x_now = rect_now_json.coordinates.end_x;
    //         var end_y_now = rect_now_json.coordinates.end_y;
    //         var start_x_temp = rect_temp.start_x;
    //         var start_y_temp = rect_temp.start_y;
    //         var end_x_temp = rect_temp.end_x;
    //         var end_y_temp = rect_temp.end_y;
    //
    //         rect_temp.start_x = (start_x_now>=start_x_temp)?start_x_now:start_x_temp;
    //         rect_temp.start_y = (start_y_now>=start_y_temp)?start_y_now:start_y_temp;
    //         rect_temp.end_x = (end_x_now<=end_x_temp)?end_x_now:end_x_temp;
    //         rect_temp.end_y = (end_y_now<=end_y_temp)?end_y_now:end_y_temp;
    //
    //     }
    //
    // }

    // var img = new Image();
    // img.src = canvas_img.toDataURL();
    // canvas_img_ctx.clearRect(0,0,canvas_img.width,canvas_img.height);
    // canvas_img_ctx.drawImage(img,0,0);
    //
    if(JSON.stringify(rect_hot_json)!='{}'){
        canvas_img_ctx.fillStyle = "rgba(0,255,0,0.2)";
        canvas_img_ctx.beginPath();
        canvas_img_ctx.rect(rect_hot_json.sx,rect_hot_json.sy,rect_hot_json.ex-rect_hot_json.sx,rect_hot_json.ey-rect_hot_json.sy);
        canvas_img_ctx.fill();
        canvas_img_ctx.closePath();
    }
    // loadPicture(rect_temp.start_x,rect_temp.start_y,rect_temp.end_x-rect_temp.start_x,rect_temp.end_y-rect_temp.start_y);


}

var buttonData = new Vue({
    el: '#details',
    data: {
        isRevert:false

    },
    methods:{
        revert:function(){

        },
        lastPi:function () {
            var res = previousPic();
            if(res=="first") {
                this.$message.warning("这已经是最开始的一张图了")
            }
        },
        nextPi:function () {
            var res = nextPic();
            if(res=="final") {
                this.$message.warning("这已经是最后的一张图了")
            }
        }
    },
    mounted:function () {

    }
})
var returnData = new Vue({
    el: '#returnP',
    data: {},
    methods:{
        returnPro:function () {
            returnProject();
        }
    }
});
var progressData = new Vue({
    el: '#progressPro',
    data: {
        progress_P:60
    },
    methods:{}

    });
progressData.$data.progress_P = parseFloat(((markerjob.markedPicList.length / markerjob.picList.length) * 100).toFixed(2));
