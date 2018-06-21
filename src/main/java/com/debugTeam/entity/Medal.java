package com.debugTeam.entity;

import java.io.Serializable;

public class Medal implements Serializable {

    private static final long serialVersionUID = -5809782578272943999L;

    private int type;  //勋章类型 1-整体标注 2-标框标注 3-轮廓标注
    private int neededNum;  //标记数量 5-菜鸡 50-熟练 500-大师 5000-传说
    private int degree;  //勋章等级 0-nothing 1-菜鸡 2-熟练 3-大师 4-传说

    public Medal(int type) {
        this.type = type;
        this.degree = 0;
    }

    public void updateState() {
        this.neededNum++;
        this.degree = this.neededNum <= 5 ? 0 :
                this.neededNum <= 50 ? 1 :
                        this.neededNum <= 500 ? 2 :
                                this.neededNum <= 5000 ? 3 : 4;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getNeededNum() {
        return neededNum;
    }

    public void setNeededNum(int neededNum) {
        this.neededNum = neededNum;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }
}
