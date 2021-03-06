package com.mycompany.popmovies;

import java.io.Serializable;

/**
 * Created by Borys on 2016-11-03.
 */

public class Movie implements Serializable{
    private String id;
    private String title;
    private String posterUri;
    private String overview;
    private String rating;
    private String date;
    private String popularity;
    private String runtime;
    static final long serialVersionUID = 42L;





    Movie(){

    }

    Movie(String id, String title, String posterUri, String overview, String rating, String date, String popularity, String runtime){
        this.id = id;
        this.title = title;
        this.posterUri = posterUri;
        this.overview = overview;
        this.rating = rating;
        this.date = date;
        this.popularity = popularity;
        this.runtime = runtime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterUri() {
        return posterUri;
    }

    public void setPosterUri(String posterUri) {
        this.posterUri = posterUri;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }
}
