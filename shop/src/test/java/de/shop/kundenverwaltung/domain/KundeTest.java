package de.shop.kundenverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.util.AbstractDomainTest;



@RunWith(Arquillian.class)
public class KundeTest extends AbstractDomainTest {
	private static final String NACHNAME_VORHANDEN = "Alpha";
	private static final String NACHNAME_NICHT_VORHANDEN = "Nicht";
	private static final Long ID_VORHANDEN = Long.valueOf(100);
	private static final String EMAIL_VORHANDEN = "k2@hska.de";
	private static final String EMAIL_NICHT_VORHANDEN = "Nicht";
	
	private static final String NACHNAME_NEU = "Test";
	private static final String VORNAME_NEU = "Theo";
	private static final String EMAIL_NEU = "theo@test.de";
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
	public void findKundeByIdVorhanden() {
		// Given
		final Long id = ID_VORHANDEN;
		
		// When
		final Kunde kunde = getEntityManager().find(Kunde.class, id);
		
		// Then
		assertThat(kunde.getId(), is(id));
	}
	
	@Test
	public void findKundeByEmailVorhanden() {
		// Given
		final String email = EMAIL_VORHANDEN;
		
		// When
		final TypedQuery<Kunde> query = getEntityManager().createNamedQuery(Kunde.FIND_KUNDE_BY_EMAIL,
				                                                                    Kunde.class);
		query.setParameter(Kunde.PARAM_KUNDE_EMAIL, email);
		Kunde kunde = query.getSingleResult();
		
		// Then
		assertThat(kunde.getEmail(), is(email));
	}
	
	@Test
	public void findKundeByEmailNichtVorhanden() {
		// Given
		final String email = EMAIL_NICHT_VORHANDEN;
		
		// When
		final TypedQuery<Kunde> query = getEntityManager().createNamedQuery(Kunde.FIND_KUNDE_BY_EMAIL,
				                                                                    Kunde.class);
		query.setParameter(Kunde.PARAM_KUNDE_EMAIL, email);
		
		// Then
		thrown.expect(NoResultException.class);
		query.getSingleResult();
	}
	
	@Test
	public void findKundenByNachnameVorhanden() {
		// Given
		final String nachname = NACHNAME_VORHANDEN;
		
		// When
		final TypedQuery<Kunde> query =
				                        getEntityManager().createNamedQuery(Kunde.FIND_KUNDEN_BY_NACHNAME,
				                                                            Kunde.class);
		query.setParameter(Kunde.PARAM_KUNDE_NACHNAME, nachname);
		final List<Kunde> kunden = query.getResultList();
		
		// Then
		assertThat(kunden.isEmpty(), is(false));
		for (Kunde k : kunden) {
			assertThat(k.getNachname(), is(nachname));
		}
	}
	
	@Test
	public void findKundenByNachnameNichtVorhanden() {
		// Given
		final String nachname = NACHNAME_NICHT_VORHANDEN;
		
		// When
		final TypedQuery<Kunde> query =
				                        getEntityManager().createNamedQuery(Kunde.FIND_KUNDEN_BY_NACHNAME,
				                                                            Kunde.class);
		query.setParameter(Kunde.PARAM_KUNDE_NACHNAME, nachname);
		final List<Kunde> kunden = query.getResultList();
		
		// Then
		assertThat(kunden.isEmpty(), is(true));
	}
	

	@Test
	public void createKunde() {
		// Given
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
		
		
		
		// When
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
		
		// Then
		
		// Den abgespeicherten Kunden ueber eine Named Query ermitteln
		final TypedQuery<Kunde> query =
				                        getEntityManager().createNamedQuery(Kunde.FIND_KUNDEN_BY_NACHNAME,
				                                                            Kunde.class);
		query.setParameter(Kunde.PARAM_KUNDE_NACHNAME, NACHNAME_NEU);
		final List<Kunde> kunden = query.getResultList();
		
		// Ueberpruefung des ausgelesenen Objekts
		kunde = (Kunde) kunden.get(0);
		assertThat(kunde.getId().longValue() > 0, is(true));
		assertThat(kunde.getNachname(), is(NACHNAME_NEU));
	}
	

	
	@Test
	public void createKundeOhneAdresse() throws HeuristicMixedException, HeuristicRollbackException,
	                                                  SystemException {
		// Given
		final String vorname = VORNAME_NEU;
		final String nachname = NACHNAME_NEU;
		final String email = EMAIL_NEU;
		final String passwort = PASSWORT_NEU;
		final String geschlecht = GESCHLECHT_NEU;
		
		// When
		final Kunde kunde = new Kunde();
		kunde.setNachname(nachname);
		kunde.setEmail(email);
		kunde.setVorname(vorname);
		kunde.setGeschlecht(geschlecht);
		kunde.setNewsletter(true);
		kunde.setPasswort(passwort);
		getEntityManager().persist(kunde);
		
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
				if (msg.contains("Ein Kunde muss eine Adresse haben")) {
					return;
				}
			}
		}

	}
}
