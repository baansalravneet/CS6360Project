package com.davisbase;

import com.davisbase.config.Settings;
import com.davisbase.services.CommandHandler;
import com.davisbase.services.Component;
import com.davisbase.services.DisplayManager;
import com.davisbase.services.Mediator;
import com.davisbase.services.Prompt;
import com.davisbase.services.QueryParser;
import com.davisbase.utils.Utils;

public class DavisBaseApplication {

	public static void main(String[] args) {
		// initialisations
		Mediator mediatorService = new Mediator();
		Prompt promptService = new Prompt(mediatorService);
		QueryParser queryParser = new QueryParser(mediatorService);
		CommandHandler commandHandler = new CommandHandler(mediatorService);
		DisplayManager displayManager = new DisplayManager(mediatorService);

		mediatorService.addPromptComponent(promptService);
		mediatorService.addQueryParserComponent(queryParser);
		mediatorService.addCommandHandlerComponent(commandHandler);
		mediatorService.addDisplayManagerComponent(displayManager);

		// splash screen
		Utils.splashScreen();

		// service loop
		while (!Settings.isExit()) {
			promptService.showPrompt();
		}
	}

}
