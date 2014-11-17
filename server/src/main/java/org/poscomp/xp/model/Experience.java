package org.poscomp.xp.model;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * An experience (activity, mood before, mood after) logged by an individual
 */

@Document
@ApiModel(description = "An experience, and how the user felt about it")
public class Experience {

    @Id
    private ObjectId id ;

    @Indexed
    private Date date ;

    @Indexed
    private ObjectId userId ;

    private String description ;

    @Indexed
    private List<String> tags ;

    @Indexed
    private Mood moodBefore ;

    @Indexed
    private Mood moodAfter ;

    //these fields are needed to implement bidirectional sync
    @Indexed
    private Date modifiedAt ;

    private boolean deleted ;

    private Experience() {

    }

    public Experience(User user, Views.Experience x) {

        this.userId = user.getId() ;
        this.id = x.getId() ;

        if (x.getDate() == null)
            this.date = new Date() ;
        else
            this.date = x.getDate() ;

        update(x) ;
    }

    public void update(Views.Experience x) {

        this.description = x.getDescription() ;
        this.tags = x.getTags() ;

        if (x.getMoodBefore() != null)
            this.moodBefore = new Mood(x.getMoodBefore()) ;
        else
            this.moodBefore = Mood.NEUTRAL ;

        if (x.getMoodAfter() != null)
            this.moodAfter = new Mood(x.getMoodAfter()) ;
        else
            this.moodAfter = Mood.NEUTRAL ;

        this.modifiedAt = new Date() ;
        this.deleted = x.isDeleted() ;

    }

    public void delete() {
        this.deleted = true ;
        this.modifiedAt = new Date() ;
    }

    public ObjectId getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public ObjectId getUserId() {
        return userId;
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
