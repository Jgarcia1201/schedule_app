package utility;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;

public class Logger {

    private static final String fileName = "login_activity.txt";

    public Logger() {}

    /**
     * Creates a Print writer, depending on the true/false value of the login attempt the message is changed to "Failed" or "Successful".
     * The writer then writes the assembled message to a file named login_activity.txt.
     *
     * @param userName - userName of the attempted log in.
     * @param success - Boolean value indicating whether a login attempt was successful or unsuccessful.
     */
    public static void logAttempt(String userName, boolean success) throws IOException {
        try (FileWriter fileW = new FileWriter(fileName, true); BufferedWriter buffWriter = new BufferedWriter(fileW); PrintWriter writer = new PrintWriter(buffWriter)) {
            String successMessage = "Failed";
            if (success) {
                successMessage = "Successful";
            }
            writer.println("Username: " + userName + " Login Attempt at " + Instant.now().toString() + ": " + successMessage);
        }
        catch (Exception e) {
            System.out.println("failed to write to file");
        }
    }
}
