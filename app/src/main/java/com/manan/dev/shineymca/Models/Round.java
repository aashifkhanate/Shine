package com.manan.dev.shineymca.Models;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;

public class Round {

    private String Poster, Name, Description, Venue, SpecialNotes, Clubname;
    private long Date, Time, Number;
    private ArrayList<Coordinator> Coordinators;
    private ArrayList<FAQ> Faq;

    public Round() {
    }

    public Round(String poster, String name, String description, String venue, String specialNotes, String clubname, long date, long time, long number, ArrayList<Coordinator> coordinators, ArrayList<FAQ> faq) {
        Poster = poster;
        Name = name;
        Description = description;
        Venue = venue;
        SpecialNotes = specialNotes;
        Clubname = clubname;
        Date = date;
        Time = time;
        Number = number;
        Coordinators = coordinators;
        Faq = faq;
    }

    public String getPoster() {
        return Poster;
    }

    public void setPoster(String poster) {
        Poster = poster;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getVenue() {
        return Venue;
    }

    public void setVenue(String venue) {
        Venue = venue;
    }

    public String getSpecialNotes() {
        return SpecialNotes;
    }

    public void setSpecialNotes(String specialNotes) {
        SpecialNotes = specialNotes;
    }

    public String getClubname() {
        return Clubname;
    }

    public void setClubname(String clubname) {
        Clubname = clubname;
    }

    public long getDate() {
        return Date;
    }

    public void setDate(long date) {
        Date = date;
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        Time = time;
    }

    public ArrayList<Coordinator> getCoordinators() {
        return Coordinators;
    }

    public void setCoordinators(ArrayList<Coordinator> coordinators) {
        Coordinators = coordinators;
    }

    public ArrayList<FAQ> getFaq() {
        return Faq;
    }

    public void setFaq(ArrayList<FAQ> faq) {
        Faq = faq;
    }


    public long getNumber() {
        return Number;
    }

    public void setNumber(long number) {
        Number = number;
    }
}
