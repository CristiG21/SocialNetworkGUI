package ubb.scs.map.dto;

import java.util.Optional;

public class PrietenieFilterDto {
    private Optional<Long> userId = Optional.empty();
    private Optional<Boolean> received = Optional.empty();

    public Optional<Long> getUserId() {
        return userId;
    }

    public void setUserId(Optional<Long> userId) {
        this.userId = userId;
    }

    public Optional<Boolean> getReceived() {
        return received;
    }

    public void setReceived(Optional<Boolean> received) {
        this.received = received;
    }
}
