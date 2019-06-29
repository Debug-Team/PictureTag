/**
 * 初始化requestorHome时，加载数据
 */
window.onload = function () {
    loadUserCredits();      //加载积分
    // initThreeTagCountChart(335,310,274);
}

var home = new Vue({
    el: '#home'
})