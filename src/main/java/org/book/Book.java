package org.book;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Book{

    @JsonProperty("id")
    private int id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("published_year")
    private String published_year;

    @JsonProperty ("author")
    private String author;

    @JsonProperty ("category")
    private Category category;

    public Book(){}

    public Book (String titleIn,String descriptionIn,String published_yearIn,String authorIN, String categoryIn){
        this.title=titleIn;
        this.description=descriptionIn;
        this.published_year=published_yearIn;
        this.author=authorIN;
        this.category= Category.valueOf(categoryIn);

    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPublished_year(){
        return published_year;
    }

    public String getAuthor(){
        return author;
    }

    public Category getCategory(){
        return category;
    }


   /* public String getBookInfo() {
        return String.format ("Title:"+this.title + "Description:"+this.description + "Year:"+this.published_year + "Author:" +this.author+ "Category:"+this.category);
    }*/


    enum Category {
        IT,
        Math,
        Physics,
        Economy;
    }
}




