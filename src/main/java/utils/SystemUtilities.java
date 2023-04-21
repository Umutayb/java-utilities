package utils;

import static utils.StringUtilities.Color.*;

import java.io.File;
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
        log.info("Checking availability of port " + PURPLE + port + RESET);
        try (Socket socket = new Socket("localhost", port)) {
            log.warning("Port is unavailable!");
            socket.close();
            return false;
        }
        catch (IOException e) {
            log.success("Port is available!");
            return true;
        }
    }

    public static class TerminalUtilities {

        private final Printer log = new Printer(TerminalUtilities.class);

        /**
         * Executes the specified command as a new process and waits for it to complete.
         *
         * @param command the command to execute
         * @throws IOException if an I/O error occurs
         * @throws InterruptedException if the current thread is interrupted while waiting for the process to complete
         */
        public void runCommand(String command) throws IOException, InterruptedException {

            log.info("Running command: " + command);

            //String homeDirectory = System.getProperty("user.home");

            ProcessBuilder processBuilder = new ProcessBuilder();
            boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
            if (isWindows) processBuilder.command("cmd.exe", "/c", command);
            else processBuilder.command("sh", "-c", command);

            processBuilder.directory(new File("."));
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            //Process process = processBuilder.inheritIO().start();
            Process process = processBuilder.start();
            process.waitFor();
            //BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            //reader.lines().forEach(System.out::println);
        }
    }
}
