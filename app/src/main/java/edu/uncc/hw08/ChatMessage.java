package edu.uncc.hw08;

import com.google.firebase.Timestamp;

import java.sql.Time;

public class ChatMessage {
    String message, name, userId;
    Timestamp timeStamp;
    String messageId;

    public ChatMessage(Timestamp timeStamp, String message, String name, String userId) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.name = name;
        this.userId = userId;
    }

    public ChatMessage() {
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "timeStamp='" + timeStamp + '\'' +
                ", message='" + message + '\'' +
                ", name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
