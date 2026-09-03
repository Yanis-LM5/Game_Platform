package GamePlatformY5;

/**
 * Entry point for the GamePlatform application.
 * Initialises the platform and launches the console UI.
 */
public class Main {

    /**
     * Application entry point.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        Platform platform = new Platform();
        ConsoleUI ui = new ConsoleUI(platform);
        ui.start();
    }
}