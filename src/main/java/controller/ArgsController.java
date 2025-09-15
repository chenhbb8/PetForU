package controller;

public class ArgsController {
    private final boolean helpRequested;

    public ArgsController(String[] args) {
        helpRequested = args.length > 0 &&
                (args[0].equals("--help") || args[0].equals("-h"));
    }

    public boolean isHelp() {
        return helpRequested;
    }

    public String getHelp() {
        return """
            Usage: java DNInfoApp [--help]
            
            Options:
              --help, -h    Show this help message
            """;
    }
}
