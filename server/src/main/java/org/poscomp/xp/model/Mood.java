package org.poscomp.xp.model;


import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A mood used within an experience (e.g. mood before, mood after)
 */
@Document
public class Mood {

    public static final Mood NEUTRAL = new Mood("neutral", 0, 0) ;

    @Id
    private String name ;

    @GeoSpatialIndexed
    private double valenceAndArousal[] ;

    private Mood() {

    }

    public Mood(String name, double valence, double arousal) {

        this.name = name;
        this.valenceAndArousal = new double[2] ;
        this.valenceAndArousal[0] = valence ;
        this.valenceAndArousal[1] = arousal ;
    }

    public Mood(Views.Mood mood) {
        this.name = mood.getName() ;

        this.valenceAndArousal = new double[2] ;
        this.valenceAndArousal[0] = mood.getValence() ;
        this.valenceAndArousal[1] = mood.getArousal() ;
    }

    public String getName() {
        return name;
    }

    public double[] getValenceAndArousal() {
        return valenceAndArousal;
    }
}
