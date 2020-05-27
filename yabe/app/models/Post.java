package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;
import play.data.validation.*;

@Entity
public class Post extends Model {

    @Required
    public String title;

    @Required
    public Date postedAt;

    //large text database type
    @Lob
    @Required
    @MaxSize(10000)
    public String content;

    //@ManyToOne used so each post is authored by single User, and each User
    //can author several Post instances
    @Required
    @ManyToOne
    public User author;

    @OneToMany(mappedBy="post", cascade=CascadeType.ALL)
    public List<Comment> comments;

    @ManyToMany(cascade=CascadeType.PERSIST)
    public Set<Tag> tags;

    public Post(User author, String title, String content) {
        this.comments = new ArrayList<Comment>();
        //use a TreeSet here in order to keep the tag list in a predictable order
        //(alphabetical order in fact, because of our previous compareTo implementation)
        this.tags = new TreeSet<Tag>();
        this.author = author;
        this.title = title;
        this.content = content;
        this.postedAt = new Date();
    }

    public Post addComment(String author, String content) {
        Comment newComment = new Comment(this, author, content);
        this.comments.add(newComment);
        this.save();
        return this;
    }

    public Post tagItWith(String name) {
        tags.add(Tag.findOrCreateByName(name));
        return this;
    }
    //legacy style query parameter: ? changed to ?1
    public static List<Post> findTaggedWith(String tag) {
        return Post.find(
                "select distinct p from Post p join p.tags as t where t.name = ?1", tag
        ).fetch();
    }

    //used to find posts with several tags
    public static List<Post> findTaggedWith(String... tags) {
        return Post.find(
                "select distinct p from Post p join p.tags as t where t.name in (:tags) group by p.id, p.author, p.title, p.content,p.postedAt having count(t.id) = :size"
        ).bind("tags", tags).bind("size", tags.length).fetch();
    }

    //got an error about using just ? instead of ?1
    public Post previous() {
        return Post.find("postedAt < ?1 order by postedAt desc", postedAt).first();
    }

    public Post next() {
        return Post.find("postedAt > ?1 order by postedAt asc", postedAt).first();
    }
}