package de.shop.bestellverwaltung.service;


import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Versand;
import de.shop.util.AbstractTest;


@RunWith(Arquillian.class)
public class LieferverwaltungTest extends AbstractTest {
	private static final Long ID_VORHANDEN = Long.valueOf(600);
	private static final Long ID_NICHT_VORHANDEN = Long.valueOf(650);
	
	private static final Long BESTELLUNG_ID_VORHANDEN = Long.valueOf(400);
	private static final String NEUE_VERSANDART = "Express";
	private static final Double NEUE_VERSANDKOSTEN = 10.50;
	private static final Long BESTELLUNG_ID2A_VORHANDEN = Long.valueOf(401);
	private static final Long BESTELLUNG_ID2B_VORHANDEN = Long.valueOf(402);
	private static final String NEUE_VERSANDART_2 = "Sperrgut";
	private static final Double NEUE_VERSANDKOSTEN_2 = 32.90;

	@Inject
	private BestellungService bv;
	
	/**
	 */
	@Test
	public void findVersandVorhanden() {
		// Given
		final Long id = ID_VORHANDEN;
		
		// When
		final Collection<Versand> versand = bv.findVersand(id);
		
		// Then
		assertThat(versand.isEmpty(), is(false));
		final String idPraefix = id.toString().substring(0, id.toString().length() - 2);  // '%' ausblenden
		for (Versand v : versand) {
			assertThat(v.getId().toString().startsWith(idPraefix), is(true));
	
			final Collection<Bestellung> bestellungen = v.getBestellungen();
			assertThat(bestellungen.isEmpty(), is(false));
	
			for (Bestellung b : bestellungen) {
				assertThat(b.getKunde(), is(notNullValue()));
			}
		}
	}

	/**
	 */
	@Test
	public void findVersandNichtVorhanden() {
		// Given
		final Long id = ID_NICHT_VORHANDEN;

		// When
		final List<Versand> versand = bv.findVersand(id);
		
		// Then
		assertThat(versand.isEmpty(), is(true));
	}
	

	/**
	 */
	@Test
	public void createVersand() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                     SystemException, NotSupportedException {
		// Given
		final Long bestellungId = BESTELLUNG_ID_VORHANDEN;
		final Double neueVersandkosten = NEUE_VERSANDKOSTEN;
		final String neueVersandart = NEUE_VERSANDART;

		
		// When
		Versand neuerVersand = new Versand();
		neuerVersand.setVersandart(neueVersandart);
		neuerVersand.setVersandkosten(neueVersandkosten);
		
		final List<Bestellung> bestellungen = new ArrayList<Bestellung>();
		final Bestellung bestellung = bv.findBestellungByIdMitLieferungen(bestellungId);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		assertThat(bestellung.getId(), is(bestellungId));
		bestellungen.add(bestellung);
		
		trans.begin();
		neuerVersand = bv.createVersand(neuerVersand, bestellungen);
		trans.commit();
		
		// Then
		assertThat(neuerVersand.getId(), is(notNullValue()));
		assertThat(neuerVersand.getVersandart(), is(neueVersandart));
		assertThat(neuerVersand.getVersandkosten(), is(neueVersandkosten));
		assertThat(neuerVersand.getBestellungen().size(), is(1));
		assertThat(neuerVersand.getBestellungen().iterator().next().getId(), is(bestellungId));
	}
	
	/**
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void createVersand2() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                      SystemException, NotSupportedException {
		// Given
		final Long bestellungId2a = BESTELLUNG_ID2A_VORHANDEN;
		final Long bestellungId2b = BESTELLUNG_ID2B_VORHANDEN;
		final Double neueVersandkosten2 = NEUE_VERSANDKOSTEN_2;
		final String neueVersandart2 = NEUE_VERSANDART_2;
				
		
		// When
		Versand neuerVersand = new Versand();
		neuerVersand.setVersandart(neueVersandart2);
		neuerVersand.setVersandkosten(neueVersandkosten2);
		
		final List<Bestellung> bestellungen = new ArrayList<Bestellung>();
		Bestellung bestellung = bv.findBestellungByIdMitLieferungen(bestellungId2a);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		assertThat(bestellung.getId(), is(bestellungId2a));
		bestellungen.add(bestellung);
		
		trans.begin();
		bestellung = bv.findBestellungByIdMitLieferungen(bestellungId2b);
		trans.commit();
		
		assertThat(bestellung.getId(), is(bestellungId2b));
		bestellungen.add(bestellung);
		
		trans.begin();
		neuerVersand = bv.createVersand(neuerVersand, bestellungen);
		trans.commit();
		
		// Then
		assertThat(neuerVersand.getId(), is(notNullValue()));
		assertThat(neuerVersand.getVersandart(), is(neueVersandart2));
		assertThat(neuerVersand.getVersandkosten(), is(neueVersandkosten2));
		
		assertThat(neuerVersand.getBestellungen().size(), is(2));
		for (Bestellung b : neuerVersand.getBestellungen()) {
			assertThat(b.getId(), anyOf(is(bestellungId2a), is(bestellungId2b)));
		}
	}
}
