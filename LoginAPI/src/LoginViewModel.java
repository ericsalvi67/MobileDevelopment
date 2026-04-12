import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginViewModel {

    public LoginViewModel() {
    }

    public LoginViewModel(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @JsonAlias("user")
    public String email;
    public String password;

}
