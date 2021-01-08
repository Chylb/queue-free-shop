package agh.queueFreeShop.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Length(min = 4, max = 64)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Length(min = 8, max = 64)
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
