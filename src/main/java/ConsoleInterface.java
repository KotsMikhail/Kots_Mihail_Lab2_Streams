import org.apache.commons.cli.*;

import java.io.*;

public class ConsoleInterface {

    public static void main(String[] args) {
        Option pack = new Option("p", "pack", true, "Puts your file into archive");
        pack.setArgs(2);
        pack.setArgName("target file> <archive file");
        Option unpack = new Option("u", "unpack", true, "Takes your file from archive");
        unpack.setArgs(2);
        unpack.setArgName("archive file> <result file");
        Option help = new Option("h", "help", false, "Prints this message");
        OptionGroup group = new OptionGroup();
        group.addOption(pack);
        group.addOption(unpack);
        Options posixOptions = new Options();
        posixOptions.addOptionGroup(group);
        posixOptions.addOption(help);
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
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            } else if (commandLine.hasOption("u")) {
                try (InputStream is = new CompressionInputStream(new FileInputStream(commandLine.getOptionValues("u")[0]));
                     OutputStream os = new FileOutputStream(commandLine.getOptionValues("u")[1])) {
                    int d;
                    while ((d = is.read()) != -1) {
                        os.write(d);
                    }
                    os.flush();
                    System.out.println("Successfully unpacked file " + commandLine.getOptionValues("u")[1] + " from archive " + commandLine.getOptionValues("u")[0]);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            } else if (commandLine.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("archiver", posixOptions);
            } else {
                System.out.println("Unknown option");
                System.out.println("Consider using option -h,--help to get the lit of available commands");
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
    }
}
