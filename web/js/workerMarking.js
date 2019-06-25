/**
 * @Author: 周正伟
 * 标注者整体标注完后保存的数据json
 * @param coordinates 坐标信息
 * @param tags_choices_uploader 由上传者决定的tag类型和其选项（数组形式）
 * @param tags_decision_annotator 标注者选择的tag
 */
json_classify_saved= {
    "coordinates": {
    },
    "tags_decision_annotator": {

    }

}

/**
 * @Author: 周正伟
 * 标注者标框标注完后保存的数据json
 * @param rect_info 数组用来存包含“每个矩形的起始””结束坐标“ “颜色信息”属性的json对象
 * 形如  {
    "rects_info": [
            {
            "start_x": 0,
            "start_y": 0,
            "end_x": 0,
            "end_y": 0,
            "color": "black"
            }
                   ]
           }
 */
json_rect_saved= {
    "rects_info": []
}

/**
 * @Author: 周正伟
 * 标注者轮廓标注完后保存的数据json
 * 形如 {
    "coordinates": [
        [
            {
                "x": 1,
                "y": 2
            },
            {
                "x": 3,
                "y": 4
            }
        ],
        [
            {
                "x": 1,
                "y": 2
            },
            {
                "x": 3,
                "y": 4
            }
        ]
    ],
    "discription": "",
    "tags_decision_annotator": {}
}
 * @param coordinates 二维数组对象，该数组是存包含坐标信息json的数组的数组
 * @param discription 上传者要求描述时的描述信息
 * @param tags_decision_annotator 上传者要求填写属性时的属性信息
 *
 */

json_border_saved= {
    "coordinates":[],
    "discription":"",
    "tags_decision_annotator": {

    }

}


var canvas_img = document.getElementById("canvas_img");
var canvas_img_ctx = canvas_img.getContext("2d");
var canvas_tag = document.getElementById("canvas_tag");
var canvas_tag_ctx = canvas_tag.getContext("2d");
var test11 = canvas_tag.getContext("2d");
var limit_undo_num;




