package utils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

import static resources.Colors.*;

public class SystemUtilities {
    Printer log = new Printer(SystemUtilities.class);

    public boolean portIsAvailable(int port) {
        log.new Info("Checking availability of port " + PURPLE + port + RESET);
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            log.new Success("Port is available!");
            return true;
        }
        catch (IOException ignored) {}
        finally {
            if (ds != null) ds.close();
            if (ss != null) {
                try {ss.close();}
                catch (IOException ignored) {}
            }
        }
        return false;
    }
}
