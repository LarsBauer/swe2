package de.shop.bestellverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.AbstractDomainTest;


@RunWith(Arquillian.class)
public class BestellungTest extends AbstractDomainTest {
	
	private static final Long B_ID_VORHANDEN = Long.valueOf(400);
	private static final Long V_ID_VORHANDEN = Long.valueOf(600);
	private static final Long BP_ID_VORHANDEN = Long.valueOf(500);
	private static final Long K_ID_VORHANDEN = Long.valueOf(103);
	
	private static final Long B_ID_NICHT_VORHANDEN = Long.valueOf(1);
	private static final Long K_ID_NICHT_VORHANDEN = Long.valueOf(50);
	
	private static final String STATUS_NEU = "in Bearbeitung";
	
	private static final String NACHNAME_NEU = "Mustermann";
	private static final String VORNAME_NEU = "Max";
	private static final String EMAIL_NEU = "max@mustermann.de";
	private static final String GESCHLECHT_NEU = "M";
	private static final String PASSWORT_NEU = "12345";
	private static final String PLZ_NEU = "11111";
	private static final String ORT_NEU = "Testort";
	private static final String STRASSE_NEU = "Testweg";
	private static final String HAUSNR_NEU = "1";

	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	
	@Test
	public void findBestellungByKundeVorhanden() {
		// Given
		final Long id = K_ID_VORHANDEN;

		
		// When
		final Kunde kunde = getEntityManager().find(Kunde.class, id);

		
		// Then
		final TypedQuery<Bestellung> query = 
				getEntityManager().createNamedQuery(Bestellung.FIND_BESTELLUNGEN_BY_KUNDE, Bestellung.class);
		
		query.setParameter(Bestellung.PARAM_KUNDEID, id);
		final List<Bestellung> bestellungen = query.getResultList();
		
		assertThat(bestellungen.size(), is(1));
		Bestellung bestellung = (Bestellung) bestellungen.get(0);
		assertThat(bestellung.getKunde(), is(kunde));
	}
	

	@Test
	public void findeKundeByIdVorhanden() {
		// Given
		final Long id = B_ID_VORHANDEN;
		
		// When
		final Bestellung bestellung = getEntityManager().find(Bestellung.class, id);
		
		//Then
		assertThat(bestellung.getKunde().getBestellungen().contains(bestellung), is(true));
		
	}
	

	@Test
	public void findKundeByIdNichtVorhanden() {
		// Given
		final Long id = B_ID_NICHT_VORHANDEN;
		
		// When
		final Bestellung bestellung = getEntityManager().find(Bestellung.class, id);
				
		//Then
		assertThat(bestellung == null, is(true));
	}
	
	

	@Test
	public void findBestellungByKundeNichtVorhanden() {
		// Given
		final Long id = K_ID_NICHT_VORHANDEN;

		
		// When
		final Kunde kunde = getEntityManager().find(Kunde.class, id);

		
		// Then
		final TypedQuery<Bestellung> query = 
				getEntityManager().createNamedQuery(Bestellung.FIND_BESTELLUNGEN_BY_KUNDE, Bestellung.class);
		
		query.setParameter(Bestellung.PARAM_KUNDEID, kunde.getId());
		final List<Bestellung> bestellungen = query.getResultList();
		
		assertThat(bestellungen.size(), is(0));
	}
	
	
	

	@Test
	public void createBestellung() {
		// Given
		final Long vid = V_ID_VORHANDEN;
		final Long bpid = BP_ID_VORHANDEN;
		
		Kunde kunde = new Kunde();
		kunde.setNachname(NACHNAME_NEU);
		kunde.setVorname(VORNAME_NEU);
		kunde.setEmail(EMAIL_NEU);
		kunde.setPasswort(PASSWORT_NEU);
		kunde.setNewsletter(true);
		kunde.setGeschlecht(GESCHLECHT_NEU);
		
		final Adresse adresse = new Adresse();
		adresse.setPlz(PLZ_NEU);
		adresse.setOrt(ORT_NEU);
		adresse.setStrasse(STRASSE_NEU);
		adresse.setHausnummer(HAUSNR_NEU);
		adresse.setKunde(kunde);
		kunde.setAdresse(adresse);
		
		try {
			getEntityManager().persist(kunde);         // abspeichern einschl. Adresse
		}
		catch (ConstraintViolationException e) {
			// Es gibt Verletzungen bzgl. Bean Validation: auf der Console ausgeben
			final Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
			for (ConstraintViolation<?> v : violations) {
				System.err.println("!!! FEHLERMELDUNG>>> " + v.getMessage());
				System.err.println("!!! ATTRIBUT>>> " + v.getPropertyPath());
				System.err.println("!!! ATTRIBUTWERT>>> " + v.getInvalidValue());
			}
			
			throw new RuntimeException(e);
		}
		
		
		final List<Versand> versand = new ArrayList<Versand>();
		versand.add(getEntityManager().find(Versand.class, vid));
		
		final List<Bestellposition> bestellpositionen = new ArrayList<Bestellposition>();
		bestellpositionen.add(getEntityManager().find(Bestellposition.class, bpid));
		
		Bestellung bestellung = new Bestellung();
		bestellung.setKunde(kunde);
		bestellung.setBestellpositionen(bestellpositionen);
		bestellung.setVersand(versand);
		bestellung.setStatus(STATUS_NEU);
		
		// When
		try {
			getEntityManager().persist(bestellung);         // abspeichern
		}
		catch (ConstraintViolationException e) {
			// Es gibt Verletzungen bzgl. Bean Validation: auf der Console ausgeben
			final Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
			for (ConstraintViolation<?> v : violations) {
				System.err.println("!!! FEHLERMELDUNG>>> " + v.getMessage());
				System.err.println("!!! ATTRIBUT>>> " + v.getPropertyPath());
				System.err.println("!!! ATTRIBUTWERT>>> " + v.getInvalidValue());
			}
			
			throw new RuntimeException(e);
		}
		
		// Then

		
	}
	
	@Ignore
	@Test
	public void creatBestellungOhneKunde() 
			throws HeuristicMixedException, HeuristicRollbackException, SystemException { 
		// Given
		final Long vid = V_ID_VORHANDEN;
		final Long bpid = BP_ID_VORHANDEN;
		
		final List<Versand> versand = new ArrayList<Versand>();
		versand.add(getEntityManager().find(Versand.class, vid));
		
		final List<Bestellposition> bestellpositionen = new ArrayList<Bestellposition>();
		bestellpositionen.add(getEntityManager().find(Bestellposition.class, bpid));
		
		// When
		Bestellung bestellung = new Bestellung();
		bestellung.setBestellpositionen(bestellpositionen);
		bestellung.setVersand(versand);
		bestellung.setStatus(STATUS_NEU);
		getEntityManager().persist(bestellung);
		
		// Then
		try {
			getUserTransaction().commit();
		}
		catch (RollbackException e) {
			final PersistenceException cause = (PersistenceException) e.getCause();
			final ConstraintViolationException cause2 = (ConstraintViolationException) cause.getCause();
			final Set<ConstraintViolation<?>> violations = cause2.getConstraintViolations();
			for (ConstraintViolation<?> v : violations) {
				final String msg = v.getMessage();
				if (msg.contains("Eine Bestellung muss einen Kunden haben")) {
					return;
				}
			}
		}
	}
}
