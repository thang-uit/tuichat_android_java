package com.thanguit.tuichat.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatMessage implements Parcelable {
    private String messageID;
    private String message;
    private String image;
    private String senderID;
    private String time;
    private int emoticon = -1;

    public ChatMessage() {
    }

    public ChatMessage(String senderID, String message, String image, String time) {
        this.senderID = senderID;
        this.message = message;
        this.image = image;
        this.time = time;
    }

    public ChatMessage(String messageID, String message, String senderID, String time, int emoticon) {
        this.messageID = messageID;
        this.message = message;
        this.senderID = senderID;
        this.time = time;
        this.emoticon = emoticon;
    }

    protected ChatMessage(Parcel in) {
        messageID = in.readString();
        message = in.readString();
        image = in.readString();
        senderID = in.readString();
        time = in.readString();
        emoticon = in.readInt();
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(messageID);
        parcel.writeString(message);
        parcel.writeString(image);
        parcel.writeString(senderID);
        parcel.writeString(time);
        parcel.writeInt(emoticon);
    }
}
