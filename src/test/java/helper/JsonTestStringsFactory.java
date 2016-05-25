package helper;

/**
 * Created by ekal on 5/24/16.
 */
public class JsonTestStringsFactory {
    public static final String VALID_LONG_JSON = "{'id':'1630215c-2608-44b9-aad4-9d56d8aafd4c'," +
            "'firstName':'Dorris'," +
            "'lastName':'Keeling'," +
            "'email':'Darby_Leffler68@gmail.com'," +
            "'address':{" +
            "'street':'193 Talon Valley'," +
            "'city':'South Tate furt'," +
            "'zip':'47069'," +
            "'state':'IA'," +
            "'country':'US'}," +
            "'dateCreated':'2016-03-15T07:02:40.896Z'," +
            "'company':{" +
            "'name':'Denesik Group'," +
            "'website':'http://jodie.org'}," +
            "'profilePic':'http://lorempixel.com/640/480/people'}";

    public static final String VALID_LONG_JSON_UPDATED = "{'id':'1630215c-2608-44b9-aad4-9d56d8aafd4c'," +
            "'firstName':'Test'," +
            "'lastName':'Update'," +
            "'email':'Darby_Leffler68@gmail.com'," +
            "'address':{" +
            "'street':'193 Talon Valley'," +
            "'city':'South Tate furt'," +
            "'zip':'47069'," +
            "'state':'IA'," +
            "'country':'US'}," +
            "'dateCreated':'2016-06-24T07:02:40.896Z'," +
            "'company':{" +
            "'name':'Denesik Group'," +
            "'website':'http://jodie.org'}," +
            "'profilePic':'http://lorempixel.com/640/480/people'}";

    public static final String VALID_SHORT_JSON = "{'id':'short'," +
            "'firstName':'Ekal'," +
            "'lastName':'Golas'," +
            "'email':'ekalgolas@gmail.com'," +
            "'dateCreated':'2016-06-15T07:02:40.896Z'," +
            "'profilePic':'http://lorempixel.com/640/480/people'}";

    /*
    No ID present
     */
    public static final String INVALID_SHORT_JSON = "{'firstName':'Ekal'," +
            "'lastName':'Golas'," +
            "'email':'ekalgolas@gmail.com'," +
            "'dateCreated':'2016-06-15T07:02:40.896Z'," +
            "'profilePic':'http://lorempixel.com/640/480/people'}";

    public static final String NO_USER_FOUND = "{\n" +
            "  \"message\" : \"No user with id invalidTest found\",\n" +
            "  \"status\" : 404\n" +
            "}";

    public static final String USER_CREATED = "{\n" +
            "  \"message\" : \"User created!!\",\n" +
            "  \"status\" : 200\n" +
            "}";

    public static final String USER_UPDATED = "{\n" +
            "  \"message\" : \"User updated!!\",\n" +
            "  \"status\" : 200\n" +
            "}";

    public static final String USER_REMOVED = "{\n" +
            "  \"message\" : \"User test removed!!\",\n" +
            "  \"status\" : 200\n" +
            "}";

    public static final String GET_ONE_USER = "{\n" +
            "  \"id\" : \"one\",\n" +
            "  \"name\" : {\n" +
            "    \"firstName\" : \"test\"\n" +
            "  }\n" +
            "}";

    public static final String GET_ALL_USERS = JsonTestStringsFactory.GET_ONE_USER + "\n" +
            "{\n" +
            "  \"id\" : \"two\"\n" +
            "}";

    public static final String GET_ERROR = "{\n" +
            "  \"message\" : \"test exception\",\n" +
            "  \"status\" : 400\n" +
            "}";
}
