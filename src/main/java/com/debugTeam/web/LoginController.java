package com.debugTeam.web;

import com.debugTeam.entity.Administrator;
import com.debugTeam.entity.Marker;
import com.debugTeam.entity.MarkerJob;
import com.debugTeam.entity.Project;
import com.debugTeam.service.AdministratorService;
import com.debugTeam.service.LoginService;
import com.debugTeam.service.ProjectService;
import com.debugTeam.service.UserSevice;
import com.debugTeam.util.ResponseObject;
import com.debugTeam.util.exception.UserDuplicateException;
import com.debugTeam.util.exception.UserNotExistedException;
import com.debugTeam.util.exception.UserWrongPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
@RequestMapping("/")
public class LoginController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private UserSevice userSevice;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private AdministratorService administratorService;

    /**
     * 登陆验证
     * @param userphone 手机号
     * @param password 密码
     * @return json状态信息 1为上传者，0为标记者
     */
    @PostMapping(value = "/login", produces="application/text; charset=utf-8")
    public @ResponseBody
    String login(@RequestParam("userphone") String userphone, @RequestParam("password") String password){
        System.out.println("login"+userphone+" "+password);
        String retJson;
        int usertype = -1;

        try {
            if(userphone.equals("admin")&&password.equals("admin"))
                usertype = -1;
            else if(loginService.login(userphone,password)==1){
                usertype = 1;
                administratorService.updateDailyUploaderLoginNum();
            }
            else {
                usertype = 0;
                administratorService.updateDailyMarkerLoginNum();
                if(!kickOut(userphone))
                    System.out.println("kickout fail");
            }
            Administrator administrator = administratorService.getAdministrator();
            administrator.setLoginDetail(userphone);
            administratorService.updateAdministrator(administrator);

            retJson = new ResponseObject(1,"登陆成功", usertype).toString();
        } catch (UserWrongPasswordException e) {
            retJson = new ResponseObject(0,"密码错误").toString();
        } catch (UserNotExistedException e) {
            retJson = new ResponseObject(-1,"账户名不存在").toString();
        } catch (UserDuplicateException e) {
            retJson = new ResponseObject(-2,"账户异常，请联系管理员").toString();
        }
        System.out.println(retJson);
        return retJson;
    }

    /**
     * 注册
     * @param username 用户名
     * @param password 密码
     * @param phone 手机号
     * @param type 用户类型 1为上传者，0为标记者
     * @return json状态信息
     */
    @PostMapping(value = "/signup", produces="application/text; charset=utf-8")
    public @ResponseBody
    String signup(@RequestParam("username") String username, @RequestParam("password") String password,
                  @RequestParam("phone") String phone, @RequestParam("type") String type){
        System.out.println("login"+username+" "+password+" "+phone+' '+type);
        String retjson;

        try {
            if(username==null || password==null || phone==null ||
                    username.equals("") || password.equals("") || phone.equals("") )
                retjson = new ResponseObject(-1,"注册失败，不符合规则的账户名或密码").toString();
            else{
                loginService.signup(username,password,phone,type);
                if (type.equals("1")){
                    administratorService.updateDailyUploaderRegisterNum();
                    administratorService.updateDailyUploaderLoginNum();
                }
                else if(type.equals("0")){
                    administratorService.updateDailyMarkerRegisterNum();
                    administratorService.updateDailyMarkerLoginNum();
                }
                Administrator administrator = administratorService.getAdministrator();
                administrator.setSignupDetail(phone);
                administratorService.updateAdministrator(administrator);
                retjson = new ResponseObject(1,"注册成功").toString();
            }
        } catch (UserDuplicateException e) {
            retjson = new ResponseObject(-1,"手机号已注册").toString();
        }

        return retjson;
    }

    /**
     * 刷新项目是否踢出
     * @param phonenum
     * @return
     */
    private boolean kickOut(String phonenum){
        final long kickoutTime = 10*24*60*60*1000; //毫秒，10天
        Marker marker = userSevice.getMarker(phonenum);
        Date now = new Date();

        for (int i=0; i<marker.getMarkerJobList().size(); i++){
            MarkerJob job = marker.getMarkerJobList().get(i);
            if (!job.getIsFinished()){
                SimpleDateFormat simFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                try {
                    Date startTime = simFormat.parse(job.getStartTime());
                    if (now.getTime() - startTime.getTime() > kickoutTime){
                        projectService.kickOut(phonenum, job.getId());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        return true;
    }
}
