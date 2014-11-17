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

    @Indexed
    private String screenName ;

    private String password ;

    private User() {

    }

    public User(String email, String screenName, String password) {
        this.email = email ;
        this.screenName = screenName ;
        this.password = password ;
    }

    public User(Views.Me me) {

        this.email = me.getEmail() ;
        this.screenName = me.getScreenName() ;
        this.password = me.getPassword() ;
    }

    public void update(Views.Me me) {

        this.email = me.getEmail() ;
        this.screenName = me.getScreenName() ;

        if (me.getPassword() != null)
            this.password = me.getPassword() ;
    }

    public ObjectId getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getScreenName() { return screenName ; }

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
