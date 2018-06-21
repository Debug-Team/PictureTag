
initWholeProjectPie();
initDoingProjectPie();
initFinishedProjectPie();

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
    myChart.on('click', function (params) {
        window.location.href = "../html/requestorProject.html?categoryList="+params.name;
    })

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
    myChart.on('click', function (params) {
        window.location.href = "../html/requestorProject.html?categoryList="+params.name+"&"+"进行中";
    })

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
    myChart.on('click', function (params) {
        window.location.href = "../html/requestorProject.html?categoryList="+params.name+"&"+"已结束";
    })

    if (option && typeof option === "object") {
        myChart.setOption(option, true);
    }
}

