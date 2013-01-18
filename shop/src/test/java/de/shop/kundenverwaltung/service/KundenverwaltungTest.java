package de.shop.kundenverwaltung.service;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.both;
import java.util.Collection;
import java.util.Date;
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

import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Versand;
import de.shop.kundenverwaltung.dao.KundeDao.FetchType;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.util.AbstractTest;


@RunWith(Arquillian.class)
public class KundenverwaltungTest extends AbstractTest {
	private static final String KUNDE_NACHNAME_VORHANDEN = "Alpha";
	private static final Long KUNDE_ID_VORHANDEN = Long.valueOf(100);
	private static final Long KUNDE_ID_NICHT_VORHANDEN = Long.valueOf(1000);
	private static final Long KUNDE_ID_OHNE_BESTELLUNGEN = Long.valueOf(102);
	private static final String KUNDE_NACHNAME_NICHT_VORHANDEN = "Beta";
	private static final String KUNDE_EMAIL_NICHT_VORHANDEN = "nicht.vorhanden@nicht.vorhanden.de";
	private static final String KUNDE_GESCHLECHT_NICHT_VORHANDEN = "M";
	private static final String KUNDE_VORNAME_NICHT_VORHANDEN = "Hans";
	private static final String PLZ_VORHANDEN = "76133";
	private static final String PLZ_NICHT_VORHANDEN = "111";
	private static final String KUNDE_NEU_NACHNAME = "Alphaneu";
	private static final String KUNDE_NEU_VORNAME = "Peterneu";
	private static final String KUNDE_NEU_VORNAME2 = "Peternew";
	private static final String KUNDE_NEU_NACHNAME2 = "Alphanew";
	private static final String KUNDE_NEU_EMAIL = "neu.test@hska.de";
	private static final String KUNDE_NEU_EMAIL2 = "new.test@hska.de";
	private static final String GESCHLECHT_NEU = "M";
	private static final String PLZ_NEU = "76133";
	private static final String ORT_NEU = "Karlsruhe";
	private static final String STRASSE_NEU = "Moltkestra\u00DFe";
	private static final String HAUSNR_NEU = "40";
	private static final String PASSWORT_NEU = "testpasswort";
	private static final String PASSWORT_FALSCH_NEU = "falsch";
	
	@Inject
	private Kundenverwaltung kv;
	
	/**
	 */
	@SuppressWarnings("unchecked") // wegen allOf
	@Test
	public void findKundenMitNachnameVorhanden() {
		// Given
		final String nachname = KUNDE_NACHNAME_VORHANDEN;

		// When
		final Collection<Kunde> kunden = kv.findKundenByNachname(nachname, FetchType.NUR_KUNDE);
		
		// Then
		assertThat(kunden, is(notNullValue()));
		assertThat(kunden.isEmpty(), is(false));

		for (Kunde k : kunden) {
			assertThat(k.getName(), is(nachname));
			
			// Nur zur Veranschaulichung von both().and()
			// Wenn Gleichheit mit einem anderen Namen, dann ja auch != null ...
			assertThat(k.getName(), both(is(notNullValue())).and(is(nachname)));
			
			// Veranschaulichung von allOf: mehrere Argumente moeglich,
			// d.h. nicht nur 2 wie bei both().and()
			assertThat(k.getName(), allOf(is(notNullValue()), is(nachname)));
		}
	}

	/**
	 */
	@Test
	public void findKundenMitNachnameVorhandenFetch() {
		// Given
		final String nachname = KUNDE_NACHNAME_VORHANDEN;
		
		// When
		final Collection<Kunde> kundenMitLieferungen =
				                        kv.findKundenByNachname(nachname,
				                        		                FetchType.MIT_BESTELLUNGEN_UND_LIEFERUNGEN);
		
		// Then
		for (Kunde k : kundenMitLieferungen) {
			assertThat(k.getName(), is(nachname));
			
			final Collection<Bestellung> bestellungen = k.getBestellungen();
			if (bestellungen == null || bestellungen.isEmpty())
				continue;
			
			for (Bestellung b : bestellungen) {
				final Collection<Versand> versand = b.getVersand();
				if (versand == null || versand.isEmpty())
					continue;
				
				assertThat(versand.isEmpty(), is(false));
			}
		}
	}

	/**
	 */
	@Test
	public void findKundenMitNachnameNichtVorhanden() {
		// Given
		final String nachname = KUNDE_NACHNAME_NICHT_VORHANDEN;

		// When
		final List<Kunde> kunden = kv.findKundenByNachname(nachname, FetchType.NUR_KUNDE);
		
		// Then
		assertThat(kunden.isEmpty(), is(true));
	}
	
	/**
	 */
	@Test
	public void findKundenMitIdNichtVorhanden() {
		// Given
		final Long id = KUNDE_ID_NICHT_VORHANDEN;

		// When
		final Kunde kunde = kv.findKundeById(id, FetchType.NUR_KUNDE);
		
		// Then
		assertThat(kunde, is(nullValue()));
	}
	
