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

}
