package ubb.scs.map.domain;

import java.time.LocalDateTime;

public class PrietenieDto extends Entity<Long> {
    private String utilizator1FirstName;
    private String utilizator1LastName;
    private String utilizator2FirstName;
    private String utilizator2LastName;
    private LocalDateTime friendsFrom;
    private Boolean accepted;

    public PrietenieDto(String utilizator1FirstName, String utilizator1LastName, String utilizator2FirstName, String utilizator2LastName, LocalDateTime friendsFrom, Boolean accepted) {
        this.utilizator1FirstName = utilizator1FirstName;
        this.utilizator1LastName = utilizator1LastName;
        this.utilizator2FirstName = utilizator2FirstName;
        this.utilizator2LastName = utilizator2LastName;
        this.friendsFrom = friendsFrom;
        this.accepted = accepted;
    }

    public String getUtilizator1FirstName() {
        return utilizator1FirstName;
    }

    public String getUtilizator1LastName() {
        return utilizator1LastName;
    }

    public String getUtilizator2FirstName() {
        return utilizator2FirstName;
    }

    public String getUtilizator2LastName() {
        return utilizator2LastName;
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    public Boolean getAccepted() {
        return accepted;
    }
}
