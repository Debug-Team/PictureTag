var dom = document.getElementById("threeTagCountChart");
var app = {};
var myChart;
var category = 0;
var rectangle = 0;
var border = 0;


/**
 * 初始化图表的数据
 * @param categoryNum 整体标注的数据
 * @param rectangleNum 标框标注
 * @param borderNum 边界标注
 */
function initThreeTagCountChart(categoryNum, rectangleNum, borderNum) {
    category = categoryNum;
    rectangle = rectangleNum;
    border = borderNum;

    option = null;
    option = {
        backgroundColor: '#2c343c',

        title: {
            text: '三类标注统计',
            left: 'center',
            top: 20,
            textStyle: {
                color: '#ccc'
            }
        },

        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },

        visualMap: {
            show: false,
            min: 80,
            max: 600,
            inRange: {
                colorLightness: [0, 1]
            }
        },
        series : [
            {
                name:'三类标注统计',
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
                        name:'边界标注',
                        itemStyle: {
                            color: '#74b9ff'
                        }
                    }
                ].sort(function (a, b) { return a.value - b.value; }),
                roseType: 'radius',
                label: {
                    normal: {
                        textStyle: {
                            color: 'rgba(255, 255, 255, 0.3)'
                        }
                    }
                },
                labelLine: {
                    normal: {
                        lineStyle: {
                            color: 'rgba(255, 255, 255, 0.3)'
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
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
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

    myChart = echarts.init(dom);
    if (option && typeof option === "object") {
        myChart.setOption(option, true);
    }
}

