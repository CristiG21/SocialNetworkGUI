package ubb.scs.map.domain;

import java.time.LocalDateTime;

public class PrietenieDto extends Entity<Long> {
    private String utilizatorFirstName;
    private String utilizatorLastName;
    private LocalDateTime friendsFrom;
    private Boolean accepted;

    public PrietenieDto(Long id, String utilizatorFirstName, String utilizatorLastName, LocalDateTime friendsFrom, Boolean accepted) {
        setId(id);
        this.utilizatorFirstName = utilizatorFirstName;
        this.utilizatorLastName = utilizatorLastName;
        this.friendsFrom = friendsFrom;
        this.accepted = accepted;
    }

    public String getUtilizatorFirstName() {
        return utilizatorFirstName;
    }

    public String getUtilizatorLastName() {
        return utilizatorLastName;
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    public Boolean getAccepted() {
        return accepted;
    }
}
