package de.shop.bestellverwaltung.service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Versand;
import de.shop.kundenverwaltung.domain.Kunde;

@Decorator
public abstract class BestellverwaltungMitGeschenkverpackung implements Bestellverwaltung {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	@Delegate
	@Any
	private Bestellverwaltung bestellverwaltung;

	@Override
	public Bestellung findBestellungById(Long id) {
		return bestellverwaltung.findBestellungById(id);
	}

	@Override
	public Bestellung findBestellungByIdMitLieferungen(Long id) {
		return bestellverwaltung.findBestellungByIdMitLieferungen(id);
	}

	@Override
	public Kunde findKundeById(Long id) {
		return bestellverwaltung.findKundeById(id);
	}

	@Override
	public List<Bestellung> findBestellungenByKundeId(Long kundeId) {
		return bestellverwaltung.findBestellungenByKundeId(kundeId);
	}

	@Override
	public Bestellung createBestellung(Bestellung bestellung, Kunde kunde, Locale locale) {
		LOGGER.warning("Geschenkverpackung noch nicht implementiert");
		
		return bestellverwaltung.createBestellung(bestellung, kunde, locale);
	}

	/*
	@Override
	public List<Artikel> ladenhueter(int anzahl) {
		return bestellverwaltung.ladenhueter(anzahl);
	}
	*/

	@Override
	public List<Versand> findVersand(String nr) {
		return bestellverwaltung.findVersand(nr);
	}

	
	@Override
	public Versand createVersand(Versand versand,
			List<Bestellung> bestellungen) {
		return bestellverwaltung.createVersand(versand, bestellungen);
	}
	

}
