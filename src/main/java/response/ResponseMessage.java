package response;

/**
 * Class to represent response to user
 * <p>
 * Created by ekal on 5/24/16.
 */
public class ResponseMessage {
    private final String message;
    private final int status;

    public ResponseMessage(final String message, final int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public int getStatus() {
        return this.status;
    }
}
