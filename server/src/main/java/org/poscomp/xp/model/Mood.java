package org.poscomp.xp.model;


import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A mood used within an experience (e.g. mood before, mood after)
 */
@Document
@ApiModel(description = "A mood that is felt within an experience")
public class Mood {

    private String name ;
    private double valence ;
    private double arousal ;


    private Mood() {

    }

    public Mood(String name, double valence, double arousal) {

        this.name = name;
        this.valence = valence;
        this.arousal = arousal;
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
