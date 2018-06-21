package com.debugTeam.util;

import com.alibaba.fastjson.JSON;

public class ResponseObject {

    private int state;
    private String retMessage;
    private int usertype; //用户类型

    public ResponseObject(int state, String retMessage) {
        this.state = state;
        this.retMessage = retMessage;
    }

    public ResponseObject(int state, String retMessage, int usertype) {
        this.state = state;
        this.retMessage = retMessage;
        this.usertype = usertype;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getRetMessage() {
        return retMessage;
    }

    public void setRetMessage(String retMessage) {
        this.retMessage = retMessage;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }


    public int getUsertype() {
        return usertype;
    }

    public void setUsertype(int usertype) {
        this.usertype = usertype;
    }
}
