package ubb.scs.map.domain;

public class Chat extends Entity<Long> {
    private String name;
    private Long adminId;

    public Chat(String name, Long adminId) {
        this.name = name;
        this.adminId = adminId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "name='" + name + '\'' +
                ", adminId=" + adminId +
                '}';
    }
}
