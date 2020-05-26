import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

    @Before
    public void setup() {
        Fixtures.deleteDatabase();
    }

    @Test
    public void tryConnectAsUser() {
        new User("bob@yahoo.com", "secret", "Bob").save();

        //Test
        assertNotNull(User.connect("bob@yahoo.com", "secret"));
        assertNull(User.connect("bob@yahoo.com", "badpassword"));
        assertNull(User.connect("tom@yahoo.com", "secret"));
    }

    @Test
    public void createPost() {
        User bob = new User("bob@gmail.com", "secret", "Bob").save();

        new Post(bob, "My first post", "Hello world").save();

        assertEquals(1, Post.count());

        //Retrieve all posts created by Bob
        List<Post> bobPosts = Post.find("byAuthor", bob).fetch();

        //Tests
        assertEquals(1, bobPosts.size());
        Post firstPost = bobPosts.get(0);
        assertNotNull(firstPost);
        assertEquals(bob, firstPost.author);
        assertEquals("My first post", firstPost.title);
        assertEquals("Hello world", firstPost.content);
        assertNotNull(firstPost.postedAt);
    }

    @Test
    public void postComments() {
        User bob = new User("bob@uiowa.edu", "secret", "Bob").save();

        Post bobPost = new Post(bob, "My first post", "Hello world").save();

        new Comment(bobPost, "Teff", "Great post, good effort").save();
        new Comment(bobPost, "Jimothy", "Thanks for the post!").save();

        //Retrieve all comments
        List<Comment> bobPostComments = Comment.find("byPost", bobPost).fetch();

        // Tests
        assertEquals(2, bobPostComments.size());

        Comment firstComment = bobPostComments.get(0);
        assertNotNull(firstComment);
        assertEquals("Teff", firstComment.author);
        assertEquals("Great post, good effort", firstComment.content);
        assertNotNull(firstComment.postedAt);

        Comment secondComment = bobPostComments.get(1);
        assertNotNull(secondComment);
        assertEquals("Jimothy", secondComment.author);
        assertEquals("Thanks for the post!", secondComment.content);
        assertNotNull(secondComment.postedAt);
    }

    @Test
    public void useTheCommentsRelation() {
        // Create a new user and save it
        User bob = new User("bob@gmail.com", "secret", "Bob").save();

        // Create a new post
        Post bobPost = new Post(bob, "My first post", "Hello world").save();

        // Post a first comment
        bobPost.addComment("Jeff", "Nice post");
        bobPost.addComment("Tom", "I knew that !");

        // Count things
        assertEquals(1, User.count());
        assertEquals(1, Post.count());
        assertEquals(2, Comment.count());

        // Retrieve Bob's post
        bobPost = Post.find("byAuthor", bob).first();
        assertNotNull(bobPost);

        // Navigate to comments
        assertEquals(2, bobPost.comments.size());
        assertEquals("Jeff", bobPost.comments.get(0).author);

        // Delete the post
        bobPost.delete();

        // Check that all comments have been deleted
        assertEquals(1, User.count());
        assertEquals(0, Post.count());
        assertEquals(0, Comment.count());
    }

}
