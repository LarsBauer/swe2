package de.shop.util;

import java.util.Arrays;
import java.util.List;

import de.shop.bestellverwaltung.service.BestellverwaltungTest;
import de.shop.kundenverwaltung.domain.KundeTest;
import de.shop.kundenverwaltung.service.KundenverwaltungTest;

public enum Testklassen {
	INSTANCE;
	
	// Testklassen aus verschiedenen Packages auflisten (durch Komma getrennt):
	// so dass alle darin enthaltenen Klassen ins Web-Archiv mitverpackt werden
	private final List<Class<? extends AbstractTest>> classes = Arrays.asList(BestellverwaltungTest.class,
			                                                                  KundeTest.class,
			                                                                  KundenverwaltungTest.class);
	
	public static Testklassen getInstance() {
		return INSTANCE;
	}
	
	public List<Class<? extends AbstractTest>> getTestklassen() {
		return classes;
	}
}
