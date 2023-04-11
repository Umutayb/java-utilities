package utils;

import java.io.File;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

public class TerminalUtilities {

    private final Printer log = new Printer(TerminalUtilities.class);

    /**
     * Executes the specified command as a new process and waits for it to complete.
     *
     * @param command the command to execute
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the current thread is interrupted while waiting for the process to complete
     */
    public void runCommand(String command) throws IOException, InterruptedException {

        log.new Info("Running command: " + command);

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
