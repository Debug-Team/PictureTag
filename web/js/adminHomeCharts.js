initWholeProjectPie();
initDoingProjectPie();
initFinishedProjectPie();
initTagAndWorkerNumChart();
setTagAndWorkerNumChart(1);

/**
 * 初始化总任务统计图
 */
function initWholeProjectPie() {
    //todo 变量名问题
    var category = projectData.category_total;
    var rectangle = projectData.rect_total;
    var border = projectData.border_total;
    // var category = 3;
    // var rectangle = 4;
    // var border = 5;
    var dom = document.getElementById("wholeProjectPie");
    var myChart = echarts.init(dom);

    option = null;
    option = {
        backgroundColor: 'transparent',

        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },


        series : [
            {
                name:'总任务三类标注统计',
                type:'pie',
                radius : '55%',
                center: ['50%', '50%'],
                data:[
                    {
                        value:category,
                        name:'整体标注',
                        itemStyle: {
                            color: '#55efc4'
                        }
                    },
                    {
                        value:rectangle,
                        name:'标框标注',
                        itemStyle: {
                            color: '#81ecec'
                        }
                    },
                    {
                        value:border,
                        name:'区域标注',
                        itemStyle: {
                            color: '#74b9ff'
                        }
                    }
                ].sort(function (a, b) { return a.value - b.value; }),
                roseType: 'radius',
                label: {
                    normal: {
                        textStyle: {
                            color: 'black'
                        }
                    }
                },
                labelLine: {
                    normal: {
                        lineStyle: {
                            color: 'black'
                        },
                        smooth: 0.2,
                        length: 30,
                        length2: 30
                    }
                },
                itemStyle: {
                    normal: {
                        color: '#279bc2',
                        shadowBlur: 200,
                        shadowColor: 'grey'
                    }
                },

                animationType: 'scale',
                animationEasing: 'elasticOut',
                animationDelay: function (idx) {
                    return Math.random() * 200;
                }
            }
        ]
    };

    if (option && typeof option === "object") {
        myChart.setOption(option, true);
    }
}

/**
 * 初始化进行中任务统计图
 */
function initDoingProjectPie() {
    //todo 变量名问题
    var category = projectData.category_running;
    var rectangle = projectData.rect_running;
    var border = projectData.border_running;
    // var category = 3;
    // var rectangle = 4;
    // var border = 5;
    var dom = document.getElementById("doingProjectPie");
    var myChart = echarts.init(dom);

    option = null;
    option = {
        backgroundColor: 'transparent',

        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },


        series : [
            {
                name:'进行中任务三类标注统计',
                type:'pie',
                radius : '55%',
                center: ['50%', '50%'],
                data:[
                    {
                        value:category,
                        name:'整体标注',
                        itemStyle: {
                            color: '#55efc4'
                        }
                    },
                    {
                        value:rectangle,
                        name:'标框标注',
                        itemStyle: {
                            color: '#81ecec'
                        }
                    },
                    {
                        value:border,
                        name:'区域标注',
                        itemStyle: {
                            color: '#74b9ff'
                        }
                    }
                ].sort(function (a, b) { return a.value - b.value; }),
                roseType: 'radius',
                label: {
                    normal: {
                        textStyle: {
                            color: 'black'
                        }
                    }
                },
                labelLine: {
                    normal: {
                        lineStyle: {
                            color: 'black'
                        },
                        smooth: 0.2,
                        length: 30,
                        length2: 30
                    }
                },
                itemStyle: {
                    normal: {
                        color: '#279bc2',
                        shadowBlur: 200,
                        shadowColor: 'grey'
                    }
                },

                animationType: 'scale',
                animationEasing: 'elasticOut',
                animationDelay: function (idx) {
                    return Math.random() * 200;
                }
            }
        ]
    };

    if (option && typeof option === "object") {
        myChart.setOption(option, true);
    }
}

/**
 * 初始化已完成任务统计图
 */
function initFinishedProjectPie() {
    //todo 变量名问题
    var category = projectData.category_closed;
    var rectangle = projectData.rect_closed;
    var border = projectData.border_closed;
    // var category = 3;
    // var rectangle = 4;
    // var border = 5;
    var dom = document.getElementById("finishedProjectPie");
    var myChart = echarts.init(dom);

    option = null;
    option = {
        backgroundColor: 'transparent',

        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },


        series : [
            {
                name:'已完成任务三类标注统计',
                type:'pie',
                radius : '55%',
                center: ['50%', '50%'],
                data:[
                    {
                        value:category,
                        name:'整体标注',
                        itemStyle: {
                            color: '#55efc4'
                        }
                    },
                    {
                        value:rectangle,
                        name:'标框标注',
                        itemStyle: {
                            color: '#81ecec'
                        }
                    },
                    {
                        value:border,
                        name:'区域标注',
                        itemStyle: {
                            color: '#74b9ff'
                        }
                    }
                ].sort(function (a, b) { return a.value - b.value; }),
                roseType: 'radius',
                label: {
                    normal: {
                        textStyle: {
                            color: 'black'
                        }
                    }
                },
                labelLine: {
                    normal: {
                        lineStyle: {
                            color: 'black'
                        },
                        smooth: 0.2,
                        length: 30,
                        length2: 30
                    }
                },
                itemStyle: {
                    normal: {
                        color: '#279bc2',
                        shadowBlur: 200,
                        shadowColor: 'grey'
                    }
                },

                animationType: 'scale',
                animationEasing: 'elasticOut',
                animationDelay: function (idx) {
                    return Math.random() * 200;
                }
            }
        ]
    };

    if (option && typeof option === "object") {
        myChart.setOption(option, true);
    }
}

