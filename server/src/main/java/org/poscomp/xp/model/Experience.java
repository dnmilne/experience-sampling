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
    @ApiModelProperty(value="An automatically-assigned string that uniquely identifies this experience")
    private ObjectId id ;

    @Indexed
    @ApiModelProperty(value="The date this was experienced")
    private Date date ;

    @Indexed
    @ApiModelProperty(value="The id of the user who experienced it")
    private ObjectId userId ;

    @Indexed
    @ApiModelProperty(value="The id of the prompt that was sent to remind the user to log this experience. This may be null if the user logged the experience without reminding.")
    private ObjectId promptId ;

    @ApiModelProperty(value="A short description of the experience.")
    private String description ;

    @ApiModelProperty(value="An optional set of tags that can be used to organize and group experiences")
    private List<String> tags ;

    @ApiModelProperty(value="An optional recording of how the user felt before the experience.")
    private Mood moodBefore ;

    @ApiModelProperty(value="An optional recording of how the user felt after the experience.")
    private Mood moodAfter ;

    //these fields are needed to implement bidirectional sync
    @Indexed
    @ApiModelProperty(value="The last time this experience was modified (needed for syncing)")
    private Date modifiedAt ;

    @ApiModelProperty(value="True if the experience has been deleted, false otherwise (needed for syncing)")
    private boolean deleted ;

    private Experience() {

    }

    public Experience(User user, Experience x) {

        this.userId = user.getId() ;
        this.id = x.getId() ;

        if (x.getDate() == null)
            this.date = new Date() ;
        else
            this.date = x.getDate() ;

        update(x) ;
    }

    public void update(Experience x) {

        this.promptId = x.getPromptId() ;

        this.description = x.getDescription() ;
        this.tags = x.getTags() ;

        this.moodBefore = x.getMoodBefore() ;
        this.moodAfter = x.getMoodAfter() ;

        this.modifiedAt = new Date() ;
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

    public ObjectId getPromptId() {
        return promptId;
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
