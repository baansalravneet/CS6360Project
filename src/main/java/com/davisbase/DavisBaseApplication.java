package com.davisbase;

import com.davisbase.services.Mediator;
import com.davisbase.services.Prompt;
import com.davisbase.utils.Utils;

public class DavisBaseApplication {

	public static void main(String[] args) {
		// initialisations
		Prompt promptService = new Prompt();
		Mediator mediatorService = new Mediator();
	
		// splash screen
		Utils.splashScreen();
	}

}
