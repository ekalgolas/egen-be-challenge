package response;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import user.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle the responses to the user
 * <p>
 * Created by ekal on 5/24/16.
 */
public class ResponseHandler {
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

    /**
     * Constructor
     *
     * @param database
     *         Database to connect to
     * @param collection
     *         Collection to fetch
     */
    public ResponseHandler(final String database, final String collection) {
        this(new UserService(database, collection));
    }

    /**
     * Constructor for unit testing
     *
     * @param userService
     *         User service
     */
    public ResponseHandler(final UserService userService) {
        this.userService = userService;
    }

    /**
     * JSON output for all users, error if no users present
     *
     * @return Response as String
     * @throws Exception
     */
    public String getResponseForAllUsers() throws Exception {
        // Get all users
        final List<Document> allUsers = this.userService.getAllUsers();

        // If no users exists, error
        if (allUsers.size() == 0) {
            this.logger.warn("[GET] No users exist");
            throw new Exception("Database empty!!");
        } else {
            // Else pretty print each user
            this.logger.info("[GET] Found " + allUsers.size() + " users...");
            final List<String> users = new ArrayList<>();
            for (final Document document : allUsers) {
                users.add(this.prettyPrint(document));
            }

            return String.join("\n", users);
        }
    }

    /**
     * Pretty print in JSON
     *
     * @param object
     *         Object to print
     * @return JSON string
     * @throws JsonProcessingException
     */
    private String prettyPrint(final Object object) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

    /**
     * Get JSON for a single user
     *
     * @param req
     *         Request containing the ID
     * @param res
     *         Response data
     * @return JSON string if found, else error
     * @throws JsonProcessingException
     */
    public String getResponseForSingleUser(final Request req, final Response res) throws JsonProcessingException {
        // Get the user
        final String id = req.params(":id");
        final Document user = this.userService.getUser(id);

        // If found, return user details
        if (user != null) {
            this.logger.info("[GET] Found user with id: " + id);
            return this.prettyPrint(user);
        }

        // Else, return error
        res.status(404);
        this.logger.warn("[GET] User not found");
        return this.prettyPrint(new ResponseMessage("No user with id " + id + " found", 404));
    }

    /**
     * Create user
     *
     * @param req
     *         Request containing JSON
     * @return Success response
     * @throws JsonProcessingException
     */
    public String create(final Request req) throws JsonProcessingException {
        this.userService.createUser(req.body());
        this.logger.info("[CREATE] User created!");
        return this.prettyPrint(new ResponseMessage("User created!!", 200));
    }

    /**
     * Update user
     *
     * @param req
     *         Request containing JSON
     * @return Success response
     * @throws Exception
     */
    public String update(final Request req) throws Exception {
        this.userService.updateUser(req.body());
        this.logger.info("[UPDATE] User updated!");
        return this.prettyPrint(new ResponseMessage("User updated!!", 200));
    }

    /**
     * Remove user
     *
     * @param req
     *         Request containing JSON
     * @return Success response
     * @throws Exception
     */
    public String remove(final Request req) throws Exception {
        // Get the user
        final String id = req.params(":id");

        this.userService.removeUser(id);
        this.logger.info("[REMOVE] User " + id + " removed");
        return this.prettyPrint(new ResponseMessage("User " + id + " removed!!", 200));
    }

    /**
     * Gets user friendly response for exceptions
     *
     * @param e
     *         Exception
     * @param res
     *         Response to be set
     * @return Response with modified body
     */
    public Response getResponseForError(final Exception e, final Response res) {
        res.status(400);
        try {
            // Pretty print error
            this.logger.warn(e.getMessage());
            res.body(this.prettyPrint(new ResponseMessage(e.getMessage(), 400)));
        } catch (final JsonProcessingException e1) {
            // If pretty print failed, set errors in string
            this.logger.error(e1.getMessage());
            res.body(e1.getMessage() + "\n" + e.getMessage());
        }

        return res;
    }
}
