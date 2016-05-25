package user;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import helper.JsonTestStringsFactory;
import org.bson.Document;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to test implementation of each user service
 * <p>
 * Created by ekal on 5/24/16.
 */
public class UserServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);
    private static final String TEST = "test";
    private static UserService userService;
    private static MongoCollection<Document> dbCollection;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    /**
     * Create a separate DB for unit testing
     */
    @BeforeClass
    public static void beforeClass() {
        UserServiceTest.logger.info("Creating connection to local MongoDB..");
        UserServiceTest.dbCollection = UserService.getCollection(UserServiceTest.TEST, UserServiceTest.TEST);
        if (UserServiceTest.dbCollection == null) {
            Assert.fail("Cannot get collection from MongoDB");
        }

        UserServiceTest.userService = new UserService(UserServiceTest.dbCollection);
        UserServiceTest.logger.info("Connection created");
    }

    /**
     * Delete all work after testing
     */
    @AfterClass
    public static void afterClass() {
        UserServiceTest.logger.info("Removing all work..");
        UserServiceTest.dbCollection.drop();
        UserServiceTest.logger.info("Done!");
    }

    /**
     * Return 0 documents when DB empty
     */
    @Test
    public void getAllUsers() {
        UserServiceTest.dbCollection.deleteMany(new Document());
        Assert.assertEquals("Number of documents should be 0 when DB is empty", 0,
                UserServiceTest.userService.getAllUsers().size());
    }

    /**
     * Return null for user if it does not exist
     */
    @Test
    public void getUser() {
        Assert.assertNull("Null expected when user not present", UserServiceTest.userService.getUser("testUser"));
    }

    @Test
    public void createDuplicateUser() {
        // Ensure collection is empty
        UserServiceTest.dbCollection.deleteMany(new Document());

        // Expect an error
        this.expectedEx.expect(MongoWriteException.class);
        this.expectedEx.expectMessage("duplicate");

        // Create multiple users
        UserServiceTest.logger.info("Creating user...");
        UserServiceTest.userService.createUser(JsonTestStringsFactory.VALID_LONG_JSON);

        // Check if duplicates are not allowed
        UserServiceTest.logger.info("Creating duplicate user...");
        UserServiceTest.userService.createUser(JsonTestStringsFactory.VALID_LONG_JSON);
    }

    /**
     * Test creation
     */
    @Test
    public void createUser() {
        UserServiceTest.dbCollection.deleteMany(new Document());

        // Create multiple users
        UserServiceTest.logger.info("Creating 2 users...");
        UserServiceTest.userService.createUser(JsonTestStringsFactory.VALID_LONG_JSON);
        UserServiceTest.userService.createUser(JsonTestStringsFactory.VALID_SHORT_JSON);

        // Validate
        Assert.assertEquals("Expected 2 users to be created", 2, UserServiceTest.userService.getAllUsers().size());

        final Document document = UserServiceTest.userService.getUser("short");
        Assert.assertEquals("Fields not created as expected", "Ekal", document.getString("firstName"));
    }

    /**
     * Check if we are able to update invalid user
     *
     * @throws Exception
     */
    @Test
    public void updateInvalidUser() throws Exception {
        // Ensure collection is empty
        UserServiceTest.dbCollection.deleteMany(new Document());

        // Expect an error
        this.expectedEx.expect(Exception.class);
        this.expectedEx.expectMessage("User not found");

        // Create multiple users
        UserServiceTest.logger.info("Creating invalid user...");
        UserServiceTest.userService.updateUser(JsonTestStringsFactory.INVALID_SHORT_JSON);
    }

    /**
     * Test a valid update
     *
     * @throws Exception
     */
    @Test
    public void updateUser() throws Exception {
        UserServiceTest.dbCollection.deleteMany(new Document());

        // Create a user
        UserServiceTest.logger.info("Creating a user..");
        UserServiceTest.userService.createUser(JsonTestStringsFactory.VALID_LONG_JSON);

        // Update the user
        UserServiceTest.logger.info("Updating created user..");
        UserServiceTest.userService.updateUser(JsonTestStringsFactory.VALID_LONG_JSON_UPDATED);

        // Validate update
        final Document document = UserServiceTest.userService.getAllUsers().get(0);
        Assert.assertEquals("Fields not created as expected", "Update", document.getString("lastName"));
    }

    /**
     * Test to remove a user that does not exist
     *
     * @throws Exception
     */
    @Test
    public void removeInvalidUser() throws Exception {
        // Expect error
        this.expectedEx.expect(Exception.class);
        this.expectedEx.expectMessage("User not found");

        // Remove user
        UserServiceTest.userService.removeUser("invalidUser");
    }

    /**
     * Checks removing a existing user
     *
     * @throws Exception
     */
    @Test
    public void removeUser() throws Exception {
        UserServiceTest.dbCollection.deleteMany(new Document());

        // Create a user
        UserServiceTest.logger.info("Creating a user..");
        UserServiceTest.userService.createUser(JsonTestStringsFactory.VALID_SHORT_JSON);

        // Update the user
        UserServiceTest.logger.info("Removing created user..");
        UserServiceTest.userService.removeUser("short");

        // Validate update
        Assert.assertEquals("No users should be present", 0, UserServiceTest.userService.getAllUsers().size());
    }
}