var marks = function (type) {

    // var rubber = document.getElementById("rubber");
    var revoke = document.getElementById("revoke");         //裱框標注的撤銷
    // var pencil = document.getElementById("pencil");
    var undo = document.getElementById("undo");             //轮廓标注的撤销


    if(type =="classify"){
        canvas_tag.style.cssText = "cursor: url(\"../image/drug.png\"),default;";

    }else if(type =="rect"){
        canvas_tag.style.cssText = "cursor: url(\"../image/drug.png\"),default;";

    }else {

        canvas_tag.style.cssText = "cursor: url(\"../image/pencil.png\"),default;";

    }
    // console.log(pencil,revoke);

    var working_state;
    // console.log(0)
    var originX;
    var originY;

    var rect_single = {} //标框标注用保存每个框的信息

    var coordinates_one_stroke = [];

    var color_rect = "";

    var border_pic_array = new Array();  //轮廓标注用于记录之前canvasurl的

    var border_last_coordinates = new Array(); //搭配实现撤销功能

    var img_temp = new Image();  //标框标注用于保存上次mouseup事件前所画的矩形集


    var start_x = -1;
    var start_y = -1;
    var last_end_x = -1;
    var last_end_y = -1;

    canvas_tag.addEventListener('mousedown',function(event){
        // console.log(1)
        working_state = true;
        switch (type){
            case "classify":
                originX = event.offsetX;
                originY = event.offsetY;

                canvas_tag_ctx.moveTo(originX,originY);
                canvas_tag_ctx.strokeStyle = "#0099e5";
                canvas_tag_ctx.lineWidth = 5;
                json_classify_saved.coordinates.start_x = originX;
                json_classify_saved.coordinates.start_y = originY;

                break;
            case "rect":

                img_temp.src = canvas_tag.toDataURL('image.png');//保存上次所画矩形
                originX = event.offsetX;
                originY = event.offsetY;



                color_rect = getcolor();
                if(color_rect==""){
                    working_state = false;
                    console.log("还没选颜色");
                }else {

                    canvas_tag_ctx.moveTo(originX, originY);
                    canvas_tag_ctx.strokeStyle = color_rect;
                    canvas_tag_ctx.lineWidth = 5;
                    console.log(color_rect)

                    rect_single.start_x = originX;
                    rect_single.start_y = originY;
                    // canvas_tag_ctx.strokeStyle = color   //根据所选颜色决定所画矩形颜色
                }
                break;
            case "border":
                // if(img_temp.src!=""){
                //     canvas_tag_ctx.clearRect(0, 0, canvas_tag.width,canvas_tag.height);
                //     canvas_tag_ctx.drawImage(img_temp, 0, 0);       //再画时再把上次画的结果先加载
                //     console.log("sss")
                // }
                originX = event.offsetX;
                originY = event.offsetY;

                canvas_tag_ctx.beginPath();
                canvas_tag_ctx.moveTo(originX, originY);
                canvas_tag_ctx.strokeStyle = "#0099e5";
                canvas_tag_ctx.lineWidth = 5;

                var coordinates_single_start = {};
                coordinates_single_start.x = originX;
                coordinates_single_start.y = originY;
                coordinates_one_stroke.push(coordinates_single_start);
                coordinates_single_start = {};
                console.log(coordinates_one_stroke)

                if(last_end_x<0){
                    start_x = originX;
                    start_y = originY;
                }
                break;
            // case "rubber":
            //     // canvas_tag_ctx.beginPath();
            //     break;

        }
    },false);

    canvas_tag.addEventListener('mousemove',function(event){
        if (working_state){
            switch (type){
                case "classify":

                    var x = event.offsetX;
                    var y = event.offsetY;
                    var newOriginX  = originX;
                    var newOriginY = originY;

                    canvas_tag_ctx.clearRect(0, 0, canvas_tag.width,canvas_tag.height);
                    canvas_tag_ctx.beginPath();

                    if (x < originX) {
                        newOriginX = x;
                    }
                    if (y < originY) {
                        newOriginY = y;
                    }
                    canvas_tag_ctx.rect(newOriginX, newOriginY, Math.abs(x - originX), Math.abs(y - originY));
                    // console.log(startx = newOriginX,
                    // starty = newOriginY,
                    // endx = newOriginX + Math.abs(x - originX),
                    // endy = newOriginY +  Math.abs(y - originY));
                    canvas_tag_ctx.stroke();
                    canvas_tag_ctx.closePath();

                    break;
                case "rect":

                    var x = event.offsetX;
                    var y = event.offsetY;
                    var newOriginX  = originX;
                    var newOriginY = originY;

                    canvas_tag_ctx.clearRect(0, 0, canvas_tag.width,canvas_tag.height);
                    canvas_tag_ctx.drawImage(img_temp, 0, 0);       //再画时再把上次画的结果先加载
                    canvas_tag_ctx.beginPath();

                    if (x < originX) {
                        newOriginX = x;
                    }
                    if (y < originY) {
                        newOriginY = y;
                    }
                    canvas_tag_ctx.rect(newOriginX, newOriginY, Math.abs(x - originX), Math.abs(y - originY));
                    // startx = newOriginX;
                    // starty =newOriginY;
                    // endx = newOriginX + Math.abs(x - originX) ;
                    // endy = newOriginY + Math.abs(y - originY) ;
                    canvas_tag_ctx.stroke();
                    canvas_tag_ctx.closePath();

                    break;
                case "border":

                    var x = event.offsetX;
                    var y = event.offsetY;
                    // var newOriginX  = originX;
                    // var newOriginY = originY;

                    canvas_tag_ctx.lineTo(x,y);
                    canvas_tag_ctx.stroke();

                    var coordinates_single_move = {};
                    coordinates_single_move.x = x;
                    coordinates_single_move.y = y;
                    coordinates_one_stroke.push(coordinates_single_move);
                    coordinates_single_move = {};
                    // console.log(coordinates_one_stroke)

                    break;
                // case "rubber":
                //     var x = event.offsetX;
                //     var y = event.offsetY;
                //
                //
                //     canvas_tag_ctx.clearRect(x-10,y-10,20,20);
                //
                //     var stroke_array = json_border_saved.coordinates;
                //     for (var i = 0;i<stroke_array.length;i++){
                //         for (var j = 0;j<stroke_array[i].length;j++){
                //             if ((stroke_array[i][j].x>=(x-10))&&(stroke_array[i][j].x<=(x+10))&&(stroke_array[i][j].y>=(y-10))&&(stroke_array[i][j].y<=(y+10))){
                //                 stroke_array[i][j].x = 0;
                //                 stroke_array[i][j].y = 0;
                //             }
                //         }
                //     }
                //     break;

            }
        }

    },false);

    canvas_tag.addEventListener('mouseup', function (event) {
        if (working_state){
            switch (type){
                case "classify":
                    json_classify_saved.coordinates.end_x = event.offsetX;
                    json_classify_saved.coordinates.end_y = event.offsetY;
                    console.log(json_classify_saved)

                    working_state = false;

                    break;
                case "rect":
                    rect_single.end_x = event.offsetX;
                    rect_single.end_y = event.offsetY;
                    rect_single.color = color_rect;
                    // console.log("meici",rect_single)


                    json_rect_saved.rects_info.push(rect_single);
                    // console.log("fi",json_rect_saved)
                    rect_single = {};                        //不清空出大问题
                    working_state = false;



                    break;
                case "border":
                    // console.log(coordinates_one_stroke)

                    var x = event.offsetX;
                    var y = event.offsetY;
                    // var newOriginX  = originX;
                    // var newOriginY = originY;

                    canvas_tag_ctx.lineTo(x,y);
                    canvas_tag_ctx.stroke();

                    var coordinates_single_move = {};
                    coordinates_single_move.x = x;
                    coordinates_single_move.y = y;
                    coordinates_one_stroke.push(coordinates_single_move);
                    coordinates_single_move = {};

                    canvas_tag_ctx.closePath();
                    json_border_saved.coordinates.push(coordinates_one_stroke);
                    coordinates_one_stroke = [];
                    // console.log("qian",json_border_saved)
                    // console.log(JSON.stringify(json_border_saved));
                    working_state = false;
                    // console.log(1)
                    // canvas_tag_ctx.moveTo(json_border_saved.coordinates[0][0].x-100,json_border_saved.coordinates[0][0].y)
                    // canvas_tag_ctx.strokeStyle = "#0099e5";
                    // canvas_tag_ctx.lineWidth = 5;
                    // for(var i = 1;i<json_border_saved.coordinates[0].length;i++){
                    //         canvas_tag_ctx.lineTo(json_border_saved.coordinates[0][i].x-100,json_border_saved.coordinates[0][i].y);
                    //     canvas_tag_ctx.stroke();
                    //     console.log(2)
                    // }
                    // console.log(3)

                    // canvas_tag_ctx.imageSmoothingEnabled = true;
                    if(last_end_x>0){

                        var coordinates_single_move = {};


                        var k = (last_end_y - originY)/(last_end_x-originX);
                        var b = originY - k * originX;
                        // console.log(k,b);
                        canvas_tag_ctx.beginPath();                    // 首尾相连
                        var max_x = last_end_x>originX?last_end_x:originX;
                        var min_x = last_end_x<originX?last_end_x:originX;
                        canvas_tag_ctx.moveTo(min_x,k*min_x+b);

                        coordinates_single_move.x = min_x;
                        coordinates_single_move.y = k*min_x+b;
                        coordinates_one_stroke.push(coordinates_single_move);
                        coordinates_single_move = {};


                        canvas_tag_ctx.strokeStyle = "#0099e5";
                        canvas_tag_ctx.lineWidth = 5;
                        for(var i = min_x; i<=max_x;i++){
                            canvas_tag_ctx.lineTo(i,k*i+b);
                            canvas_tag_ctx.stroke();

                            coordinates_single_move.x = i;
                            coordinates_single_move.y = k*i+b;
                            coordinates_one_stroke.push(coordinates_single_move);
                            coordinates_single_move = {};

                        }
                        canvas_tag_ctx.closePath();

                        json_border_saved.coordinates.push(coordinates_one_stroke);
                        coordinates_one_stroke = [];


                    }

                    last_end_x = event.offsetX;
                    last_end_y = event.offsetY;

                   if(last_end_x!= start_x){

                       var len  = Math.pow(Math.pow(last_end_x-start_x,2)+Math.pow(last_end_y-start_y,2),1/2);
                       if(len<40){

                           var coordinates_single_move = {};

                           var k = (last_end_y - start_y)/(last_end_x-start_x);
                           var b = start_y - k * start_x;
                           // console.log(k,b);
                           canvas_tag_ctx.beginPath();                    // 首尾相连
                           var max_x = last_end_x>start_x?last_end_x:start_x;
                           var min_x = last_end_x<start_x?last_end_x:start_x;
                           canvas_tag_ctx.moveTo(min_x,k*min_x+b);
                           canvas_tag_ctx.strokeStyle = "#0099e5";
                           canvas_tag_ctx.lineWidth = 5;

                           coordinates_single_move.x = min_x;
                           coordinates_single_move.y = k*min_x+b;
                           coordinates_one_stroke.push(coordinates_single_move);
                           coordinates_single_move = {};

                           for(var i = min_x; i<=max_x;i++){
                               canvas_tag_ctx.lineTo(i,k*i+b);
                               canvas_tag_ctx.stroke();

                               coordinates_single_move.x = i;
                               coordinates_single_move.y = k*i+b;
                               coordinates_one_stroke.push(coordinates_single_move);
                               coordinates_single_move = {};


                           }
                           canvas_tag_ctx.closePath();
                           json_border_saved.coordinates.push(coordinates_one_stroke);
                           coordinates_one_stroke = [];

                           type = "closed";
                       }
                   }

                   border_pic_array.push(canvas_tag.toDataURL("image.png"));

                   var last_coordinates_temp = {};
                   last_coordinates_temp.x = last_end_x;
                   last_coordinates_temp.y = last_end_y;

                   border_last_coordinates.push(last_coordinates_temp);

                     break;
                // case "rubber":
                //     // img_temp.src = canvas_tag.toDataURL('image.png');//保存上次所画边界
                //     // canvas_tag_ctx.closePath();
                //     var coordinates_temp = [];
                //     var coordinates_one_stroke_temp = [];
                //
                //     var stroke_array = json_border_saved.coordinates;
                //     for (var i = 0;i<stroke_array.length;i++){
                //         for (var j = 0;j<stroke_array[i].length;j++){
                //             if (stroke_array[i][j].x==0&&stroke_array[i][j].y==0&&coordinates_one_stroke_temp.length!=0){
                //                 coordinates_temp.push(coordinates_one_stroke_temp);
                //                 coordinates_one_stroke_temp = [];
                //             }
                //             if(stroke_array[i][j].x!=0&&stroke_array[i][j].y!=0){
                //                 var single_coordinate = {};
                //                 single_coordinate["x"] = stroke_array[i][j].x;
                //                 single_coordinate["y"] = stroke_array[i][j].y;
                //                 coordinates_one_stroke_temp.push(single_coordinate);
                //                 single_coordinate = {};
                //             }
                //         }
                //         if(coordinates_one_stroke_temp.length!=0){
                //             coordinates_temp.push(coordinates_one_stroke_temp);
                //         }
                //         coordinates_one_stroke_temp = [];
                //     }
                //
                //     json_border_saved.coordinates = coordinates_temp;
                //
                //     // var stroke_arrayss = json_border_saved.coordinates;
                //     // for (var i = 0;i<stroke_arrayss.length;i++){
                //     //     canvas_tag_ctx.moveTo(stroke_arrayss[i][0].x-100,stroke_arrayss[i][0].y);
                //     //     canvas_tag_ctx.strokeStyle = "#0099e5";
                //     //     canvas_tag_ctx.lineWidth = 5;
                //     //     for (var j = 1;j<stroke_arrayss[i].length;j++){
                //     //         canvas_tag_ctx.lineTo(stroke_arrayss[i][j].x-100,stroke_arrayss[i][j].y);
                //     //         canvas_tag_ctx.stroke();
                //     //     }
                //     // }
                //
                //     working_state = false;
                //     break;

            }
        }
    },false);

    // if(rubber!=null){
    //     rubber.addEventListener('click',function () {
    //         type = "rubber";
    //
    //         // canvas_tag.style.cssText = "cursor: url(\"../image/rubber.png\"),default;";
    //         // canvas_tag_ctx.clearRect(0, 0, canvas_tag.width,canvas_tag.height);
    //
    //     },false)
    // }

    if(revoke!=null){
        revoke.addEventListener('click',function () {
            json_rect_saved.rects_info.pop();
            // console.log("nooww",json_rect_saved)
            var rects_saved_array = json_rect_saved.rects_info;
            canvas_tag_ctx.clearRect(0, 0, canvas_tag.width,canvas_tag.height);
            for(var i = 0;i<rects_saved_array.length;i++){
                canvas_tag_ctx.strokeStyle = rects_saved_array[i].color;
                canvas_tag_ctx.strokeRect(rects_saved_array[i].start_x,rects_saved_array[i].start_y,(rects_saved_array[i].end_x-rects_saved_array[i].start_x),(rects_saved_array[i].end_y-rects_saved_array[i].start_y));

            }

        },false)
    }

    if(undo!=null){
        undo.addEventListener('click',function () {
            // console.log("sss")
            canvas_tag_ctx.clearRect(0, 0, canvas_tag.width,canvas_tag.height);
            var img_last = new Image();
            if(border_pic_array.length>=2){


                img_last.onload = function (ev) {
                    canvas_tag_ctx.drawImage(img_last,0,0);
                }
                img_last.src = border_pic_array[border_pic_array.length-2];
                border_pic_array.pop();
                border_last_coordinates.pop();
                last_end_x = border_last_coordinates[border_last_coordinates.length-1].x;
                last_end_y = border_last_coordinates[border_last_coordinates.length-1].y;


            }else {
                border_pic_array.pop();
                border_last_coordinates.pop();
                start_x = -1;
                start_y = -1;
                last_end_x = -1;
                last_end_y = -1;

            }
            console.log("s"+limit_undo_num);
            console.log("ss"+json_border_saved.coordinates.length);
            if(limit_undo_num == json_border_saved.coordinates.length){
                json_border_saved.coordinates = [];
                limit_undo_num == 100;
            }else {
                if(type=="closed"){
                    type = "border";
                    json_border_saved.coordinates.pop();
                    json_border_saved.coordinates.pop();
                    json_border_saved.coordinates.pop();

                }else {
                    json_border_saved.coordinates.pop();
                    json_border_saved.coordinates.pop();
                }
            }




        },false)
    }

    // if(pencil!=null){
    //     pencil.addEventListener('click',function () {
    //         type = "border";
    //         canvas_tag.style.cssText = "cursor: url(\"../image/pencil.png\"),default;";
    //         test11.beginPath();
    //         test11.moveTo(200, 200);
    //         test11.strokeStyle = "#0099e5";
    //         test11.lineWidth = 5;
    //         test11.lineTo(500,500);
    //         test11.closePath();
    //     },false)
    // }

    


}

