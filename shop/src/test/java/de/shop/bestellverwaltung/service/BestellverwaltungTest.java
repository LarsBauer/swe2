package de.shop.bestellverwaltung.service;


import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.dao.KundeDao.FetchType;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.util.AbstractTest;


@RunWith(Arquillian.class)
public class BestellverwaltungTest extends AbstractTest {
	private static final Long KUNDE_ID_VORHANDEN = Long.valueOf(100);
	private static final Long ARTIKEL_1_ID = Long.valueOf(300);
	private static final short ARTIKEL_1_ANZAHL = 1;
	private static final Long ARTIKEL_2_ID = Long.valueOf(301);
	private static final short ARTIKEL_2_ANZAHL = 2;
	
	@Inject
	private BestellungService bv;
	
	@Inject
	private KundeService kv;
	
	@Inject
	private ArtikelService av;
	
	/**
	 */
	@SuppressWarnings("unchecked")  // wegen anyOf()
	@Test
	public void createBestellung() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                      SystemException, NotSupportedException {
		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;
		final Long artikel1Id = ARTIKEL_1_ID;
		final short artikel1Anzahl = ARTIKEL_1_ANZAHL;
		final Long artikel2Id = ARTIKEL_2_ID;
		final short artikel2Anzahl = ARTIKEL_2_ANZAHL;

		// When
		
		// An der Web-Oberflaeche wird eine Bestellung in mehrere Benutzerintaraktionen u. Transaktionen komponiert

		Artikel artikel = av.findArtikelById(artikel1Id);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		Bestellung bestellung = new Bestellung();
		Bestellposition bpos = new Bestellposition(artikel, artikel1Anzahl);
		bestellung.addBestellposition(bpos);
		
		trans.begin();
		artikel = av.findArtikelById(artikel2Id);
		trans.commit();
		
		bpos = new Bestellposition(artikel, artikel2Anzahl);
		bestellung.addBestellposition(bpos);

		trans.begin();
		Kunde kunde = kv.findKundeById(kundeId, FetchType.MIT_BESTELLUNGEN);
		trans.commit();

		trans.begin();
		bestellung = bv.createBestellung(bestellung, kunde, LOCALE);
		trans.commit();
		
		// Then
		assertThat(bestellung.getBestellpositionen().size(), is(2));
		for (Bestellposition bp : bestellung.getBestellpositionen()) {
			assertThat(bp.getArtikel().getId(), anyOf(is(artikel1Id), is(artikel2Id)));
		}
			
		kunde = bestellung.getKunde();
		assertThat(kunde.getId(), is(kundeId));
	}
}
