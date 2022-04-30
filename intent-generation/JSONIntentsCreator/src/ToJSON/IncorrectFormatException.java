package ToJSON;

/**
 * This class defines an exception that occurs when the supplied files are formatted incorrectly
 */

public class IncorrectFormatException extends Exception {

    public IncorrectFormatException(String errorMessage) {
        super(errorMessage);
    }

}
