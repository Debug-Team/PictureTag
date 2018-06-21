function getMedal() {
    var phonenum = getUserPhone();
    var result={};
    $.ajax({
        url:'/getMarkerMedals',
        type:'post',
        async:false,
        data:{
            userphone:phonenum
        },
        success:function (data) {
            result = JSON.parse(data);
        },
        error:function () {
            alert("fail1")
        }
    });
    return result;
}
function loadMedalDetail() {
    var medals_json = getMedal();
    var medals_array = medals_json.medals;
    for(var i = 0;i<medals_array.length;i++){
        initMedalDetail(i,medals_array[i].neededNum);
        // initMedalDetail(i,40+10*i);
    }
}

function initMedalDetail(i,neededNum){

        ;(function(){
            var mamture_progress = $('.mature-progress-box-detail');
            var l = 0;
            var _number = neededNum;
            var timer = null;
            var pro = 0;

            if ( _number < 5 ) {
                lad(_number, 5, '.progress-box-1',function(){
                    $('.progress-box-1').eq(i).append('<span>已标注'+_number+'</span>');
                });
            };

            if ( _number >= 5 && _number < 50  ) {
                lad(5, 5, '.progress-box-1', function(){
                    mamture_progress.eq(i).addClass('v1');
                    lad(_number-5, 50-5, '.progress-box-2',function(){
                        $('.progress-box-2').eq(i).addClass('active');
                        $('.progress-box-2').eq(i).append('<span>已标注'+_number+'</span>');
                    });
                });
            };

            if ( _number >= 50 && _number < 500  ) {
                lad(5, 5, '.progress-box-1', function(){
                    mamture_progress.eq(i).addClass('v1')
                    lad(50, 50, '.progress-box-2',function(){
                        mamture_progress.eq(i).addClass('v2');
                        lad(_number-50, 500-50, '.progress-box-3',function(){
                            $('.progress-box-3').eq(i).addClass('active');
                            $('.progress-box-3').eq(i).append('<span>已标注'+_number+'</span>');
                        })
                    });
                });
            };

            if ( _number >= 500 && _number < 5000  ) {
                lad(5, 5, '.progress-box-1', function(){
                    mamture_progress.eq(i).addClass('v1')
                    lad(50, 50, '.progress-box-2',function(){
                        mamture_progress.eq(i).addClass('v2')
                        lad(500, 500, '.progress-box-3', function(){
                            mamture_progress.eq(i).addClass('v3')
                            lad(_number-500, 5000-500, '.progress-box-4',function(){
                                $('.progress-box-4').eq(i).addClass('active');
                                $('.progress-box-4').eq(i).append('<span>已标注'+_number+'</span>');
                            })
                        })
                    });
                });
            };

            if ( _number >= 5000) {
                lad(5, 5, '.progress-box-1', function(){
                    mamture_progress.eq(i).addClass('v1')
                    lad(50, 50, '.progress-box-2',function(){
                        mamture_progress.eq(i).addClass('v2')
                        lad(500, 500, '.progress-box-3', function(){
                            mamture_progress.eq(i).addClass('v3')
                            lad(5000, 5000, '.progress-box-4',function(){
                                mamture_progress.eq(i).addClass('v4')
                                lad(_number-5000,10000,'.progress-box-5')
                            })
                        })
                    });
                });
            };

            /*
             @number : 成长值
             @max : 最大值
             @callback : 回调方法
             */
            function lad(number, max, cls, callback){
                l = 0;
                timer = setInterval(function(){
                    if ( number <= 5 ) {
                        l++;
                    }else if( number > 5 && number <= 50 ){
                        l+=5;
                    }else if( number > 50 && number <= 500 ){
                        l+=10;
                    }else if( number > 500 && number <= 5000 ){
                        l+=20;
                    }else{
                        l+=30;
                    };

                    pro = (l/max)*100;				//100为  div的长度
                    if ( l >= number ) {
                        clearInterval(timer);
                        if ( callback ) callback();   //回调
                    };
                    $(cls).eq(i).css({
                        width : pro+'px'
                    })
                },1)
            }
        })();


}