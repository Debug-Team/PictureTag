
function initCreditsExchange() {
    var marker_json = {};
    var phonenum = getUserPhone();
    $.ajax({
        url:'/getMarkerDetail',
        type:'post',
        async:false,
        data:{
            phonenum:phonenum
        },
        success:function (data) {
            marker_json = JSON.parse(data);
        },
        error:function () {
            alert("fail")
        }
    });


    document.getElementById("current_creits").innerText = "当前积分为"+marker_json.credits;
    var text = document.getElementById("credits_exchange");
    text.onkeyup = function(){
        this.value=this.value.replace(/\D/g,'');
        if(text.value>marker_json.credits){
            text.value = marker_json.credits;
        }
    }



}

function markerExchange() {
    var userphone = getUserPhone();
    var credits_exchange = document.getElementById("credits_exchange").value;
    var result = {};

    if(credits_exchange!=""){
        credits_exchange = Number(credits_exchange);
        $.ajax({
            url:'/markerExchange',
            type:'post',
            async:false,
            data:{
                userphone:userphone,
                amounts:credits_exchange
            },
            success:function (data) {
                result = JSON.parse(data);
                window.location.reload();
            },
            error:function () {
                alert("fail")
            }
        });
    }

    // console.log(resu)
}