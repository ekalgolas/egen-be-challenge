package driver;

import helper.JsonTestStringsFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;
import spark.utils.IOUtils;
import user.UserService;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Test flow for create, get, update, remove and error
 * <p>
 * Created by ekal on 5/25/16.
 */
public class AppControllerIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(AppControllerIntegrationTest.class);
    private static final String TEST = "test";

    /**
     * Start server
     *
     * @throws Exception
     */
    @BeforeClass
    public static void setUp() throws Exception {
        logger.info("Start Spark server and connect to MongoDB...");
        new AppController(TEST, TEST);
        Thread.sleep(2000);
        logger.info("Server started!");
    }

    /**
     * Remove all the test work and stop server
     *
     * @throws Exception
     */
    @AfterClass
    public static void tearDown() throws Exception {
        logger.info("Removing all the test work..");
        UserService.getCollection(TEST, TEST).drop();
        Spark.stop();
    }

    /**
     * Test flow as empty -> create -> update -> remove = empty
     */
    @Test
    public void testFlow() {
        // First check if DB empty
        String response = this.execute("/users", null, "GET");
        Assert.assertEquals("No users expected", JsonTestStringsFactory.NO_USERS_FOUND, response);

        // Then, create a user
        response = this.execute("/create", JsonTestStringsFactory.VALID_LONG_JSON, "PUT");
        Assert.assertEquals("User not created", JsonTestStringsFactory.USER_CREATED, response);

        // Then, update the user created above
        response = this.execute("/update", JsonTestStringsFactory.VALID_LONG_JSON_UPDATED, "PUT");
        Assert.assertEquals("User not updated", JsonTestStringsFactory.USER_UPDATED, response);

        // Then, remove a non existing user
        response = this.execute("/remove/invalidTest", null, "POST");
        Assert.assertTrue("Non existing user removed", response.contains("User not found to remove"));

        // Then, remove the user
        response = this.execute("/remove/1630215c-2608-44b9-aad4-9d56d8aafd4c", null, "POST");
        Assert.assertTrue("User not removed", response.contains("User 1630215c-2608-44b9-aad4-9d56d8aafd4c removed!!"));

        // Again check if DB empty
        response = this.execute("/users", null, "GET");
        Assert.assertEquals("No users expected", JsonTestStringsFactory.NO_USERS_FOUND, response);
    }

    /**
     * Execute an HTTP request
     *
     * @param targetURL
     *         Relative url corresponding to the service
     * @param json
     *         Optional JSON data
     * @param method
     *         HTTP Method used
     * @return Response as JSON string
     */
    private String execute(final String targetURL, final String json, final String method) {
        HttpURLConnection connection = null;
        try {
            // Create connection
            final URL url = new URL("http://localhost:8000" + targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");

            // Send JSON data
            if (json != null && !json.isEmpty()) {
                connection.setRequestProperty("Content-Length", Integer.toString(json.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");
                connection.setUseCaches(false);
                connection.setDoOutput(true);

                // Send request
                final DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(json);
                wr.close();
            }

            // Get Response
            InputStream inputStream = null;
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            final String body = IOUtils.toString(inputStream);
            return body;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
