import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

    @Test
    public void tryConnectAsUser() {
        new User("bob@yahoo.com", "secret", "Bob").save();

        //Test
        assertNotNull(User.connect("bob@yahoo.com", "secret"));
        assertNull(User.connect("bob@yahoo.com", "badpassword"));
        assertNull(User.connect("tom@yahoo.com", "secret"));
    }

}
