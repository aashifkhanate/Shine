package com.manan.dev.shineymca.Models;

public class Club {
    private String clubName;
    private String clubIcon;
    private String clubDescription;
    private String clubType;

    public Club(String clubName, String clubIcon, String clubDescription, String clubType) {
        this.clubName = clubName;
        this.clubIcon = clubIcon;
        this.clubDescription = clubDescription;
        this.clubType = clubType;
    }

    public Club(String clubName, String clubDescription, String clubType) {
        this.clubName = clubName;
        this.clubDescription = clubDescription;
        this.clubType = clubType;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public void setClubIcon(String clubIcon) {
        this.clubIcon = clubIcon;
    }

    public void setClubDescription(String clubDescription) {
        this.clubDescription = clubDescription;
    }

    public String getClubName() {
        return clubName;
    }

    public String getClubIcon() {
        return clubIcon;
    }

    public String getClubDescription() {
        return clubDescription;
    }

    public Club() { // for firebase purpose
    }
}
