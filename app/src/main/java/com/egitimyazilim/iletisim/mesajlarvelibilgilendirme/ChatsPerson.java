package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.content.Context;

import java.util.List;

public class ChatsPerson {

    private String sender;
    private List<String> messages;
    private List<String> sms_read;
    private List<Long> times;
    private List<String> folder_names;

    public ChatsPerson() {
    }

    public ChatsPerson(String sender, List<String> messages, List<String> sms_read, List<Long> time, List<String> folder_name) {
        this.sender = sender;
        this.messages = messages;
        this.sms_read = sms_read;
        this.times= time;
        this.folder_names = folder_name;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public List<String> getSms_read() {
        return sms_read;
    }

    public void setSms_read(List<String> sms_read) {
        this.sms_read = sms_read;
    }

    public List<Long> getTimes() {
        return times;
    }

    public void setTimes(List<Long> time) {
        this.times = time;
    }

    public List<String> getFolder_names() {
        return folder_names;
    }

    public void setFolder_names(List<String> folder_name) {
        this.folder_names = folder_name;
    }

}
