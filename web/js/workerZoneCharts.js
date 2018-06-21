
initalThreeTagPieCharts();
initalWorkHotMap();
initalDayTime();
initCreditsCharts();
initLinearRegression();
initRadar();
initCreditsBoxPlot();
function initalThreeTagPieCharts() {
    var classify = worker_info_json.jobTypeNum[0];
    var rect = worker_info_json.jobTypeNum[1];
    var border = worker_info_json.jobTypeNum[2];
    // var classify = 1;
    // var rect = 2;
    // var border = 3;
    var dom = document.getElementById("pieTry");
    var myChart = echarts.init(dom);
    var app = {};

    option = null;
    option = {
        backgroundColor: 'transparent',


        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },


        series : [
            {
                name:'三类标注统计',
                type:'pie',
                radius : '55%',
                center: ['50%', '50%'],
                data:[
                    {
                        value:classify,
                        name:'整体标注',
                        itemStyle: {
                            color: '#55efc4'
                        }
                    },
                    {
                        value:rect,
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
        window.location.href = "../html/workerHome.html?categoryList="+params.name;
    })

    if (option && typeof option === "object") {
        myChart.setOption(option, true);
    }
}
function initalWorkHotMap() {
    var worker_hot_map_json = worker_info_json.hotMap;
    var data = [];
    for (var date in worker_hot_map_json){
        data.push([date,worker_hot_map_json[date]]);
    }
    var dom = document.getElementById("hotMap");
    var myChart = echarts.init(dom);
    var app = {};
    option = null;

//获取虚拟数据
//     function getVirtulData(year) {
//         year = year || '2019';
//         var date = +echarts.number.parseDate(year + '-01-01');
//         var end = +echarts.number.parseDate((+year + 1) + '-01-01');
//         var dayTime = 3600 * 24 * 1000;
//         var data = [];
//         for (var i= 0;i<10;i++) {
//             data.push([
//                 "2018-04-1"+i,
//                 10*i
//             ]);
//         }
//         // data.push("2018-04-15",15);
//         // data.push("2018-04-16",30)
//         console.log(data)
//         return data;
//     }

    // var data = getVirtulData(2018);

    option = {
        backgroundColor: 'white',

        tooltip : {
            trigger: 'item'
        },
        legend: {
            top: '30',
            left: '100',
            data:['标记数', 'Top 12'],
            textStyle: {
                color: 'black'
            }
        },
        calendar: [{
            top: 100,
            left: 'center',
            range: ['2018-01-01', '2018-12-31'],
            splitLine: {
                show: true,
                lineStyle: {
                    color: '#000',
                    width: 4,
                    type: 'solid'
                }
            },
            yearLabel: {
                formatter: '{start}',
                textStyle: {
                    color: 'black'
                }
            },
            itemStyle: {
                normal: {
                    color: '#323c48',
                    borderWidth: 1,
                    borderColor: '#111'
                }
            }
        }],
        series : [
            {
                name: '标记数',
                type: 'scatter',
                coordinateSystem: 'calendar',
                data: data,
                symbolSize: function (val) {
                    return val[1] / 1;
                },
                itemStyle: {
                    normal: {
                        color: '#ddb926'
                    }
                }
            },
            {
                name: 'Top 12',
                type: 'effectScatter',
                coordinateSystem: 'calendar',
                calendarIndex: 1,
                data: data.sort(function (a, b) {
                    return b[1] - a[1];
                }).slice(0, 12),
                symbolSize: function (val) {
                    return val[1] /1 ;
                },
                showEffectOn: 'render',
                rippleEffect: {
                    brushType: 'stroke'
                },
                hoverAnimation: true,
                itemStyle: {
                    normal: {
                        color: '#f4e925',
                        shadowBlur: 10,
                        shadowColor: '#333'
                    }
                },
                zlevel: 1
            },
            {
                name: 'Top 12',
                type: 'effectScatter',
                coordinateSystem: 'calendar',
                data: data.sort(function (a, b) {
                    return b[1] - a[1];
                }).slice(0, 12),
                symbolSize: function (val) {
                    return val[1] /1;
                },
                showEffectOn: 'render',
                rippleEffect: {
                    brushType: 'stroke'
                },
                hoverAnimation: true,
                itemStyle: {
                    normal: {
                        color: '#f4e925',
                        shadowBlur: 10,
                        shadowColor: '#333'
                    }
                },
                zlevel: 1
            }
        ]
    };

    if (option && typeof option === "object") {
        myChart.setOption(option, true);
    }


}
function initalDayTime() {


    var dom = document.getElementById("HourMarkChart");
    var myChart = echarts.init(dom);
    var app = {};
    option = null;
    var dataAxis = ['00:00-01:00', '01:00-02:00', '02:00-03:00', '03:00-04:00', '04:00-05:00', '05:00-06:00',
        '06:00-07:00', '07:00-08:00', '08:00-09:00', '09:00-10:00', '10:00-11:00', '11:00-12:00',
        '12:00-13:00', '13:00-14:00', '14:00-15:00', '15:00-16:00', '16:00-17:00', '17:00-18:00',
        '19:00-20:00', '21:00-22:00', '22:00-23:00', '23:00-24:00'];

    var data = [];
    var timeMarkNum_json_array = worker_info_json.timeMarkNum;

    for(var i = 0;i<timeMarkNum_json_array.length;i++){
        data.push(timeMarkNum_json_array[i]);
    }

    var yMax = 20;
    // var dataShadow = [];
    //
    // for (var i = 0; i < data.length; i++) {
    //     dataShadow.push(yMax);
    // }

    option = {

        xAxis: {
            data: dataAxis,
            // axisLabel: {
            //     inside: true,
            //     textStyle: {
            //         color: '#fff'
            //     }
            // },
            axisTick: {
                show: false
            },
            axisLine: {
                show: false
            },
            z: 10
        },
        yAxis: {
            axisLine: {
                show: false
            },
            axisTick: {
                show: false
            },
            axisLabel: {
                textStyle: {
                    color: '#999'
                },
                formatter:'{value} 张'
            }
        },
        dataZoom: [
            {
                type: 'inside'
            }
        ],
        tooltip: {},
        series: [
            { // For shadow
                type: 'bar',
                itemStyle: {
                    normal: {color: 'rgba(0,0,0,0.05)'}
                },
                barGap:'-100%',
                barCategoryGap:'40%',
                // data: dataShadow,
                animation: false
            },
            {
                type: 'bar',
                itemStyle: {
                    normal: {
                        color: new echarts.graphic.LinearGradient(
                            0, 0, 0, 1,
                            [
                                {offset: 0, color: '#83bff6'},
                                {offset: 0.5, color: '#188df0'},
                                {offset: 1, color: '#188df0'}
                            ]
                        )
                    },
                    emphasis: {
                        color: new echarts.graphic.LinearGradient(
                            0, 0, 0, 1,
                            [
                                {offset: 0, color: '#2378f7'},
                                {offset: 0.7, color: '#2378f7'},
                                {offset: 1, color: '#83bff6'}
                            ]
                        )
                    }
                },
                data: data
            }
        ]
    };

// Enable data zoom when user click bar.
    var zoomSize = 6;
    myChart.on('click', function (params) {
        console.log(dataAxis[Math.max(params.dataIndex - zoomSize / 2, 0)]);
        myChart.dispatchAction({
            type: 'dataZoom',
            startValue: dataAxis[Math.max(params.dataIndex - zoomSize / 2, 0)],
            endValue: dataAxis[Math.min(params.dataIndex + zoomSize / 2, data.length - 1)]
        });
    });;
    if (option && typeof option === "object") {
        myChart.setOption(option, true);
    }
}
function initCreditsCharts() {
    var phonenum = getUserPhone();
    var result_json = null;

    var dateData = new Array();
    var selfData = new Array();
    var totalData = new Array();

    $.ajax({
        url:'/creditsCmp',
        type:'post',
        async:false,
        data:{
            phonenum:phonenum
        },
        success:function (data) {
            result_json = JSON.parse(data);
            console.log(result_json)
        },
        error:function () {
            alert("fuck")
        }
    });

    dateData = result_json.dateList;
    selfData = result_json.selfData;
    totalData = result_json.totalData;

    var dom = document.getElementById("creditsGetLineCharts");
    var myChart = echarts.init(dom);
    var app = {};
    option = null;
    option = {
        title: {
            text: ''
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data:['自己','网站平均']
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        toolbox: {
            feature: {
                saveAsImage: {}
            }
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: dateData
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                name:'自己',
                type:'line',
                data:selfData
            },
            {
                name:'网站平均',
                type:'line',
                data:totalData
            }
        ]
    };
    myChart.on('click', function (params) {
        if(params.seriesName == '自己'){
            window.location.href = "../html/markerCreditDetail.html";
        }
    })
    ;
    if (option && typeof option === "object") {
        myChart.setOption(option, true);
    }
}
function initLinearRegression() {
    var result_json = null;
    var phonenum = getUserPhone();

    $.ajax({
        url:'/tagTimeCrossNum',
        type:'post',
        async:false,
        data:{
            phonenum:phonenum
        },
        success:function (data) {
            result_json = JSON.parse(data);
            console.log(result_json)
        },
        error:function () {
            alert("fuck1")
        }
    });

    var data = [];
    var data_array = result_json.data;
    for (var i = 0;i<data_array.length;i++){
        data.push([JSON.parse(data_array[i]).page_num,JSON.parse(data_array[i]).ave_time]);
        // console.log(i,JSON.parse(data_array[i]));
    }
    // console.log("ss",data);

    var dom = document.getElementById("pageTimeLinearRegression");
    var myChart = echarts.init(dom);
    var app = {};
    option = null;
    // var data = [
    //     [0.067732, 3.176513],
    //     [0.42781, 3.816464],
    //     [0.995731, 4.550095],
    //     [0.738336, 4.256571],
    //     [0.981083, 4.560815],
    //     [0.526171, 3.929515],
    //     [0.378887, 3.52617],
    //     [0.033859, 3.156393],
    //     [0.132791, 3.110301],
    //     [0.138306, 3.149813],
    //     [0.247809, 3.476346],
    //     [0.64827, 4.119688],
    //     [0.731209, 4.282233],
    //     [0.236833, 3.486582],
    //     [0.969788, 4.655492],
    //     [0.607492, 3.965162],
    //     [0.358622, 3.5149],
    //     [0.147846, 3.125947],
    //     [0.63782, 4.094115],
    //     [0.230372, 3.476039],
    //     [0.070237, 3.21061],
    //     [0.067154, 3.190612],
    //     [0.925577, 4.631504],
    //     [0.717733, 4.29589],
    //     [0.015371, 3.085028],
    //     [0.33507, 3.44808],
    //     [0.040486, 3.16744],
    //     [0.212575, 3.364266],
    //     [0.617218, 3.993482],
    //     [0.541196, 3.891471],
    //     [0.045353, 3.143259],
    //     [0.126762, 3.114204],
    //     [0.556486, 3.851484],
    //     [0.901144, 4.621899],
    //     [0.958476, 4.580768],
    //     [0.274561, 3.620992],
    //     [0.394396, 3.580501],
    //     [0.87248, 4.618706],
    //     [0.409932, 3.676867],
    //     [0.908969, 4.641845],
    //     [0.166819, 3.175939],
    //     [0.665016, 4.26498],
    //     [0.263727, 3.558448],
    //     [0.231214, 3.436632],
    //     [0.552928, 3.831052],
    //     [0.047744, 3.182853],
    //     [0.365746, 3.498906],
    //     [0.495002, 3.946833],
    //     [0.493466, 3.900583],
    //     [0.792101, 4.238522],
    //     [0.76966, 4.23308],
    //     [0.251821, 3.521557],
    //     [0.181951, 3.203344],
    //     [0.808177, 4.278105],
    //     [0.334116, 3.555705],
    //     [0.33863, 3.502661],
    //     [0.452584, 3.859776],
    //     [0.69477, 4.275956],
    //     [0.590902, 3.916191],
    //     [0.307928, 3.587961],
    //     [0.148364, 3.183004],
    //     [0.70218, 4.225236],
    //     [0.721544, 4.231083],
    //     [0.666886, 4.240544],
    //     [0.124931, 3.222372],
    //     [0.618286, 4.021445],
    //     [0.381086, 3.567479],
    //     [0.385643, 3.56258],
    //     [0.777175, 4.262059],
    //     [0.116089, 3.208813],
    //     [0.115487, 3.169825],
    //     [0.66351, 4.193949],
    //     [0.254884, 3.491678],
    //     [0.993888, 4.533306],
    //     [0.295434, 3.550108],
    //     [0.952523, 4.636427],
    //     [0.307047, 3.557078],
    //     [0.277261, 3.552874],
    //     [0.279101, 3.494159],
    //     [0.175724, 3.206828],
    //     [0.156383, 3.195266],
    //     [0.733165, 4.221292],
    //     [0.848142, 4.413372],
    //     [0.771184, 4.184347],
    //     [0.429492, 3.742878],
    //     [0.162176, 3.201878],
    //     [0.917064, 4.648964],
    //     [0.315044, 3.510117],
    //     [0.201473, 3.274434],
    //     [0.297038, 3.579622],
    //     [0.336647, 3.489244],
    //     [0.666109, 4.237386],
    //     [0.583888, 3.913749],
    //     [0.085031, 3.22899],
    //     [0.687006, 4.286286],
    //     [0.949655, 4.628614],
    //     [0.189912, 3.239536],
    //     [0.844027, 4.457997],
    //     [0.333288, 3.513384],
    //     [0.427035, 3.729674],
    //     [0.466369, 3.834274],
    //     [0.550659, 3.811155],
    //     [0.278213, 3.598316],
    //     [0.918769, 4.692514],
    //     [0.886555, 4.604859],
    //     [0.569488, 3.864912],
    //     [0.066379, 3.184236],
    //     [0.335751, 3.500796],
    //     [0.426863, 3.743365],
    //     [0.395746, 3.622905],
    //     [0.694221, 4.310796],
    //     [0.27276, 3.583357],
    //     [0.503495, 3.901852],
    //     [0.067119, 3.233521],
    //     [0.038326, 3.105266],
    //     [0.599122, 3.865544],
    //     [0.947054, 4.628625],
    //     [0.671279, 4.231213],
    //     [0.434811, 3.791149],
    //     [0.509381, 3.968271],
    //     [0.749442, 4.25391],
    //     [0.058014, 3.19471],
    //     [0.482978, 3.996503],
    //     [0.466776, 3.904358],
    //     [0.357767, 3.503976],
    //     [0.949123, 4.557545],
    //     [0.41732, 3.699876],
    //     [0.920461, 4.613614],
    //     [0.156433, 3.140401],
    //     [0.656662, 4.206717],
    //     [0.616418, 3.969524],
    //     [0.853428, 4.476096],
    //     [0.133295, 3.136528],
    //     [0.693007, 4.279071],
    //     [0.178449, 3.200603],
    //     [0.199526, 3.299012],
    //     [0.073224, 3.209873],
    //     [0.286515, 3.632942],
    //     [0.182026, 3.248361],
    //     [0.621523, 3.995783],
    //     [0.344584, 3.563262],
    //     [0.398556, 3.649712],
    //     [0.480369, 3.951845],
    //     [0.15335, 3.145031],
    //     [0.171846, 3.181577],
    //     [0.867082, 4.637087],
    //     [0.223855, 3.404964],
    //     [0.528301, 3.873188],
    //     [0.890192, 4.633648],
    //     [0.106352, 3.154768],
    //     [0.917886, 4.623637],
    //     [0.014855, 3.078132],
    //     [0.567682, 3.913596],
    //     [0.068854, 3.221817],
    //     [0.603535, 3.938071],
    //     [0.53205, 3.880822],
    //     [0.651362, 4.176436],
    //     [0.901225, 4.648161],
    //     [0.204337, 3.332312],
    //     [0.696081, 4.240614],
    //     [0.963924, 4.532224],
    //     [0.98139, 4.557105],
    //     [0.987911, 4.610072],
    //     [0.990947, 4.636569],
    //     [0.736021, 4.229813],
    //     [0.253574, 3.50086],
    //     [0.674722, 4.245514],
    //     [0.939368, 4.605182],
    //     [0.235419, 3.45434],
    //     [0.110521, 3.180775],
    //     [0.218023, 3.38082],
    //     [0.869778, 4.56502],
    //     [0.19683, 3.279973],
    //     [0.958178, 4.554241],
    //     [0.972673, 4.63352],
    //     [0.745797, 4.281037],
    //     [0.445674, 3.844426],
    //     [0.470557, 3.891601],
    //     [0.549236, 3.849728],
    //     [0.335691, 3.492215],
    //     [0.884739, 4.592374],
    //     [0.918916, 4.632025],
    //     [0.441815, 3.75675],
    //     [0.116598, 3.133555],
    //     [0.359274, 3.567919],
    //     [0.814811, 4.363382],
    //     [0.387125, 3.560165],
    //     [0.982243, 4.564305],
    //     [0.78088, 4.215055],
    //     [0.652565, 4.174999],
    //     [0.87003, 4.58664],
    //     [0.604755, 3.960008],
    //     [0.255212, 3.529963],
    //     [0.730546, 4.213412],
    //     [0.493829, 3.908685],
    //     [0.257017, 3.585821],
    //     [0.833735, 4.374394],
    //     [0.070095, 3.213817],
    //     [0.52707, 3.952681],
    //     [0.116163, 3.129283]
    // ];

// See https://github.com/ecomfe/echarts-stat
    var myRegression = ecStat.regression('linear', data);

    myRegression.points.sort(function(a, b) {
        return a[0] - b[0];
    });

    option = {
        // title: {
        //     text: 'Linear Regression',
        //     subtext: 'By ecStat.regression',
        //     sublink: 'https://github.com/ecomfe/echarts-stat',
        //     left: 'center'
        // },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross'
            },
            axisLabel:{formatter:'{value} ms'}
        },
        xAxis: {
            type: 'value',
            splitLine: {
                lineStyle: {
                    type: 'dashed'
                }
            },
            axisLabel:{formatter:'{value} 张'}
        },
        yAxis: {
            type: 'value',
            min: 1.5,
            splitLine: {
                lineStyle: {
                    type: 'dashed'
                }
            },
            axisLabel:{formatter:'{value} ms'}
        },
        series: [{
            name: '当前均时',
            type: 'scatter',
            label: {
                emphasis: {
                    show: true,
                    position: 'left',
                    textStyle: {
                        color: 'blue',
                        fontSize: 16
                    }
                }
            },
            data: data
        }, {
            name: '回归方程均时',
            type: 'line',
            showSymbol: false,
            data: myRegression.points,
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
                            color: '#333',
                            fontSize: 14
                        }
                    }
                },
                data: [{
                    coord: myRegression.points[myRegression.points.length - 1]
                }]
            }
        }]
    };;
    if (option && typeof option === "object") {
        myChart.setOption(option, true);
    }
}
function initRadar() {

    var dom = document.getElementById("radarWorker");
    var myChart = echarts.init(dom);
    var app = {};
    option = null;


    var phonenum = getUserPhone();
    var result_json;
    var capacity = [];
    var total_capacity = [];
    $.ajax({
        url:'/get5Dstatistics',
        type:'post',
        async:false,
        data:{
            phonenum:phonenum
        },
        success:function (data) {
            result_json = JSON.parse(data);
            // console.log(result_json)
        },
        error:function () {
            alert("fuck1")
        }
    });

    capacity.push(result_json.reputation);
    capacity.push(result_json.work);
    capacity.push(result_json.empiricalValue);
    capacity.push(result_json.credits);
    capacity.push(result_json.ratings);

    total_capacity.push(result_json.total_reputation);
    total_capacity.push(result_json.total_work);
    total_capacity.push(result_json.total_empiricalValue);
    total_capacity.push(result_json.total_credits);
    total_capacity.push(result_json.total_ratings);


    console.log(capacity)
    option = {
        tooltip: {},
        legend: {
            data: ['个人能力','网站平均']
        },
        radar: {
            // shape: 'circle',
            name: {
                textStyle: {
                    color: '#fff',
                    backgroundColor: '#999',
                    borderRadius: 3,
                    padding: [3, 5]
                }
            },
            indicator: [
                { name: '信誉度（Reputation）', max: 100},
                { name: '勤劳度（Work）', max: 100},
                { name: '经验值（EmpiricalValue）', max: 100},
                { name: '积分（Credits）', max: 100},
                { name: '工作满意度（Ratings）', max: 100}
            ]
        },
        series: [{
            name: '预算 vs 开销（Budget vs spending）',
            type: 'radar',
            // areaStyle: {normal: {}},
            data : [
                {
                    value : capacity,
                    name : '个人能力'
                },
                {
                    value : total_capacity,
                    name : '网站平均'
                }
            ]
        }]
    };

    if (option && typeof option === "object") {
        myChart.setOption(option, true);
    }
}

