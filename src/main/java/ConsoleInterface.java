import org.apache.commons.cli.*;

import java.io.*;

public class ConsoleInterface {

    public static void main(String[] args) {
        Option pack = new Option("p", "pack", true, "Puts your file into archive");
        pack.setArgs(2);
        Option unpack = new Option("u", "unpack", true, "Takes your file from archive");
        unpack.setArgs(2);
        OptionGroup group = new OptionGroup();
        group.addOption(pack);
        group.addOption(unpack);
        Options posixOptions = new Options();
        posixOptions.addOptionGroup(group);
        CommandLineParser cmdLinePosixParser = new DefaultParser();
        try {
            CommandLine commandLine = cmdLinePosixParser.parse(posixOptions, args);
            if (commandLine.hasOption("p")) {
                try (InputStream is = new FileInputStream(commandLine.getOptionValues("p")[0]);
                     OutputStream os = new CompressionOutputStream(new FileOutputStream(commandLine.getOptionValues("p")[1]))) {
                    int d;
                    while ((d = is.read()) != -1) {
                        os.write(d);
                    }
                    os.flush();
                    System.out.println("Successfully created archive " + commandLine.getOptionValues("p")[1] + " from file " + commandLine.getOptionValues("p")[0]);
                } catch (FileNotFoundException e) {
                    System.out.println("File not found");
                } catch (IOException e) {
                    System.out.println("input/output error");
                }
                //...
            } else if (commandLine.hasOption("u")) {
                String[] arguments = commandLine.getOptionValues("u");
                //...
            } else {
                //error
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