	/**
	 */
	@Test
	public void findKundenMitPLZVorhanden() {
		// Given
		final String plz = PLZ_VORHANDEN;

		// When
		final Collection<Kunde> kunden = kv.findKundenByPLZ(plz);

		// Then
		assertThat(kunden, is(notNullValue()));
		assertThat(kunden.isEmpty(), is(false));
		
		for (Kunde k : kunden) {
			assertThat(k.getAdresse(), is(notNullValue()));
			assertThat(k.getAdresse().getPlz(), is(plz));
		}
	}
	
	/**
	 */
	@Test
	public void findKundenMitPLZNichtVorhanden() {
		// Given
		final String plz = PLZ_NICHT_VORHANDEN;
		
		// When
		final List<Kunde> kunden = kv.findKundenByPLZ(plz);
		
		// Then
		assertThat(kunden.isEmpty(), is(true));	}

	/**
	 */
	@Test
	public void findKundenByNachnameCriteria() {
		// Given
		final String nachname = KUNDE_NACHNAME_VORHANDEN;
		
		// When
		final List<Kunde> kunden = kv.findKundenByNachnameCriteria(nachname);
		
		// Then
		for (Kunde k : kunden) {
			assertThat(k.getName(), is(nachname));
		}
	}
	
	/**
	 */
	@Test
	public void findKundenMitMinBestMenge() {
		// Given
		final short minMenge = 2;
		
		// When
		final Collection<Kunde> kunden = kv.findKundenMitMinBestMenge(minMenge);
		
		// Then
		for (Kunde k : kunden) {
			final Kunde kundeMitBestellungen = kv.findKundeById(k.getId(), FetchType.MIT_BESTELLUNGEN);
			int bestellmenge = 0;   // short-Werte werden aufsummiert
			for (Bestellung b : kundeMitBestellungen.getBestellungen()) {
				if (b.getBestellpositionen() == null) {
					fail("Bestellung " + b + " ohne Bestellpositionen");
				}
				for (Bestellposition bp : b.getBestellpositionen()) {
					bestellmenge += bp.getAnzahl();
				}
			}
			
			assertTrue(bestellmenge >= minMenge);
		}
	}
	
	/**
	 */
	@Test
	public void createKunde() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                       SystemException, NotSupportedException {
		// Given
		final String nachname = KUNDE_NEU_NACHNAME;
		final String vorname = KUNDE_NEU_VORNAME;
		final String email = KUNDE_NEU_EMAIL;
		final String geschlecht = GESCHLECHT_NEU;
		final String plz = PLZ_NEU;
		final String ort = ORT_NEU;
		final String strasse = STRASSE_NEU;
		final String hausnr = HAUSNR_NEU;
		final String passwort = PASSWORT_NEU;
		final String passwortWdh = passwort;

		// When
		final Collection<Kunde> kundenVorher = kv.findAllKunden(FetchType.NUR_KUNDE, null);
		final UserTransaction trans = getUserTransaction();
		trans.commit();

		Kunde neuerKunde = new Kunde();
		neuerKunde.setName(nachname);
		neuerKunde.setVorname(vorname);
		neuerKunde.setEmail(email);
		neuerKunde.setGeschlecht(geschlecht);
		neuerKunde.setNewsletter(true);
		
		final Adresse adresse = new Adresse();
		adresse.setPlz(plz);
		adresse.setOrt(ort);
		adresse.setStrasse(strasse);
		adresse.setHausnummer(hausnr);
		neuerKunde.setAdresse(adresse);
		adresse.setKunde(neuerKunde);
		neuerKunde.setPasswort(passwort);
		neuerKunde.setPasswortWdh(passwortWdh);

		final Date datumVorher = new Date();
		
		trans.begin();
		Kunde ergKunde = kv.createKunde(neuerKunde, LOCALE);
		trans.commit();
		
		// Then
		assertThat(datumVorher.getTime() <= ergKunde.getErzeugt().getTime(), is(true));

		trans.begin();
		final Collection<Kunde> kundenNachher = kv.findAllKunden(FetchType.NUR_KUNDE, null);
		trans.commit();
		
		assertThat(kundenVorher.size() + 1, is(kundenNachher.size()));
		for (Kunde k : kundenVorher) {
			assertTrue(k.getId() < ergKunde.getId());
			assertTrue(k.getErzeugt().getTime() < ergKunde.getErzeugt().getTime());
		}
		
		trans.begin();
		ergKunde = kv.findKundeById(neuerKunde.getId(), FetchType.NUR_KUNDE);
		trans.commit();
		
		assertThat(ergKunde.getName(), is(nachname));
		assertThat(ergKunde.getEmail(), is(email));
		assertThat(ergKunde.getGeschlecht(), is(GESCHLECHT_NEU));
		assertThat(ergKunde.getVorname(), is(KUNDE_NEU_VORNAME));
	}
	
