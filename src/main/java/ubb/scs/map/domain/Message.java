package ubb.scs.map.domain;

import java.time.LocalDateTime;

public class Message extends Entity<Long> {
    private Long userId;
    private Long chatId;
    private String message;
    private LocalDateTime date;
    private Long replyMessageId;

    public Message(Long userId, Long chatId, String message, LocalDateTime date, Long replyMessageId) {
        this.userId = userId;
        this.chatId = chatId;
        this.message = message;
        this.date = date;
        this.replyMessageId = replyMessageId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Long getReplyMessageId() {
        return replyMessageId;
    }

    public void setReplyMessageId(Long replyMessageId) {
        this.replyMessageId = replyMessageId;
    }

    @Override
    public String toString() {
        return "Message{" +
                "userId=" + userId +
                ", chatId=" + chatId +
                ", message='" + message + '\'' +
                ", date=" + date +
                ", replyMessageId=" + replyMessageId +
                '}';
    }
}