package org.example.cli;

import org.example.cli.commands.record.AddCommand;
import org.example.cli.commands.record.DeleteCommand;
import org.example.cli.commands.record.SearchCommand;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.Scanner;


@Command(name = "SQL CLI", description = "A simple CLI",
        mixinStandardHelpOptions = true, version = "1.0")
public class SQLCLI implements Runnable
{
    private static final CommandLine cmd = new CommandLine(new SQLCLI())
            .addSubcommand("add", new AddCommand())
            .addSubcommand("delete", new DeleteCommand())
            .addSubcommand("search", new SearchCommand());

    public static void main( String[] args )
    {
        new CommandLine(new SQLCLI()).execute(args); // Start the CLI
    }

    public void run() {
        System.out.println("\nWelcome to the MiniSQL. Type 'exit' to quit");
        runCommandLoop();
    }

    private void runCommandLoop() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("\u001B[1m\u001B[36mdbcli> \u001B[0m");
            String userInput = sc.nextLine().trim();

            if (userInput.equalsIgnoreCase("exit")) {
                System.out.println("Exiting CLI...");
                break;
            }
            if (userInput.isEmpty()) {
                continue;
            }
            processSQLCommand(userInput);
        }
    }

    private void processSQLCommand(String userInput) {
        try {
            cmd.execute(userInput.split(" "));
        } catch (Exception e) {
            System.out.println("Invalid command. Type 'help' for available commands.");
        }
    }
}
