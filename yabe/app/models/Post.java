package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Post extends Model {
    public String title;
    public Date postedAt;

    //large text database type
    @Lob
    public String content;

    //@ManyToOne used so each post is authored by single User, and each User
    //can author several Post instances
    @ManyToOne
    public User author;

    public Post(User author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.postedAt = new Date();
    }
}