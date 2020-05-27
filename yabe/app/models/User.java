package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;
import play.data.validation.*;

//@Entity annotation marks class as JPA entity, Model superclass provides JPA helpers
//ie Model superclass probides auto @Id which is required by JPA
@Entity
public class User extends Model {

    //Play automatically encapsulates variables and provides getters and setters
    //otherwise leaving everything public would be a no no
    @Email
    @Required
    public String email;

    @Required
    public String password;

    public String fullname;
    public boolean isAdmin;

    public User(String email, String password, String fullname) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
    }

    public static User connect(String email, String password) {
        return find("byEmailAndPassword", email, password).first();
    }

    public String toString() {
        return email;
    }
}