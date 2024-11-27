package ubb.scs.map.utils.events;

import ubb.scs.map.domain.Chat;

public class ChatEntityChangeEvent implements Event {
    private final ChangeEventType type;
    private final Chat data;
    private Chat oldData;

    public ChatEntityChangeEvent(ChangeEventType type, Chat data) {
        this.type = type;
        this.data = data;
    }

    public ChatEntityChangeEvent(ChangeEventType type, Chat data, Chat oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Chat getData() {
        return data;
    }

    public Chat getOldData() {
        return oldData;
    }
}