function getcolor() {
    var radio_input = document.getElementsByClassName("radio_input");
    var color_result = "";
    for (var i = 0;i<radio_input.length;i++){
        if (radio_input[i].checked){
            console.log(radio_input[i].value)
            color_result = radio_input[i].value;
        }
    }
    console.log("ss",color_result);
    return color_result;
}

function savetags(obj) {
    // console.log(1)
    var tags_tip = $('label[for="'+ obj.id +'"]').text();
    // console.log(tags_tip);
    if(project_detail.type==1) {
        json_classify_saved.tags_decision_annotator[tags_tip] = obj.value;
    }else {
        json_border_saved.tags_decision_annotator[tags_tip] = obj.value;

    }
    // console.log(json_classify_saved)
}

function savetextdiscription() {
    json_border_saved.discription = document.getElementById("description_textarea").value;

}

function nextPic() {
    saveMarkWork();
    var pic_array_all = project_detail.picList;
    var present_pic = window.location.href.split("=")[1];
    var target_pic = "";
    var last_pic = false;
    for(var i = 0;i<pic_array_all.length;i++){
        if(pic_array_all[i].split(".")[0]==present_pic){
            if(i!=(pic_array_all.length-1)){
                target_pic = pic_array_all[i+1].split(".")[0];
            }else {
                last_pic = true;
            }
            break;
        }
    }
    if(!last_pic){
        window.location.href = window.location.href.split("=")[0]+"="+target_pic;
        return "next";
    }else {
        // alert("这已经是最后一张图了");
        return "final";
    }

}

