package com.thanguit.tuichat.models;

public class ChatMessage {
    private String messageID;
    private String message;
    private String senderID;
    private String time;
    private int emoticon;

    public ChatMessage() {
    }

    public ChatMessage(String senderID, String message, String time) {
        this.senderID = senderID;
        this.message = message;
        this.time = time;
    }

    public ChatMessage(String messageID, String message, String senderID, String time, int emoticon) {
        this.messageID = messageID;
        this.message = message;
        this.senderID = senderID;
        this.time = time;
        this.emoticon = emoticon;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getEmoticon() {
        return emoticon;
    }

    public void setEmoticon(int emoticon) {
        this.emoticon = emoticon;
    }
}
