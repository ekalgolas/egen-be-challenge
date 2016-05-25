package user;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to implement user services
 * Created by ekal on 5/24/16.
 */
public class UserService implements IUserService {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final MongoCollection<Document> dbCollection;

    /**
     * Constructor to connect to MongoDB and get DB collection
     *
     * @param database
     *         Database to connect to
     * @param collection
     *         Collection to fetch
     */
    public UserService(final String database, final String collection) {
        this(UserService.getCollection(database, collection));
    }

    /**
     * Constructor for unit testing
     *
     * @param dbCollection
     *         DB collection
     */
    public UserService(final MongoCollection<Document> dbCollection) {
        this.dbCollection = dbCollection;

        // Create Index on ID
        final Document key = new Document("id", 1);
        final IndexOptions options = new IndexOptions();
        options.unique(true);
        options.sparse(true);
        this.dbCollection.createIndex(key, options);
    }

    /**
     * Get collection from Mongo DB
     *
     * @param database
     *         Name of database
     * @param collection
     *         Name of collection
     * @return Mongo Collection
     */
    public static MongoCollection<Document> getCollection(final String database, final String collection) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost"));
        return mongoClient.getDatabase(database).getCollection(collection);
    }

    @Override
    public List<Document> getAllUsers() {
        this.logger.info("[GET] Getting all users...");
        final List<Document> documents = new ArrayList<>();
        final MongoCursor<Document> cursor = this.dbCollection.find().projection(Projections.excludeId()).iterator();
        while (cursor.hasNext()) {
            documents.add(cursor.next());
        }

        return documents;
    }

    @Override
    public Document getUser(final String id) {
        this.logger.info("[GET] Getting user with ID " + id);
        final Document document = this.dbCollection.find(Filters.eq("id", id)).projection(Projections.excludeId())
                                                   .first();
        return document;
    }

    @Override
    public void createUser(final String json) {
        this.logger.info("[CREATE] Parsing JSON....");
        final Document document = Document.parse(json);

        this.logger.info("[CREATE] JSON parsed as: " + document.toJson());
        this.logger.info("[CREATE] Creating user...");
        this.dbCollection.insertOne(document);
    }

    @Override
    public void updateUser(final String json) throws Exception {
        // Find the user by ID
        this.logger.info("[UPDATE] Parsing JSON....");
        final Document document = Document.parse(json);

        final String id = document.getString("id");
        this.logger.info("[UPDATE] Finding user with ID " + id);
        final Document user = this.dbCollection.find(Filters.eq("id", id)).first();

        // If found, update
        if (user != null) {
            this.logger.info("[UPDATE] User found! Updating...");
            this.dbCollection.updateOne(user, new Document("$set", document));
        } else {
            // Else, throw an error
            this.logger.warn("[UPDATE] User not found");
            throw new Exception("User not found to update");
        }
    }

    @Override
    public void removeUser(final String id) throws Exception {
        this.logger.info("[REMOVE] Finding user with ID " + id);
        final Document user = this.dbCollection.find(Filters.eq("id", id)).first();

        // If found, remove
        if (user != null) {
            this.logger.info("[REMOVE] User found! Removing...");
            this.dbCollection.deleteOne(user);
        } else {
            // Else, throw an error
            this.logger.warn("[REMOVE] User not found");
            throw new Exception("User not found to remove");
        }
    }
}
