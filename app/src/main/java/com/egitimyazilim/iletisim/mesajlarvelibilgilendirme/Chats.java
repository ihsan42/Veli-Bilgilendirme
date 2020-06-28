package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Chats  implements Comparable<Chats>{

    private long _id;
    private String sender;
    private String sender_name;
    private String message;
    private String sms_read;
    private Long time;
    private Long sent_time;
    private String folder_name;

    public Chats() {
    }

    public Chats(String sender, String message, String sms_read, long time, String folder_name) {
        this.sender = sender;
        this.message = message;
        this.sms_read = sms_read;
        this.time = time;
        this.folder_name = folder_name;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public Long getSent_time() {
        return sent_time;
    }

    public void setSent_time(Long sent_time) {
        this.sent_time = sent_time;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSms_read() {
        return sms_read;
    }

    public void setSms_read(String sms_read) {
        this.sms_read = sms_read;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFolder_name() {
        return folder_name;
    }

    public void setFolder_name(String folder_name) {
        this.folder_name = folder_name;
    }

    @Override
    public int compareTo(@NonNull Chats o) {
        return time.compareTo(o.getTime());
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Chats chats = (Chats) o;
        return sender == chats.sender;
    }


}
