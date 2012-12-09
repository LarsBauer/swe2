package de.shop.bestellverwaltung.service;

import java.util.List;
import java.util.Locale;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Versand;
import de.shop.kundenverwaltung.domain.Kunde;

public interface Bestellverwaltung {

	/**
	 */
	Bestellung findBestellungById(Long id);

	/**
	 */
	Bestellung findBestellungByIdMitLieferungen(Long id);

	/**
	 */
	Kunde findKundeById(Long id);

	/**
	 */
	List<Bestellung> findBestellungenByKundeId(Long kundeId);

	/**
	 */
	Bestellung createBestellung(Bestellung bestellung, Kunde kunde, Locale locale);

	/**
	 */
	/*
	List<Artikel> ladenhueter(int anzahl);
	*/
	/**
	 */
	List<Versand> findVersand(String nr);

	/**
	 */
	Versand createVersand(Versand versand, List<Bestellung> bestellungen);
}
