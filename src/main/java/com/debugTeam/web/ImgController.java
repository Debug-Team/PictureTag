package com.debugTeam.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
public class ImgController {

    @RequestMapping("pic/{project}/{picname:.*}")
    public void getIcon(@PathVariable("project") String project,
                        @PathVariable("picname") String picname,
                        HttpServletRequest request,
                        HttpServletResponse response) throws IOException {

        String fileName = "data"+File.separator+"project"+File.separator+project+File.separator+
                        "pics"+File.separator+picname;

        String[] strArr = picname.split("\\.");
        try {
            File filePath = new File(fileName);
            if(filePath.exists()){
                //读图片
                FileInputStream inputStream = new FileInputStream(filePath);
                int available = inputStream.available();
                byte[] data = new byte[available];
                inputStream.read(data);
                inputStream.close();
                //写图片
                response.setContentType("image/"+strArr[1]);
                response.setCharacterEncoding("UTF-8");
                OutputStream stream = new BufferedOutputStream(response.getOutputStream());
                stream.write(data);
                stream.flush();
                stream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
