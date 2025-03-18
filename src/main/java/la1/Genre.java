package la1;

public enum Genre {
    UNKNOWN("Unknown"),
    POP("Pop"),
    ROCK("Rock"),
    JAZZ("Jazz"),
    HIPHOP("HipHop"),
    CLASSICAL("Classical"),
    COUNTRY("Country"),
    ELECTRONIC("Electronic"),
    FOLK("Folk"),
    BLUES("Blues"),
    LATIN("Latin"),
    REGGAE("Reggae"),
    METAL("Metal"),
    PUNK("Punk"),
    RAP("Rap"),
    RNB("RnB"),
    SOUL("Soul"),
    INDIE("Indie"),
    DANCE("Dance"),
    FUNK("Funk"),
    TECHNO("Techno"),
    DISCO("Disco"),
    SKA("Ska"),
    SWING("Swing"),
    GRUNGE("Grunge"),
    DUBSTEP("Dubstep"),
    HOUSE("House"),
    TRANCE("Trance"),
    DRUM_AND_BASS("DrumAndBass"),
    GARAGE("Garage"),
    AMBIENT("Ambient"),
    DUB("Dub"),
    REGGAETON("Reggaeton"),
    PSYCHEDELIC("Psychedelic"),
    ELECTRO("Electro"),
    ACOUSTIC("Acoustic"),
    OTHER("Other");

    private final String genre;

    Genre(String genre) {
        this.genre = genre;
    }

    public String getGenre() {
        return genre;
    }

    /**
     * Converts a string representation of a genre into the corresponding {@code Genre} enum value.
     * @param genreStr the string representation of the genre
     * @return the matching {@code Genre} if found; {@code UNKNOWN}.
     */
    public static Genre fromString(String genreStr) {
        if (genreStr == null || genreStr.trim().isEmpty()) {
            return UNKNOWN;
        }
        for (Genre genre : Genre.values()) {
            if (genre.getGenre().equalsIgnoreCase(genreStr.trim())) {
                return genre;
            }
        }
        return OTHER;
    }

}
