package controllers;

import play.*;
import play.mvc.*;
import play.data.validation.*;
import play.libs.*;
import play.cache.*;

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
    //before displaying a comment form we will generate a unique ID for the captcha
    public static void show(Long id) {
        Post post = Post.findById(id);
        String randomID = Codec.UUID();
        render(post, randomID);
    }

    //post comment + validation for form and captcha + success message
    public static void postComment(
            Long postId,
            @Required(message="Author is required") String author,
            @Required(message="A message is required") String content,
            @Required(message="Please type the code") String code,
            String randomID)
    {
        Post post = Post.findById(postId);
        validation.equals(
                code, Cache.get(randomID)
        ).message("Invalid code. Please type it again");
        if(validation.hasErrors()) {
            render("Application/show.html", post, randomID);
        }
        post.addComment(author, content);
        flash.success("Thanks for posting %s", author);
        Cache.delete(randomID);
        show(postId);
    }

    //can pass the captcha object directly to the renderBinary() method because the Images.Captcha
    //class implements java.io.InputStream.
    //added state management via cache
    public static void captcha(String id) {
        Images.Captcha captcha = Images.captcha();
        String code = captcha.getText("#E4EAFD");
        Cache.set(id, code, "10mn");
        renderBinary(captcha);
    }

}