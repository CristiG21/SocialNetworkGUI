package ubb.scs.map.domain;

public class UtilizatorChat extends Entity<Long> {
    private Long userId;
    private Long chatId;

    public UtilizatorChat(Long userId, Long chatId) {
        this.userId = userId;
        this.chatId = chatId;
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

    @Override
    public String toString() {
        return "UtilizatoriChat{" +
                "userId=" + userId +
                ", chatId=" + chatId +
                '}';
    }
}
