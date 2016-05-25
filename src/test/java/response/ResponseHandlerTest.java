package response;

import com.fasterxml.jackson.core.JsonProcessingException;
import helper.JsonTestStringsFactory;
import org.bson.Document;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import user.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for response handler
 * <p>
 * Created by ekal on 5/24/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ResponseHandlerTest {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandlerTest.class);
    private static ResponseHandler handler;
    private static Request request;
    private static Response response;
    private static UserService userService;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    /**
     * Setup all mocks
     *
     * @throws Exception
     */
    @BeforeClass
    public static final void setUp() throws Exception {
        ResponseHandlerTest.logger.info("Mocking classes..");
        ResponseHandlerTest.userService = Mockito.mock(UserService.class);
        ResponseHandlerTest.handler = new ResponseHandler(ResponseHandlerTest.userService);
        ResponseHandlerTest.request = Mockito.mock(Request.class);
        ResponseHandlerTest.response = Mockito.mock(Response.class);
        ResponseHandlerTest.logger.info("Mocks created");
    }

    /**
     * Checks if error is thrown when getting users from an empty database
     *
     * @throws Exception
     */
    @Test
    public void testInvalidGetAll() throws Exception {
        // Setup user service to return empty list
        ResponseHandlerTest.logger.info("Mock user service to return empty list");
        Mockito.when(userService.getAllUsers()).thenReturn(new ArrayList<>());

        // Expect error
        this.expectedEx.expect(Exception.class);
        this.expectedEx.expectMessage("Database empty");

        // Try to get response for all users when DB is empty
        ResponseHandlerTest.logger.info("Get response for all users");
        handler.getResponseForAllUsers();
    }

    /**
     * Test pretty print response for valid get all
     *
     * @throws Exception
     */
    @Test
    public void getResponseForAllUsers() throws Exception {
        // Setup user service to return list with nested elements
        ResponseHandlerTest.logger.info("Mock user service to return users...");
        final List<Document> list = new ArrayList<>();
        list.add(new Document().append("id", "one").append("name", new Document().append("firstName", "test")));
        list.add(new Document().append("id", "two"));
        Mockito.when(userService.getAllUsers()).thenReturn(list);

        // Try to get response for all users when DB is empty
        ResponseHandlerTest.logger.info("Mocking complete. Getting response for all users...");
        final String result = handler.getResponseForAllUsers();
        Assert.assertEquals("Result does not match", JsonTestStringsFactory.GET_ALL_USERS, result);
    }

    /**
     * Test response when user not found
     *
     * @throws JsonProcessingException
     */
    @Test
    public void testInvalidGet() throws JsonProcessingException {
        // Setup mock call of request and user service
        ResponseHandlerTest.logger.info("Mock user service and spark request to return users...");
        Mockito.when(userService.getUser(Matchers.any(String.class))).thenReturn(null);
        Mockito.when(request.params(Matchers.any(String.class))).thenReturn("invalidTest");
        Mockito.reset(ResponseHandlerTest.response);

        // Try to get the user
        ResponseHandlerTest.logger.info("Mocking complete. Getting response for invalid user...");
        final String result = handler.getResponseForSingleUser(request, response);

        // Validate that status set and error JSON obtained
        Mockito.verify(response).status(Matchers.any(Integer.class));
        Assert.assertEquals("Result does not match", JsonTestStringsFactory.NO_USER_FOUND, result);
    }

    /**
     * Checks the pretty print response for single user retrieval
     *
     * @throws Exception
     */
    @Test
    public void getResponseForSingleUser() throws Exception {
        // Setup mock for user service and request
        ResponseHandlerTest.logger.info("Mock user service and spark request to return users...");
        final Document document = new Document().append("id", "one")
                                                .append("name", new Document().append("firstName", "test"));
        Mockito.when(userService.getUser(Matchers.any(String.class))).thenReturn(document);
        Mockito.when(request.params(Matchers.any(String.class))).thenReturn("test");

        // Get the response for single user
        ResponseHandlerTest.logger.info("Mocking complete. Getting response for valid user...");
        final String result = handler.getResponseForSingleUser(request, response);

        // Validate
        Assert.assertEquals("Result does not match", JsonTestStringsFactory.GET_ONE_USER, result);
    }

    /**
     * Test creation response
     *
     * @throws Exception
     */
    @Test
    public void create() throws Exception {
        // Setup mock for request
        ResponseHandlerTest.logger.info("Mocking spark request...");
        Mockito.when(request.body()).thenReturn(JsonTestStringsFactory.VALID_LONG_JSON);
        Mockito.reset(ResponseHandlerTest.userService);

        // Get response for create
        ResponseHandlerTest.logger.info("Mocking complete. Getting response for create user...");
        final String result = handler.create(request);

        // Validate call to create and match the response
        Mockito.verify(userService).createUser(Matchers.any(String.class));
        Assert.assertEquals("Result does not match", JsonTestStringsFactory.USER_CREATED, result);
    }

    /**
     * Test update response
     *
     * @throws Exception
     */
    @Test
    public void update() throws Exception {
        // Setup mock for request
        ResponseHandlerTest.logger.info("Mocking spark request...");
        Mockito.when(request.body()).thenReturn(JsonTestStringsFactory.VALID_LONG_JSON);
        Mockito.reset(ResponseHandlerTest.userService);

        // Get response for update
        ResponseHandlerTest.logger.info("Mocking complete. Getting response for update user...");
        final String result = handler.update(request);

        // Validate call to update and match the response
        Mockito.verify(userService).updateUser(Matchers.any(String.class));
        Assert.assertEquals("Result does not match", JsonTestStringsFactory.USER_UPDATED, result);
    }

    /**
     * Test remove response
     *
     * @throws Exception
     */
    @Test
    public void remove() throws Exception {
        // Setup mock for request
        ResponseHandlerTest.logger.info("Mocking spark request...");
        Mockito.when(request.params(Matchers.any(String.class))).thenReturn("test");
        Mockito.reset(ResponseHandlerTest.userService);

        // Get response for remove
        ResponseHandlerTest.logger.info("Mocking complete. Getting response for delete user...");
        final String result = handler.remove(request);

        // Validate call to remove and match the response
        Mockito.verify(userService).removeUser(Matchers.any(String.class));
        Assert.assertEquals("Result does not match", JsonTestStringsFactory.USER_REMOVED, result);
    }

    /**
     * Null test for error response
     */
    @Test
    public void testResponseForPrintError() {
        ResponseHandlerTest.logger.info("Call error response when pretty print error occurs");
        Mockito.reset(ResponseHandlerTest.response);
        ResponseHandlerTest.handler.getResponseForError(null, ResponseHandlerTest.response);

        // Validate
        Mockito.verify(ResponseHandlerTest.response).status(Matchers.any(Integer.class));
        Mockito.verify(ResponseHandlerTest.response).body(null);
    }

    /**
     * Pretty print test for errors
     */
    @Test
    public void getResponseForError() {
        ResponseHandlerTest.logger.info("Get pretty response for errors...");
        Mockito.reset(ResponseHandlerTest.response);
        ResponseHandlerTest.handler.getResponseForError(new Exception("test exception"), ResponseHandlerTest.response);

        // Validate
        Mockito.verify(ResponseHandlerTest.response).status(Matchers.any(Integer.class));
        Mockito.verify(ResponseHandlerTest.response).body(JsonTestStringsFactory.GET_ERROR);
    }
}
