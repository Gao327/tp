package seedu.main;

import seedu.category.CategoryList;
import seedu.command.AddCategoryCommand;
import seedu.command.ByeCommand;
import seedu.command.Command;
import seedu.command.DeleteCategoryCommand;
import seedu.command.HelpCommand;
import seedu.command.HistoryCommand;
import seedu.command.ViewCategoryCommand;
import seedu.command.ViewExpenseCommand;
import seedu.command.ViewIncomeCommand;
import seedu.command.AddIncomeCommand;
import seedu.command.AddExpenseCommand;
import seedu.command.DeleteTransactionCommand;
import seedu.command.ViewTotalCommand;
import seedu.command.KeywordsSearchCommand;

import seedu.transaction.TransactionList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static final String NAME = "uNivUSaver";
    public static final String HI_MESSAGE = "Hello, %s is willing to help!";
    public static final String INVALID_COMMAND_ERROR_MESSAGE = "Invalid command.";
    public static Scanner scanner; // Scanner for reading user input
    private static final Logger logger = Logger.getLogger("Main");

    // Prefix for message formatting
    private static final String PREFIX = "\t";
    // Separator for message formatting
    private static final String SEPARATOR = "-------------------------------------";

    private static Parser parser; //Parser to parse the commands

    // Singleton CategoryList for use across classes
    private static CategoryList categories; //Category list to store categories

    // Singleton TransactionList for use across classes
    private static TransactionList transactions;

    private static boolean isRunning = true;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        while (isRunning) {
            run();
        }
    }

    /**
     * Setter for the chatbot's running state.
     *
     * @param isRunning A boolean showing if the chatbot should continue running.
     */
    public static void setRunning(boolean isRunning) {
        Main.isRunning = isRunning;
    }

    /**
     * Starts the chatbot and enters the command processing loop.
     */
    public static void run() {
        try {
            start();
            runCommandLoop();
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    /**
     * Starts the chatbot by printing the logo and greeting messages,
     * and sign up the Command objects.
     */
    public static void start() {
        logger.log(Level.INFO, "Starting uNivUSaver...");

        scanner = new Scanner(System.in);
        parser = new Parser();
        categories = new CategoryList();
        transactions = new TransactionList();

        setupCommands();

        printMessage(String.format(HI_MESSAGE, NAME));
    }

    /**
     * Signs up the Command objects.
     */
    private static void setupCommands() {
        assert categories != null : "Categories should be initialized.";
        assert transactions != null : "Transactions should be initialized.";

        HelpCommand helpCommand = new HelpCommand();
        parser.registerCommands(helpCommand);

        parser.registerCommands(new AddCategoryCommand(categories));
        parser.registerCommands(new AddIncomeCommand(transactions));
        parser.registerCommands(new AddExpenseCommand(transactions));

        parser.registerCommands(new DeleteCategoryCommand(categories));
        parser.registerCommands(new DeleteTransactionCommand(transactions));

        parser.registerCommands(new ViewCategoryCommand(categories));
        parser.registerCommands(new ViewExpenseCommand(transactions));
        parser.registerCommands(new ViewIncomeCommand(transactions));
        parser.registerCommands(new ViewTotalCommand(transactions));
        parser.registerCommands(new HistoryCommand(transactions));


        KeywordsSearchCommand keywordsSearchCommand = new KeywordsSearchCommand(transactions);
        parser.registerCommands(keywordsSearchCommand);

        parser.registerCommands(new ByeCommand());


        // Set command list for the help command
        logger.log(Level.INFO, "Setting command list for HelpCommand...");
        helpCommand.setCommands(new ArrayList<>(parser.getCommands().values()));
    }

    /**
     * Main command processing loop that retrieves user commands, processes, and displays the results.
     * The loop continues until the application is stopped.
     */
    private static void runCommandLoop() throws Exception {
        while (isRunning) {
            String commandString = getUserInput();
            String[] commandParts = commandString.split(" ", 2);

            Command command = parser.parseCommand(commandParts[0]);

            if (command == null) {
                List<String> messages = new ArrayList<>();
                messages.add(INVALID_COMMAND_ERROR_MESSAGE);
                showCommandResult(messages);
                continue;
            }

            if (commandParts.length == 2) {
                Map<String, String> arguments = parser.extractArguments(command, commandParts[1]);
                command.setArguments(arguments);
            }

            List<String> messages = command.execute();
            showCommandResult(messages);
        }
    }

    /**
     * Gets the input entered by the user.
     *
     * @return The input entered by the user as a string.
     */
    public static String getUserInput() {
        if (scanner.hasNextLine()) {
            String input = scanner.nextLine();

            // Silently consume all ignored lines (empty commands)
            while (input.trim().isEmpty()) {
                input = scanner.nextLine();
            }
            return input;
        }
        return "";
    }

    /**
     * Prints a single message to the console.
     *
     * @param message The message to be printed.
     */
    public static void printMessage(String message) {
        System.out.println(PREFIX + message);
    }

    /**
     * Prints a message to the console without a new line at the end.
     *
     * @param message The message to be printed.
     */
    public static void printMiddleMessage(String message) {
        System.out.print(PREFIX + message);
    }

    /**
     * Prints multiple messages to the console, each as a separate line.
     *
     * @param messages The list of messages to print.
     */
    public static void printMessages(List<String> messages) {
        messages.forEach(Main::printMessage);
    }

    /**
     * Prints multiple messages to the console, each as a separate line.
     *
     * @param messages The messages to print, provided as a variable-length argument list.
     */
    public static void printMessages(String... messages) {
        for (String m : messages) {
            printMessage(m);
        }
    }

    /**
     * Displays the result of a command execution
     *
     * @param results a list of Strings containing feedback.
     */
    public static void showCommandResult(List<String> results) {
        if (results == null) {
            return;
        }
        printMessage(SEPARATOR); // Print a separator
        printMessages(results); // Print feedback to user
        printMessage(SEPARATOR); // Print another separator
    }
}
