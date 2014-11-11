package org.poscomp.xp.model;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * A mood that is used across everyone's experiences.
 * Exists to maintain a central library of moods and their average valence/arousal
 */
@Document
@ApiModel(description = "A mood that is felt across people's experiences")
public class IndexedMood {

    @Id
    private String name ;

    @Indexed
    private boolean canonical ;

    @GeoSpatialIndexed
    private double[] averageValenceAndArousal ;

    private double totalValence ;
    private double totalArousal ;

    @Indexed
    private int totalSamples ;



    //these fields are needed to implement bidirectional sync
    @Indexed
    private Date modifiedAt ;

    private boolean deleted ;


    private IndexedMood() {


    }

    public IndexedMood(String name, boolean canonical, double valence, double arousal) {

        this.name = IndexedMood.normalize(name) ;
        this.canonical = canonical ;

        this.totalValence = valence ;
        this.totalArousal = arousal ;

        this.totalSamples = 1 ;

        updateAverages();
    }

    public void addSample(Mood mood) {

        if (!IndexedMood.normalize(mood.getName()).equals(name))
            throw new IllegalArgumentException("mood names do not match") ;

        totalSamples++ ;

        totalArousal += mood.getArousal() ;

        totalValence += mood.getValence() ;

        updateAverages();
    }

    public void removeSample(Mood mood) {

        if (!IndexedMood.normalize(mood.getName()).equals(name))
            throw new IllegalArgumentException("mood names do not match") ;

        if (totalSamples == 0)
            throw new IllegalArgumentException("Already removed all samples") ;

        totalSamples -- ;

        if (totalSamples == 0) {
            totalArousal = 0;
            totalValence = 0;
        } else {
            totalArousal -= mood.getArousal();
            totalValence -= mood.getValence();
        }

        updateAverages();
    }


    private void updateAverages() {

        averageValenceAndArousal = new double[2] ;

        if (totalSamples == 0) {
            averageValenceAndArousal[0] = 0;
            averageValenceAndArousal[1] = 0;
        } else {
            averageValenceAndArousal[0] = totalValence / totalSamples;
            averageValenceAndArousal[1] = totalArousal / totalSamples;
        }

        this.modifiedAt = new Date() ;
    }

    @ApiModelProperty(value="A name that uniquely identifies this mood")
    public String getName() {
        return name;
    }

    @ApiModelProperty(value="true if this mood was sourced from official mood lists, otherwise false")
    public boolean isCanonical() {
        return canonical;
    }

    /*
    public double getTotalValence() {
        return totalValence;
    }

    public double getTotalArousal() {
        return totalArousal;
    }
    */

    @ApiModelProperty(value="A score that ranges from -1 (highly negative, sad) to +1 (highly positive, happy), averaged across all samples")
    public double getAverageValence() {
        return averageValenceAndArousal[0] ;
    }

    @ApiModelProperty(value="A score that ranges from -1 (calming, tiring) to +1 (exciting, agitating), averaged across all samples")
    public double getAverageArousal() {
        return averageValenceAndArousal[1] ;
    }

    @ApiModelProperty(value="The total number of times this mood has been logged within user's experiences")
    public int getTotalSamples() {
        return totalSamples;
    }

    @ApiModelProperty(value="The last time this mood was modified (needed for syncing)")
    public Date getModifiedAt() {
        return modifiedAt;
    }

    @ApiModelProperty(value="True if this mood has been deleted, otherwise false (needed for syncing)")
    public boolean isDeleted() {
        return deleted;
    }

    public static String normalize(String name) {
        return name.toLowerCase().replaceAll("[\\W]", "-") ;
    }
}
