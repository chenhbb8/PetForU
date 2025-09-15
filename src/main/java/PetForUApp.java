import controller.ArgsController;
import controller.ConsoleController;
import controller.PetManager;
import java.io.File;
import java.util.Scanner;

public final class PetForUApp {

    private PetForUApp() {}

    public static void main(String[] args) {
        try {
            ArgsController controller = new ArgsController(args);
            if (controller.isHelp()) {
                System.out.println(controller.getHelp());
                return;
            }

            Scanner scanner = new Scanner(System.in);

            System.out.println("Welcome to PetMatcher! Please enter your preferences.");
            System.out.println("Please answer Y or N for the following:");

            int iScore = askYesNo(scanner,
                    "After a long week, do you recharge better by spending time alone rather than going out with friends?");
            int sScore = askYesNo(scanner,
                    "Do you find yourself focusing more on facts and details than on big-picture ideas?");
            int tScore = askYesNo(scanner,
                    "When helping a friend, do you often try to find practical solutions before offering emotional support?");
            int jScore = askYesNo(scanner,
                    "Do you like having a clear plan for your day rather than going with the flow?");

            System.out.print("What is your gender (Male/Female/Other): ");
            String userGender = scanner.nextLine();

            System.out.print("Preferred pet gender (Male/Female/Any): ");
            String preferredPetGender = scanner.nextLine();

            int energyLevel = askPositiveInt(scanner, "Your personal energy level (1 to 10): ");
            double space = askPositiveDouble(scanner, "Space available for your new pet in square feet: ");
            double budget = askPositiveDouble(scanner, "Monthly pet care budget in USD: ");
            int allergy = askYesNo(scanner, "Do you or anyone in your home have pet allergies?");
            int hasYard = askYesNo(scanner, "Do you have a yard or outdoor space?");
            double timePerDay = askPositiveDouble(scanner, "Time available for your new pet per day (hours): ");

            PetManager manager = new PetManager(
                    userGender, preferredPetGender,
                    iScore, sScore, tScore, jScore,
                    energyLevel, space, budget, allergy == 1, hasYard == 1, timePerDay
            );

            ConsoleController consoleController = new ConsoleController(manager.getUser(), new model.CompatibilityCalculator());

            boolean running = true;
            while (running) {
                System.out.println("\nMenu:");
                System.out.println("1. View best matched pet");
                System.out.println("2. View recommended pets (80%+ compatibility)");
                System.out.println("3. View all pets with compatibility scores");
                System.out.println("4. Search for a pet by type or breed");
                System.out.println("Q. Quit");

                System.out.print("Your choice: ");
                String input = scanner.nextLine().trim();

                switch (input) {
                    case "1" -> consoleController.displayBestMatch(manager.getCsvPath());
                    case "2" -> consoleController.displayRecommendedPets(manager.getCsvPath());
                    case "3" -> consoleController.displayAllPetsWithScores(manager.getCsvPath());
                    case "4" -> {
                        System.out.print("Enter the pet type (Dog, Cat, Hamster, Parrot): ");
                        String petType = scanner.nextLine().trim();

                        System.out.print("Enter the breed of the pet (e.g., Syrian, Goldfish, Labrador — leave empty for any breed): ");
                        String petBreed = scanner.nextLine().trim();

                        new model.PetSearcher(consoleController)
                                .searchAndDisplay(petType, petBreed, manager.getCsvPath());
                    }
                    case "Q", "q" -> running = false;
                    default -> System.out.println("Invalid option. Please choose 1, 2, 3, 4, or Q.");
                }
            }

            System.out.println("Thank you for using PetMatcher! Goodbye.");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int askYesNo(Scanner scanner, String question) {
        while (true) {
            System.out.print(question + " (Y/N): ");
            String response = scanner.nextLine().trim().toUpperCase();
            if (response.equals("Y")) return 1;
            if (response.equals("N")) return 0;
            System.out.println("Please enter 'Y' or 'N'.");
        }
    }

    private static int askPositiveInt(Scanner scanner, String question) {
        while (true) {
            try {
                System.out.print(question);
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value > 0) return value;
                System.out.println("Please enter a positive number.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a positive integer.");
            }
        }
    }

    private static double askPositiveDouble(Scanner scanner, String question) {
        while (true) {
            try {
                System.out.print(question);
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value > 0) return value;
                System.out.println("Please enter a positive number.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a positive number.");
            }
        }
    }
}
