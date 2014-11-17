package org.poscomp.xp.model;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;
import java.util.List;

/**
 * Created by dmilne on 17/11/14.
 */
public class Views {

    public static class User {

        private ObjectId id ;

        private String screenName ;

        private User() {

        }


        public User(org.poscomp.xp.model.User user) {
            this.id = user.getId() ;
            this.screenName = user.getScreenName() ;
        }

        public ObjectId getId() {
            return id;
        }

        public String getScreenName() {
            return screenName;
        }
    }

    public static class Me {

        private ObjectId id ;

        private String screenName ;

        private String email ;

        private String password ;

        private Me() {

        }

        public Me(org.poscomp.xp.model.User user) {

            this.id = user.getId() ;
            this.screenName = user.getScreenName() ;
            this.email = user.getEmail() ;
            this.password = user.getPassword() ;
        }

        public ObjectId getId() {
            return id;
        }

        public String getScreenName() {
            return screenName;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public Me redactPassword() {
            this.password = null ;
            return this ;
        }
    }



    public static class Experience {

        @ApiModelProperty(value="An automatically-assigned string that uniquely identifies this experience")
        private ObjectId id ;

        @ApiModelProperty(value="The date this was experienced")
        private Date date ;

        @ApiModelProperty(value="The person who had this experience")
        private Views.User user ;

        @ApiModelProperty(value="A short description of the experience.")
        private String description ;

        @ApiModelProperty(value="An optional set of tags that can be used to organize and group experiences")
        private List<String> tags ;

        @ApiModelProperty(value="An optional recording of how the user felt before the experience.")
        private Views.Mood moodBefore ;

        @ApiModelProperty(value="An optional recording of how the user felt after the experience.")
        private Views.Mood moodAfter ;

        //these fields are needed to implement bidirectional sync
        @ApiModelProperty(value="The date this experience was last modified (used for sync)")
        private Date modifiedAt ;

        @ApiModelProperty(value="True if this experience has been deleted, otherwise false (used for sync)")
        private boolean deleted ;


        private Experience() {

        }


        public Experience(org.poscomp.xp.model.Experience experience, org.poscomp.xp.model.User user) {

            this.id = experience.getId() ;

            this.date = experience.getDate() ;

            this.user = new Views.User(user) ;

            this.description = experience.getDescription() ;

            this.tags = experience.getTags() ;

            this.moodBefore = new Mood(experience.getMoodBefore()) ;
            this.moodAfter = new Mood(experience.getMoodAfter()) ;


            this.modifiedAt = experience.getModifiedAt() ;
            this.deleted = experience.isDeleted() ;
        }


        public ObjectId getId() {
            return id;
        }

        public Date getDate() {
            return date;
        }

        public User getUser() {
            return user;
        }

        public String getDescription() {
            return description;
        }

        public List<String> getTags() {
            return tags;
        }

        public Mood getMoodBefore() {
            return moodBefore;
        }

        public Mood getMoodAfter() {
            return moodAfter;
        }

        public Date getModifiedAt() {
            return modifiedAt;
        }

        public boolean isDeleted() {
            return deleted;
        }
    }


    @ApiModel(description = "A mood that is felt")
    public static class Mood {

        private String name ;

        private double valence ;

        private double arousal ;

        private Mood() {

        }

        public Mood(org.poscomp.xp.model.Mood mood) {

            this.name = mood.getName() ;

            this.valence = mood.getValenceAndArousal()[0] ;
            this.arousal = mood.getValenceAndArousal()[1] ;
        }

        @ApiModelProperty(value="The name of this mood")
        public String getName() {
            return name;
        }

        @ApiModelProperty(value="A score that ranges from -1 (highly negative, sad) to +1 (highly positive, happy)")
        public double getValence() {
            return valence;
        }

        @ApiModelProperty(value="A score that ranges from -1 (calming, tiring) to +1 (exciting, agitating)")
        public double getArousal() {
            return arousal;
        }

    }
}
