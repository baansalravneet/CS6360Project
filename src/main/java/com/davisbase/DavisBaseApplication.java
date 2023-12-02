package com.davisbase;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.davisbase.commands.Command;
import com.davisbase.commands.CommandContext;
import com.davisbase.commands.impl.CreateTableCommand;
import com.davisbase.config.Database;
import com.davisbase.config.Settings;
import com.davisbase.models.Table;
import com.davisbase.services.Mediator;
import com.davisbase.services.components.impl.CommandHandler;
import com.davisbase.services.components.impl.DisplayManager;
import com.davisbase.services.components.impl.Prompt;
import com.davisbase.services.components.impl.QueryParser;
import com.davisbase.utils.Utils;

public class DavisBaseApplication {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		// initialisations
		Mediator mediatorService = new Mediator();
		Prompt promptService = new Prompt(mediatorService);
		QueryParser queryParser = new QueryParser(mediatorService);
		CommandHandler commandHandler = new CommandHandler(mediatorService);
		DisplayManager displayManager = new DisplayManager(mediatorService);

		// mediator setup
		mediatorService.addPromptComponent(promptService);
		mediatorService.addQueryParserComponent(queryParser);
		mediatorService.addCommandHandlerComponent(commandHandler);
		mediatorService.addDisplayManagerComponent(displayManager);

		// create meta-data tables if they do not exist. Otherwise, read from file
		initialise();

		CommandContext c = new CommandContext();
		c.setTableName("something");
		Command command = new CreateTableCommand(c);
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();
		command.execute();


		// splash screen
		Utils.splashScreen();

		// service loop
		while (!Settings.isExit()) {
			promptService.showPrompt();
		}
	}
	private static void initialise() throws FileNotFoundException, IOException {
		Database.setTablesTable(new Table(Settings.TABLES_TABLE));
		Database.setColumnsTable(new Table(Settings.COLUMNS_TABLE));
		// TOOD: if there are tables in the TablesTable, add those to the Database class
	}

}
