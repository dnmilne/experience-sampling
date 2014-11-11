package org.poscomp.xp.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by dmilne on 6/11/14.
 */
@Document
public class User {

    @Id
    private ObjectId id ;

    @Indexed
    private String email ;

    private String password ;

    private User() {

    }

    public User(String email, String password) {
        this.email = email ;
        this.password = password ;
    }

    public ObjectId getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
