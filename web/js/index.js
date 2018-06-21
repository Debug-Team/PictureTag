/**
 * Variables
 */
const signupButton = document.getElementById('signup-button'),
    loginButton = document.getElementById('login-button'),
    userForms = document.getElementById('user_options-forms')

/**
 * Add event listener to the "Sign Up" button
 */
signupButton.addEventListener('click', () => {
    userForms.classList.remove('bounceRight')
    userForms.classList.add('bounceLeft')
}, false)

/**
 * Add event listener to the "Login" button
 */
loginButton.addEventListener('click', () => {
    userForms.classList.remove('bounceLeft')
    userForms.classList.add('bounceRight')
}, false)

/**
 * 点击滚动
 */
$.scrollify({
    section:".panel"
});

$(".module-nav a").click(function(e) {
    e.preventDefault();
    $.scrollify("move",$(this).attr("href"));
});

/**
 * 点击登录注册按钮，显示登录
 */
function showLogin() {
    $(".overlay").show();
    $(".login").show();
}

/**
 * 关闭登录
 */
function closeLogin(){
    $(".overlay").hide();
    $(".login").hide();
}