/**
 * 标注图片数量和对应标注者数量统计图
 * @param 拟合类型
 */
function initTagAndWorkerNumChart(){

    var dom = document.getElementById("tagAndWorkerNumChart");
    var myChart = echarts.init(dom);

    // var dataSrc = getUserNumCrossTagNums();
    // var data = [];
    // for(var i = 0;i < dataSrc.length;i++){
    //     var item = dataSrc[i];
    //     data.push([item.x, item.y]);
    // }
    // console.log(data);
    // var data = [
    //     [1, 4862.4],
    //     [2, 5294.7],
    //     [3, 5934.5],
    //     [4, 7171.0],
    //     [5, 8964.4],
    //     [6, 10202.2],
    //     [7, 11962.5],
    //     [8, 14928.3],
    //     [9, 16909.2],
    //     [10, 18547.9],
    //     [11, 21617.8],
    //     [12, 26638.1],
    //     [13, 34634.4],
    //     [14, 46759.4],
    //     [15, 58478.1],
    //     [16, 67884.6],
    //     [17, 74462.6],
    //     [18, 79395.7]
    // ];

    var option = {
        title: {
            // subtext: 'By ecStat.regression',
            left: 'center'
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross'
            }
        },
        xAxis: {
            name: '标注数量',
            type: 'value',
            splitLine: {
                lineStyle: {
                    type: 'dashed'
                }
            },
            minInterval: 1
        },
        yAxis: {
            name: '标注者数量',
            type: 'value',
            splitLine: {
                lineStyle: {
                    type: 'dashed'
                }
            },
            minInterval: 1
        },

    };

    // console.log(myRegression.points[myRegression.points.length - 1])

    if (option && typeof option === "object") {
        myChart.setOption(option, true);
    }

}


function setTagAndWorkerNumChart(type) {
    var chart = echarts.getInstanceByDom(document.getElementById("tagAndWorkerNumChart"));

    var dataSrc = getUserNumCrossTagNums();
    console.log(dataSrc);
    var data = [];
    // for(var i = 0;i < dataSrc.length;i++){
    for(var i = 0;i < dataSrc[0].length;i++){
        // var item = dataSrc[i];
        var item = dataSrc[0][i];
        data.push([item.x, item.y]);
    }
    console.log(data);

    // // See https://github.com/ecomfe/echarts-stat
    var myRegression = {
        points:[],
        expression:''
    };
    console.log("type:"+type)
    if(type == 1){
        var data1 = [];
        for(var i = 0; i < dataSrc[1].length;i++){
            var item = dataSrc[1][i];
            data1.push([item.x, item.y]);
        }
        console.log("data1:"+data1);
        myRegression.points = data1;
        // myRegression.points = dataSrc[1];
    }
    else if(type == 2){
        myRegression = ecStat.regression('linear', data);
    }
    else if(type == 3){
        // console.log("xx")

        myRegression = ecStat.regression('exponential', data);
    }
    else if(type == 4){
        // console.log("xx")
        myRegression = ecStat.regression('polynomial', data);
    }
    else if(type == 5){
        myRegression = ecStat.regression('logarithmic', data);
    }

    myRegression.points.sort(function(a, b) {
        return a[0] - b[0];
    });

    if(type == 2){      //线性需要是直线
        chart.setOption({
            series: [{
                // name: 'scatter',
                type: 'scatter',
                label: {
                    emphasis: {
                        show: true,
                        position: 'left',
                        textStyle: {
                            color: '#74b9ff',
                            fontSize: 16
                        }
                    }
                },
                data: data,
                itemStyle: {
                    color: '#74b9ff'
                }
            },{
                name: 'line',
                type: 'line',
                showSymbol: false,
                smooth: false,
                data: myRegression.points,
                lineStyle:{
                    color:'#81ecec'
                },
                markPoint: {
                    itemStyle: {
                        normal: {
                            color: 'transparent'
                        }
                    },
                    label: {
                        normal: {
                            show: true,
                            position: 'left',
                            formatter: myRegression.expression,
                            textStyle: {
                                color: '#a29bfe',
                                fontSize: 14
                            }
                        }
                    },
                    data: [{
                        coord: myRegression.points[myRegression.points.length - 1]
                    }]
                }
            }]
        });
    }
    else{       //光滑线
        chart.setOption({
            series: [{
                // name: 'scatter',
                type: 'scatter',
                label: {
                    emphasis: {
                        show: true,
                        position: 'left',
                        textStyle: {
                            color: '#74b9ff',
                            fontSize: 16
                        }
                    }
                },
                data: data,
                itemStyle: {
                    color: '#74b9ff'
                }
            },{
                name: 'line',
                type: 'line',
                showSymbol: false,
                smooth: true,
                data: myRegression.points,
                lineStyle:{
                    color:'#81ecec'
                },
                markPoint: {
                    itemStyle: {
                        normal: {
                            color: 'transparent'
                        }
                    },
                    label: {
                        normal: {
                            show: true,
                            position: 'left',
                            formatter: myRegression.expression,
                            textStyle: {
                                color: '#a29bfe',
                                fontSize: 14
                            }
                        }
                    },
                    data: [{
                        coord: myRegression.points[myRegression.points.length - 1]
                    }]
                }
            }]
        });
    }
}