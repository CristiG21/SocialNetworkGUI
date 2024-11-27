package ubb.scs.map.domain;

import java.time.LocalDateTime;

public class MessageDto extends Entity<Long> {
    private String userName;
    private String message;
    private String replyMessage;

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