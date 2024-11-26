package ubb.scs.map.utils.events;

import ubb.scs.map.domain.Prietenie;

public class PrietenieEntityChangeEvent implements Event {
    private final ChangeEventType type;
    private final Prietenie data;
    private Prietenie oldData;

    public PrietenieEntityChangeEvent(ChangeEventType type, Prietenie data) {
        this.type = type;
        this.data = data;
    }

    public PrietenieEntityChangeEvent(ChangeEventType type, Prietenie data, Prietenie oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Prietenie getData() {
        return data;
    }

    public Prietenie getOldData() {
        return oldData;
    }
}