function previousPic() {
    saveMarkWork();
    var pic_array_all = project_detail.picList;
    var present_pic = window.location.href.split("=")[1];
    var target_pic = "";
    var first_pic = false;
    for(var i = 0;i<pic_array_all.length;i++){
        if(pic_array_all[i].split(".")[0]==present_pic){
            if(i!=0){
                target_pic = pic_array_all[i-1].split(".")[0];
            }else {
                first_pic = true;
            }
            break;
        }
    }
    if(!first_pic){
        window.location.href = window.location.href.split("=")[0]+"="+target_pic;
        return "last";
    }else {
        return "first";
    }

}

function designatedPic(pic_id) {
    saveMarkWork();
    window.location.href = window.location.href.split("=")[0]+"="+ pic_id;
}

function saveMarkWork() {
    end_time = new Date();
    var work_time = end_time -start_time;

    var phonenum = getUserPhone();
    var pro_id = window.location.href.split("?")[1].split("=")[0];

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


    var dataString = "";
    var work_none = false;
    if (project_detail.type ==1){
        if(jQuery.isEmptyObject(json_classify_saved.coordinates)){
           if(jQuery.isEmptyObject(json_classify_saved.tags_decision_annotator)){
               console.log("整体空")
               work_none = true;
           }
        }
        dataString = JSON.stringify(json_classify_saved);
    }else if(project_detail.type ==2) {
        if(json_rect_saved.rects_info.length==0){
            console.log("标框空")
            work_none = true;
        }
        dataString = JSON.stringify(json_rect_saved);
    }else {
         if(json_border_saved.coordinates.length==0){
            if(json_border_saved.discription==""){
                if(jQuery.isEmptyObject(json_border_saved.tags_decision_annotator)){
                    console.log("轮廓空")
                    work_none = true;
                }
            }
        }
        dataString = JSON.stringify(json_border_saved);
    }


   if(!work_none){
       $.ajax({
           url:'/updatePictureTag',
           type:'post',
           async:false,
           data:{
               phonenum:phonenum,
               id:pro_id,
               pic:picname,
               data:dataString,
               workTime:work_time

           },
           success:function (data) {
               // alert(JSON.parse(data).retMessage)
           },
           error:function () {
               alert("fail");
           }

       });
   }
}

function returnProject() {
    window.location.href = "../html/projectOverview.html?"+ markerjob.id;
}

