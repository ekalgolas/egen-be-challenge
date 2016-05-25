package driver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import response.ResponseHandler;
import spark.Spark;

/**
 * Driver class
 * <p>
 * Created by ekal on 5/23/16.
 */
public class AppController {
    private final ResponseHandler responseHandler;
    private final Logger logger = LoggerFactory.getLogger(AppController.class);

    /**
     * Constructor to start Spark and configure services
     *
     * @param database
     *         Database to connect to
     * @param collection
     *         Collection to fetch
     */
    public AppController(final String database, final String collection) {
        // Start spark
        this.logger.info("Start server on port 8000");
        Spark.port(8000);

        // Configure user services
        this.logger.info("Configuring services");
        this.responseHandler = new ResponseHandler(database, collection);
        Spark.get("/users", "application/json", (req, res) -> this.responseHandler.getResponseForAllUsers());
        Spark.get("/users/:id", "application/json",
                (req, res) -> this.responseHandler.getResponseForSingleUser(req, res));
        Spark.put("/create", "application/json", (req, res) -> this.responseHandler.create(req));
        Spark.put("/update", "application/json", (req, res) -> this.responseHandler.update(req));
        Spark.post("/remove/:id", "application/json", (req, res) -> this.responseHandler.remove(req));

        // Configure errors
        Spark.exception(Exception.class, (e, req, res) -> this.responseHandler.getResponseForError(e, res));
        this.logger.info("Services configured");
    }

    /**
     * Driver program
     *
     * @param args
     *         Command line arguments
     */
    public static void main(final String[] args) {
        // Create controller an run an infinite loop
        new AppController("egen", "users");
        while (true) {
        }
    }
}
