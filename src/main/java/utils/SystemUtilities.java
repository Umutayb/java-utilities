package utils;

import static utils.StringUtilities.Color.*;
import java.io.IOException;
import java.net.Socket;

public class SystemUtilities {
    Printer log = new Printer(SystemUtilities.class);

    /**
     * Checks if the specified port is available on the local host.
     *
     * @param port the port number to check for availability
     * @return true if the port is available, false otherwise
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
