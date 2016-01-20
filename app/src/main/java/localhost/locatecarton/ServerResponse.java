package localhost.locatecarton;

/**
 * Created by maheshwarip on 12-01-2016.
 */
public class ServerResponse {
    public int getError() {
        return Error;
    }

    public void setError(int error) {
        Error = error;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    int Error;
    String Message;
}
