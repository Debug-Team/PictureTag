package com.debugTeam.dto;

import com.alibaba.fastjson.JSON;
import com.debugTeam.entity.Project;

public class ResProject {

    //项目预览只需前5个属性
    private String id;
    private String previewPic;
    private double process;
    private String discription;
    private int type;
    private boolean isEnded;
    private String title;

    public ResProject(String id, String previewPic, double process, String discription, int type) {
        this.id = id;
        this.previewPic = previewPic;
        this.process = process;
        this.discription = discription;
        this.type = type;
    }

    public ResProject(Project project){
        this.id = project.getId();
        this.previewPic = project.getPicList().get(0);
        this.process = project.getProcess();
        this.discription = project.getDescription();
        this.type = project.getType();
        this.isEnded = project.isEnded();
        this.title = project.getName();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isEnded() {
        return isEnded;
    }

    public void setEnded(boolean ended) {
        isEnded = ended;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPreviewPic() {
        return previewPic;
    }

    public void setPreviewPic(String previewPic) {
        this.previewPic = previewPic;
    }

    public double getProcess() {
        return process;
    }

    public void setProcess(double process) {
        this.process = process;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
