package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    @Before
    static void addDefaults() {
        renderArgs.put("blogTitle", Play.configuration.getProperty("blog.title"));
        renderArgs.put("blogBaseline", Play.configuration.getProperty("blog.baseline"));
    }

    public static void index() {
        Post frontPost = Post.find("order by postedAt desc").first();
        List<Post> olderPosts = Post.find(
                "order by postedAt desc"
        ).from(1).fetch(10);
        render(frontPost, olderPosts);
    }

    //If we try to send an id HTTP parameter that is not a valid number, the id variable value will
    // be null and Play will automatically add a validation error to the errors container.
    public static void show(Long id) {
        Post post = Post.findById(id);
        render(post);
    }

    //reuse addComment() from Post class
    public static void postComment(Long postId, String author, String content) {
        Post post = Post.findById(postId);
        post.addComment(author, content);
        show(postId);
    }

}