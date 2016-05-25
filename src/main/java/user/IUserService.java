package user;

import org.bson.Document;

import java.util.List;

/**
 * Abstract definition of supported operations
 * Created by ekal on 5/23/16.
 */
public interface IUserService {
    /**
     * Returns a list of all users
     *
     * @return List of documents for each user
     */
    List<Document> getAllUsers();

    /**
     * Returns a single users
     *
     * @param id
     *         ID of the user
     * @return User with the id specified, null if not present
     */
    Document getUser(String id);

    /**
     * Creates a user
     *
     * @param json
     *         JSON string
     */
    void createUser(String json);

    /**
     * Updates a specific user
     *
     * @param json
     *         JSON string
     */
    void updateUser(String json) throws Exception;

    /**
     * Removes a user
     *
     * @param id
     *         ID of the user to be removed
     */
    void removeUser(String id) throws Exception;
}
