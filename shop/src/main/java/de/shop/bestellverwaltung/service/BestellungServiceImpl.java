package de.shop.bestellverwaltung.service;

import static de.shop.util.Constants.KEINE_ID;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.jboss.logging.Logger;

import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Lieferung;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.util.Log;
import de.shop.util.ValidatorProvider;

@Log
public class BestellungServiceImpl implements Serializable, BestellungService {
	private static final long serialVersionUID = -9145947650157430928L;
	
	@PersistenceContext
	private transient EntityManager em;
	
	@Inject
	private Logger logger;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private ValidatorProvider validatorProvider;
	
	@Inject
	@NeueBestellung
	private transient Event<Bestellung> event;
	
	@PostConstruct
	private void postConstruct() {
		logger.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		logger.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	/**
	 */
	@Override
	public Bestellung findBestellungById(Long id, FetchType fetch, Locale locale) {
		Bestellung bestellung = null;
		if (fetch == null || FetchType.NUR_BESTELLUNG.equals(fetch)) {
			bestellung = em.find(Bestellung.class, id);
		}
		else if (FetchType.MIT_LIEFERUNGEN.equals(fetch)) {
			try {
			bestellung = em.createNamedQuery(Bestellung.FIND_BESTELLUNG_BY_ID_FETCH_LIEFERUNGEN, Bestellung.class)
					       .setParameter(Bestellung.PARAM_ID, id)
					       .getSingleResult();
			}
			catch (NoResultException e) {
				return null;
			}
		}
		return bestellung;
	}
	
	@Override
	public List<Bestellung> findBestellungenByKunde(Kunde kunde) {
		if (kunde == null) {
			return Collections.emptyList();
		}
		
		final List<Bestellung> bestellungen = em.createNamedQuery(Bestellung.FIND_BESTELLUNGEN_BY_KUNDEID,
				                                                  Bestellung.class)
			                                    .setParameter(Bestellung.PARAM_KUNDEID, kunde.getId())
			                                    .getResultList();
		return bestellungen;
	}
	
	public Kunde findKundeById(Long id, Locale locale) {
		try {
			final Kunde kunde = em.createNamedQuery(Bestellung.FIND_KUNDE_BY_ID, Kunde.class)
					                      .setParameter(Bestellung.PARAM_ID, id)
					                      .getSingleResult();
			return kunde;
		}
		catch (NoResultException e) {
			return null;
		}
	}
	
	@Override
	public List<Bestellung> findBestellungenMitLieferungenByKunde(Kunde kunde) {
		final List<Bestellung> bestellungen =
				               em.createNamedQuery(Bestellung.FIND_BESTELLUNGEN_BY_KUNDEID_FETCH_LIEFERUNGEN,
                                                   Bestellung.class)
                                 .setParameter(Bestellung.PARAM_KUNDEID, kunde.getId())
                                 .getResultList();
		return bestellungen;
	}



	
	@Override
	public Bestellung createBestellung(Bestellung bestellung, Long kundeId, Locale locale) {
		if (bestellung == null) {
			return null;
		}

		// Den persistenten Kunden mit der transienten Bestellung verknuepfen
		final Kunde kunde = ks.findKundeById(kundeId, KundeService.FetchType.MIT_BESTELLUNGEN, locale);
		return createBestellung(bestellung, kunde, locale);
	}


	/**
	 */
	@Override
	public Bestellung createBestellung(Bestellung bestellung, Kunde kunde, Locale locale) {
		if (bestellung == null) {
			return null;
		}

		// Den persistenten Kunden mit der transienten Bestellung verknuepfen
		if (!em.contains(kunde)) {
			kunde = ks.findKundeById(kunde.getId(), KundeService.FetchType.MIT_BESTELLUNGEN, locale);
		}
		bestellung.setKunde(kunde);
		kunde.addBestellung(bestellung);
		
		// Vor dem Abspeichern IDs zuruecksetzen:
		// IDs koennten einen Wert != null haben, wenn sie durch einen Web Service uebertragen wurden
		bestellung.setId(KEINE_ID);
		for (Bestellposition bp : bestellung.getBestellpositionen()) {
			bp.setId(KEINE_ID);
		}
		
		validateBestellung(bestellung, locale, Default.class);
		em.persist(bestellung);
		event.fire(bestellung);
		
		return bestellung;
	}
	
	private void validateBestellung(Bestellung bestellung, Locale locale, Class<?> groups) {
		final Validator validator = validatorProvider.getValidator(locale);
		final Set<ConstraintViolation<Bestellung>> violations =
			                                       validator.validate(bestellung, groups);
		if (violations != null && !violations.isEmpty()) {
			throw new InvalidBestellungException(bestellung, violations);
		}
	}
	
	/**
	 */

	@Override
	public List<Lieferung> findLieferungen(String nr) {
		final List<Lieferung> lieferungen =
				              em.createNamedQuery(Lieferung.FIND_LIEFERUNGEN_BY_LIEFERNR_FETCH_BESTELLUNGEN,
				            		              Lieferung.class)
                                .setParameter(Lieferung.PARAM_LIEFER_NR, nr)
                                .getResultList();
		return lieferungen;
	}

	/**
	 */
	
	@Override
	public Lieferung createLieferung(Lieferung lieferung) {
		if (lieferung == null) {
			return null;
		}
		
		lieferung.setId(KEINE_ID);
		em.persist(lieferung);
		return lieferung;
	}

}
