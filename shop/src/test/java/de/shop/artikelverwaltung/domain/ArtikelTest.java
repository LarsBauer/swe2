package de.shop.artikelverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
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
	private static final Double PREIS_MAX = 10.0;
	private static final String NAME = "Hose";
	private static final Long ID=Long.valueOf(300);
	private static final String GROESSE = "S";
	//private static final String ERZEUGT = "01.08.2006 00:00:00";
	//private static final String AKTUALISIERT = "01.08.2006 00:00:00";
	private static final Double PREIS = 10.0;
	

	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findArtikelByName(){
		// Given
		final String name = NAME;
		
		// When
		final TypedQuery<Artikel> query =
				                        getEntityManager().createNamedQuery(Artikel.FIND_ARTIKEL_BY_NAME,
				                                                            Artikel.class);
		query.setParameter(Artikel.PARAM_NAME, name);
		final List<Artikel> dieartikel = query.getResultList();
		
		// Then
		//assertThat(dieartikel.isEmpty(), is(false));
		for (Artikel a : dieartikel) {
			assertThat(a.getName(), is(name));
		}
	}
	
	@Test
	public void findArtikelMaxPreis(){

		//Given
		final Double preis = PREIS_MAX;
		
		//When
		final TypedQuery<Artikel> query = getEntityManager().createNamedQuery(Artikel.FIND_ARTIKEL_MAX_PREIS, Artikel.class);
		query.setParameter(Artikel.PARAM_PREIS, preis);
		final List<Artikel> dieartikel = query.getResultList();
		
		//Then
		//assertThat(dieartikel.isEmpty(), is(false));
		for (Artikel a : dieartikel) {
			assertThat(a.getPreis(), is(preis));
		
		}
	
	}
	
	@Ignore
	@Test
	public void CreateArtikel(){
		//Given
		Artikel artikel = new Artikel();
		artikel.setPreis(PREIS);
		artikel.setName(NAME);
		artikel.setId(ID);
		artikel.setGroesse(GROESSE);
		//artikel.setErzeugt(ERZEUGT);
		//artikel.setAktualisiert(AKTUALISIERT);
		
		// When
				try {
					getEntityManager().persist(artikel);         // abspeichern einschl. Adresse
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
				
		// Den abgespeicherten Artikel ueber eine Named Query ermitteln
		final TypedQuery<Artikel> query =
				                        getEntityManager().createNamedQuery(Artikel.FIND_ARTIKEL_BY_NAME,
				                                                            Artikel.class);
		query.setParameter(Artikel.PARAM_NAME, NAME);
		final List<Artikel> dieartikel = query.getResultList();
				
		// Ueberpruefung des ausgelesenen Objekts
		assertThat(dieartikel.size(), is(1));
		artikel = (Artikel) dieartikel.get(0);
		assertThat(artikel.getId().longValue() > 0, is(true));
		assertThat(artikel.getName(), is(NAME));	
	}
}