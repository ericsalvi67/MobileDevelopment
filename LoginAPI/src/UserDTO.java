public class UserDTO {
    public String user;
    public String password;
    public String name;
    public String address;

    public UserDTO() {
    }

    public UserDTO(String user, String password, String name, String address) {
        this.user = user;
        this.password = password;
        this.name = name;
        this.address = address;
    }
}
