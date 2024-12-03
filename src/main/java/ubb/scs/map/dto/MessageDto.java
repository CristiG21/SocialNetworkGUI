package ubb.scs.map.dto;

import ubb.scs.map.domain.Entity;

public class MessageDto extends Entity<Long> {
    private final String userName;
    private final String message;
    private final String replyMessage;

    public MessageDto(String userName, String message, String replyMessage) {
        this.userName = userName;
        this.message = message;
        this.replyMessage = replyMessage;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    public String getReplyMessage() {
        return replyMessage;
    }

    @Override
    public String toString() {
        return "MessageDto{" +
                "userName='" + userName + '\'' +
                ", message='" + message + '\'' +
                ", replyMessage='" + replyMessage + '\'' +
                '}';
    }
}