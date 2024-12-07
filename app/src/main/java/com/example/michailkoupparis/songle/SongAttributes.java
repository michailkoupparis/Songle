package com.example.michailkoupparis.songle;


import java.util.ArrayList;
import java.util.List;


// Class-Object for holding Songs's Attributes
public class SongAttributes {

    public List<String> numbers ;
    public List<String> titles ;
    public List<String> artists;
    public List<String> links ;


    // Class constructor
    public SongAttributes(List<String> numbers, List<String> titles, List<String> artists, List<String> links){
        this.numbers= numbers;
        this.titles= titles;
        this.artists= artists;
        this.links= links;
    }

    // Class constructor
    public SongAttributes(){
        this.numbers= new ArrayList<>();
        this.titles= new ArrayList<>();
        this.artists= new ArrayList<>();
        this.links= new ArrayList<>();
    }


    //Return Songs's Numbers
    public List<String> getNumbers(){
        return numbers;
    }

    //Return Songs'Titles
    public List<String> getTitles(){
        return titles;
    }

    //Return Songs's Artists
    public List<String> getArtists(){
        return artists;
    }

    //Return Songs's Links
    public List<String> getLinks(){
        return links;
    }



}
