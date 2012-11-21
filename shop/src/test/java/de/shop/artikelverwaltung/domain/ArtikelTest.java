package de.shop.artikelverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.Date;

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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.AbstractDomainTest;
import de.shop.artikelverwaltung.domain.*;



@RunWith(Arquillian.class)
public class ArtikelTest extends AbstractDomainTest {
	private static final String NAME_VORHANDEN = "Hose";
	private static final String NAME_NICHT_VORHANDEN = "Nicht";

	private static final Double PREIS_MAX = 500.0;
	private static final String GROESSE = "S";
	private static final Double PREIS = 10.0;
	

	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findArtikelByNameVorhanden(){
		// Given
		final String name = NAME_VORHANDEN;
		
		// When
		final TypedQuery<Artikel> query =
				                        getEntityManager().createNamedQuery(Artikel.FIND_ARTIKEL_BY_NAME,
				                                                            Artikel.class);
		query.setParameter(Artikel.PARAM_NAME, name);
		final List<Artikel> dieartikel = query.getResultList();
		
		// Then
		assertThat(dieartikel.isEmpty(), is(false));
		for (Artikel a : dieartikel) {
			assertThat(a.getName(), is(name));
		}
	}
	
	@Test
	public void findArtikelByNameNichtVorhanden(){
		//Given
		final String name = NAME_NICHT_VORHANDEN;
		
		//When
		final TypedQuery<Artikel> query = getEntityManager().createNamedQuery(Artikel.FIND_ARTIKEL_BY_NAME, Artikel.class);
		query.setParameter(Artikel.PARAM_NAME, name);
		final List<Artikel> artikel = query.getResultList();
		
		//Then
		assertThat(artikel.isEmpty(), is(true));
	}
		
	@Test
	public void findArtikelMaxPreis(){

		//Given
		final Double preismax = PREIS_MAX;
		
		//When
		final TypedQuery<Artikel> query = getEntityManager().createNamedQuery(Artikel.FIND_ARTIKEL_MAX_PREIS, Artikel.class);
		query.setParameter(Artikel.PARAM_PREIS, preismax);
		final List<Artikel> dieartikel = query.getResultList();
		
		//Then
		assertThat(dieartikel.isEmpty(), is(false));
		for (Artikel a : dieartikel) {
			assertThat(a.getPreis(), lessThan(preismax));
		
		}
	
	}

	
	@Test
	public void CreateArtikel(){
		//Given
		Artikel artikel = new Artikel();
		artikel.setPreis(PREIS);
		artikel.setName(NAME_VORHANDEN);
		artikel.setGroesse(GROESSE);

		// Then
				
		// Den abgespeicherten Artikel ueber eine Named Query ermitteln
		final TypedQuery<Artikel> query =
				                        getEntityManager().createNamedQuery(Artikel.FIND_ARTIKEL_BY_NAME,
				                                                            Artikel.class);
		query.setParameter(Artikel.PARAM_NAME, NAME_VORHANDEN);
		final List<Artikel> dieartikel = query.getResultList();
				
		// Ueberpruefung des ausgelesenen Objekts
		artikel = (Artikel) dieartikel.get(0);
		assertThat(artikel.getId().longValue() > 0, is(true));
		assertThat(artikel.getName(), is(NAME_VORHANDEN));	
	}
	
	@Test
	public void createArtikelOhneGroesse() throws HeuristicMixedException, HeuristicRollbackException,
														SystemException {
	// Given
	final Double preis = PREIS;
	final String name = NAME_VORHANDEN;
	
	// When
	final Artikel artikel = new Artikel();
	artikel.setPreis(preis);
	artikel.setName(name);
	getEntityManager().persist(artikel);
	
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
			if (msg.contains("Ein Artikel muss eine Groesse haben")) {
				return;
			}
		}
	}
}

}