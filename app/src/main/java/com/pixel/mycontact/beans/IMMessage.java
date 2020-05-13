package com.pixel.mycontact.beans;

import com.google.gson.annotations.Expose;

import java.text.SimpleDateFormat;
import java.util.UUID;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * @author Carl Su
 * @date 2019/11/26
 */
@RealmClass
public class IMMessage implements RealmModel {

    @PrimaryKey
    private String msgID = UUID.randomUUID().toString();
    @Expose
    private String msgSource;
    @Expose
    private String msgDestination;
    @Expose
    private long msgTime;
    @Expose
    private String msgUser;
    @Expose
    private String msgBody;

    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public String getMsgSource() {
        return msgSource;
    }

    public void setMsgSource(String msgSource) {
        this.msgSource = msgSource;
    }

    public String getMsgDestination() {
        return msgDestination;
    }

    public void setMsgDestination(String msgDestination) {
        this.msgDestination = msgDestination;
    }

    public String getMsgUser() {
        return msgUser;
    }

    public void setMsgUser(String msgUser) {
        this.msgUser = msgUser;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }

    public String getSimpleTime() {
        return SimpleDateFormat.getTimeInstance().format(msgTime);
    }


    @Override
    public String toString() {
        return "IMMessage{" +
                "msgID='" + msgID + '\'' +
                ", msgBody='" + msgBody + '\'' +
                '}';
    }
}
