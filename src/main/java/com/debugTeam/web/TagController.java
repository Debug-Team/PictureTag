package com.debugTeam.web;

import com.alibaba.fastjson.JSON;
import com.debugTeam.entity.Marker;
import com.debugTeam.entity.MarkerJob;
import com.debugTeam.service.TagService;
import com.debugTeam.service.UserSevice;
import com.debugTeam.util.ResponseObject;
import com.debugTeam.util.ZipHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class TagController {

    @Autowired
    private UserSevice userSevice;
    @Autowired
    private TagService tagService;

    /**
     * 下载数据集
     * @param id 项目id
     * @return
     */
    @RequestMapping(value = "/downloadTags/{id}")
    public @ResponseBody
    String downloadTags(@PathVariable("id") String id,
                        HttpServletRequest request,
                        HttpServletResponse response){
        String tmp = "data"+File.separator+"tmp";
        String tagLoc = "data"+File.separator+"project"+File.separator+id+File.separator+
                "tags";
        String zipName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".zip";

        System.out.println("here");
        try {
            ZipHelper.zip(tagLoc,tmp+File.separator+zipName);
            File filePath = new File(tmp+File.separator+zipName);
            if(filePath.exists()){
                //读文件
                FileInputStream inputStream = new FileInputStream(filePath);
                int available = inputStream.available();
                byte[] data = new byte[available];
                inputStream.read(data);
                inputStream.close();
                //写文件
                response.setContentType("application/x-zip-compressed");
                response.setCharacterEncoding("UTF-8");
                OutputStream stream = new BufferedOutputStream(response.getOutputStream());
                stream.write(data);
                stream.flush();
                stream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("here");
        return new ResponseObject(1,"下载成功").toString();
    }

    /**
     * 得到标记者任务的标注细节
     * @param phonenum 标注者手机号
     * @param id 项目id
     * @return 状态信息
     */
    @PostMapping(value = "/getJobDetail", produces="application/text; charset=utf-8")
    public @ResponseBody
    String getJobDetail(@RequestParam("phonenum") String phonenum, @RequestParam("id") String id){
        Marker marker = userSevice.getMarker(phonenum);
        MarkerJob job = marker.getJob(id);
        return JSON.toJSONString(job);
    }

    /**
     * 得到图片的标注信息
     * @param phonenum 标记者手机号
     * @param id 项目id
     * @param pic 图片名
     * @return 图片的标注信息
     */
    @PostMapping(value = "/getPictureTag", produces="application/text; charset=utf-8")
    public @ResponseBody
    String getPictureTag(@RequestParam("phonenum") String phonenum, @RequestParam("id") String id,
                         @RequestParam("pic") String pic){
        try {
            String tag = tagService.getTag(id,phonenum,pic);
            return tag != null ? tag : "";
        } catch (IOException e) {
            return "";
        }

    }

    /**
     * 更新图片的标注信息，没有标注信息为新增
     * @param phonenum 标记者手机号
     * @param id 项目id
     * @param pic 图片名
     * @return 更新状态
     */
    @PostMapping(value = "/updatePictureTag", produces="application/text; charset=utf-8")
    public @ResponseBody
    String updatePictureTag(@RequestParam("phonenum") String phonenum, @RequestParam("id") String id,
                         @RequestParam("pic") String pic, @RequestParam("data") String data, @RequestParam("workTime") long workTime){
        MarkerJob job = userSevice.getMarker(phonenum).getJob(id);
        try {
            if (job.getMarkedPicList()
                    .stream()
                    .anyMatch((p) -> p.equals(pic))) {
                tagService.updateTag(id, phonenum, pic, data, (int)workTime);
            } else {
                tagService.addTag(id, phonenum, pic, data, (int)workTime);
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
            return new ResponseObject(-1,"文件保存异常，请联系管理员").toString();
        }catch (FileAlreadyExistsException e){
            e.printStackTrace();
            return new ResponseObject(-1,"文件保存异常，请联系管理员").toString();
        }
        return new ResponseObject(1,"成功").toString();
    }

}
