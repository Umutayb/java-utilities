package utils;

import java.io.IOException;
import java.net.Socket;

import static resources.Colors.*;

public class SystemUtilities {
    Printer log = new Printer(SystemUtilities.class);

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