function initCreditsBoxPlot() {
    var userphone = getUserPhone();
    $.ajax({
        url:'/makerHistoryRating',
        type:'post',
        async:false,
        data:{
            userphone:userphone
        },
        success:function (data) {
            result_json = JSON.parse(data);
            // console.log(result_json)
        },
        error:function () {
            alert("fuck1")
        }
    });
    var mydata = result_json.data;
    var othersdata = result_json.others;

    var dom = document.getElementById("creditsBoxPlotChart");
    var myChart = echarts.init(dom);
    var app = {};
    option = null;
    var data = echarts.dataTool.prepareBoxplotData([
        mydata,
        othersdata
         ]);

    option = {
        tooltip: {
            trigger: 'item',
            axisPointer: {
                type: 'shadow'
            }
        },
        grid: {
            left: '10%',
            right: '10%',
            bottom: '15%'
        },
        xAxis: {
            type: 'category',
            data: data.axisData,
            boundaryGap: true,
            nameGap: 30,
            splitArea: {
                show: false
            },
            axisLabel: {
                formatter: '{value}'
            },
            splitLine: {
                show: false
            }
        },
        yAxis: {
            type: 'value',
            name: '评分',
            splitArea: {
                show: true
            }
        },
        series: [
            {
                name: 'boxplot',
                type: 'boxplot',
                data: data.boxData,
                tooltip: {
                    formatter: function (param) {
                        return [
                            (param.name==0)?'自己':'网站',
                            'upper: ' + param.data[5],
                            'Q3: ' + param.data[4],
                            'median: ' + param.data[3],
                            'Q1: ' + param.data[2],
                            'lower: ' + param.data[1]
                        ].join('<br/>')
                    }
                }
            },
            {
                name: 'outlier',
                type: 'scatter',
                data: data.outliers
            }
        ]
    };;
    if (option && typeof option === "object") {
        myChart.setOption(option, true);
    }
}