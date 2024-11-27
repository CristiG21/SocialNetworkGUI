package ubb.scs.map.utils.events;

import ubb.scs.map.domain.Message;

public class MessageEntityChangeEvent implements Event {
    private final ChangeEventType type;
    private final Message data;
    private Message oldData;

    public MessageEntityChangeEvent(ChangeEventType type, Message data) {
        this.type = type;
        this.data = data;
    }

    public MessageEntityChangeEvent(ChangeEventType type, Message data, Message oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Message getData() {
        return data;
    }

    public Message getOldData() {
        return oldData;
    }
}