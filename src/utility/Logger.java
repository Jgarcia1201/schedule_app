package utility;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;

public class Logger {

    private static final String fileName = "login_activity.txt";

    public Logger() {}

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