	/**

	 */
	@Test
	public void createDuplikatKunde() throws RollbackException, HeuristicMixedException,
	                                               HeuristicRollbackException, SystemException,
	                                               NotSupportedException {
		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;
		
		final Kunde k = kv.findKundeById(kundeId, FetchType.NUR_KUNDE);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		assertThat(k, is(notNullValue()));
		assertThat(k, is(instanceOf(Kunde.class)));
		
		// When
		final Kunde neuerKunde = new Kunde();
		neuerKunde.setName(k.getName());
		neuerKunde.setVorname(k.getVorname());
		neuerKunde.setEmail(k.getEmail());
		neuerKunde.setAdresse(k.getAdresse());
		neuerKunde.setGeschlecht(k.getGeschlecht());
		neuerKunde.setVorname(k.getVorname());
		neuerKunde.setNewsletter(k.isNewsletter());
		
		// Then
		thrown.expect(EmailExistsException.class);
		trans.begin();
		kv.createKunde(neuerKunde, LOCALE);
	}
	
	/**
	 */
	@Test
	public void createKundeOhneAdresse() {
		// Given
		final String nachname = KUNDE_NACHNAME_NICHT_VORHANDEN;
		final String email = KUNDE_EMAIL_NICHT_VORHANDEN;
		final String geschlecht = KUNDE_GESCHLECHT_NICHT_VORHANDEN;
		final String vorname = KUNDE_VORNAME_NICHT_VORHANDEN;
		
		
		// When
		final Kunde neuerKunde = new Kunde();
		neuerKunde.setName(nachname);
		neuerKunde.setVorname(vorname);
		neuerKunde.setEmail(email);
		neuerKunde.setAdresse(null);
		neuerKunde.setGeschlecht(geschlecht);
		neuerKunde.setNewsletter(true);
		
		// Then
		thrown.expect(KundeValidationException.class);
		thrown.expectMessage("Ungueltiger Kunde:");
		kv.createKunde(neuerKunde, LOCALE);
	}
	
	/**
	 */
	@Test
	public void createKundeFalschesPassword() {
		// Given
		final String nachname = KUNDE_NEU_NACHNAME2;
		final String vorname = KUNDE_NEU_VORNAME2;
		final String email = KUNDE_NEU_EMAIL2;
		final String plz = PLZ_NEU;
		final String ort = ORT_NEU;
		final String strasse = STRASSE_NEU;
		final String hausnr = HAUSNR_NEU;
		final String passwort = PASSWORT_NEU;
		final String passwortWdh = PASSWORT_FALSCH_NEU;

		// When
		Kunde neuerKunde = new Kunde();
		neuerKunde.setName(nachname);
		neuerKunde.setEmail(email);
		neuerKunde.setVorname(vorname);
		final Adresse adresse = new Adresse();
		adresse.setPlz(plz);
		adresse.setOrt(ort);
		adresse.setStrasse(strasse);
		adresse.setHausnummer(hausnr);
		neuerKunde.setAdresse(adresse);
		neuerKunde.setPasswort(passwort);
		neuerKunde.setPasswortWdh(passwortWdh);

		// Then
		thrown.expect(KundeValidationException.class);
		thrown.expectMessage("Ungueltiger Kunde:");
		kv.createKunde(neuerKunde, LOCALE);
	}
	
	/**
	 */
	@Test
	public void deleteKunde() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                 SystemException, NotSupportedException {
		// Given
		final Long kundeId = KUNDE_ID_OHNE_BESTELLUNGEN;

		final Collection<Kunde> kundenVorher = kv.findAllKunden(FetchType.NUR_KUNDE, null);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
	
		// When
		trans.begin();
		final Kunde kunde = kv.findKundeById(kundeId, FetchType.MIT_BESTELLUNGEN);
		trans.commit();
		trans.begin();
		kv.deleteKunde(kunde);
		trans.commit();
	
		// Then
		trans.begin();
		final Collection<Kunde> kundenNachher = kv.findAllKunden(FetchType.NUR_KUNDE, null);
		trans.commit();
		assertThat(kundenVorher.size() - 1, is(kundenNachher.size()));
	}
	
	/**
	 */
	@Test
	public void neuerNameFuerKunde() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                        SystemException, NotSupportedException {
		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;

		// When
		Kunde kunde = kv.findKundeById(kundeId, FetchType.NUR_KUNDE);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		final String alterNachname = kunde.getName();
		final String neuerNachname = alterNachname + alterNachname.charAt(alterNachname.length() - 1);
		kunde.setName(neuerNachname);
	
		trans.begin();
		kunde = kv.updateKunde(kunde, LOCALE);
		trans.commit();
		
		// Then
		assertThat(kunde.getName(), is(neuerNachname));
		trans.begin();
		kunde = kv.findKundeById(kundeId, FetchType.NUR_KUNDE);
		trans.commit();
		assertThat(kunde.getName(), is(neuerNachname));
	}
}
