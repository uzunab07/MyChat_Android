package edu.uncc.hw08;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Chat implements Serializable {
    String name;
    String docId;
    String message;
    Timestamp time;
    ChatMessage chatMessage;

    public Chat(String name, ChatMessage chatMessage) {
        this.name = name;
        this.chatMessage = chatMessage;
    }

    public  Chat(String name, String message, Timestamp time){
        this.name = name;
        this.message = message;
        this.time = time;
    }

    public Chat() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "name='" + name + '\'' +
                ", chatMessage=" + chatMessage +
                '}';
    }
}
