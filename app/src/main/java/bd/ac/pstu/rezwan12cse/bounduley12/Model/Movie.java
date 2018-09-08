package bd.ac.pstu.rezwan12cse.bounduley12.Model;

/**
 * Created by Rezwan on 03-04-18.
 */

public class Movie {
    private String title, thumbnailUrl;
    private String year;
    private String rating;
    //private ArrayList<String> genre;
    private int genre;
    private String details;

    public Movie() {
    }

    public Movie(String name, String thumbnailUrl, String year, String rating,
                 int genre, String details) {
        this.title = name;
        this.thumbnailUrl = thumbnailUrl;
        this.year = year;
        this.rating = rating;
        this.genre = genre;
        this.details = details;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getDetails(){
        return details;
    }

    public void setDetails(String details){
        this.details = details;
    }
    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getGenre(){
        return genre;
    }

    public void setGenre(int genre){
        this.genre = genre;
    }

    /*
    public ArrayList<String> getGenre() {
        return genre;
    }

    public void setGenre(ArrayList<String> genre) {
        this.genre = genre;
    }

    */
}
