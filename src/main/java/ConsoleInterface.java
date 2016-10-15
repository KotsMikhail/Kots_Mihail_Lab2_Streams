import org.apache.commons.cli.*;

import java.io.*;

public class ConsoleInterface {

    public static void main(String[] args) {
        Option pack = new Option("p", "pack", true, "Puts your file into archive");
        pack.setArgs(1);
        pack.setArgName("path");
        Option unpack = new Option("u", "unpack", true, "Takes your file from archive");
        unpack.setArgs(1);
        unpack.setArgName("path");
        OptionGroup group = new OptionGroup();
        group.addOption(pack);
        group.addOption(unpack);
        Options posixOptions = new Options();
        posixOptions.addOptionGroup(group);
        CommandLineParser cmdLinePosixParser = new DefaultParser();
        try {
            CommandLine commandLine = cmdLinePosixParser.parse(posixOptions, args);
            if (commandLine.hasOption("p")) {
                String[] arguments = commandLine.getOptionValues("p");
                try (InputStream is = new FileInputStream(arguments[0]);
                     OutputStream os = new CompressionOutputStream(new FileOutputStream(arguments[1]))) {
                    int d;
                    while ((d = is.read()) != -1) {
                        os.write(d);
                    }
                    os.flush();
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

        //String str = "hello hello hello";
        //String code = new String(LZ77.compress(str.getBytes()));
        //String res = new String(LZ77.decompress(code.getBytes(), str.length()));
        //System.out.println(code);
        //System.out.println(res);
    }

}
