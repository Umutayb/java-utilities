package utils;

import java.io.IOException;
import java.net.Socket;

import static resources.Colors.*;

public class SystemUtilities {
    Printer log = new Printer(SystemUtilities.class);

    /**
     * Checks a given port is available
     * @param port target port
     * @return returns true if available
     */
    public boolean portIsAvailable(int port) {
        log.new Info("Checking availability of port " + PURPLE + port + RESET);
        try (Socket socket = new Socket("localhost", port)) {
            log.new Warning("Port is unavailable!");
            socket.close();
            return false;
        }
        catch (IOException e) {
            log.new Success("Port is available!");
            return true;
        }
    }
}
