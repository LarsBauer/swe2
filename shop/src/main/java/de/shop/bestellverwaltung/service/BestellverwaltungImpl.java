package de.shop.bestellverwaltung.service;

import static de.shop.util.AbstractDao.QueryParameter.with;
import static de.shop.util.Constants.KEINE_ID;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import de.shop.artikelverwaltung.dao.ArtikelDao;
import de.shop.bestellverwaltung.dao.BestellungDao;
import de.shop.bestellverwaltung.dao.VersandDao;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Versand;
import de.shop.kundenverwaltung.dao.KundeDao;
import de.shop.kundenverwaltung.dao.KundeDao.FetchType;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.Kundenverwaltung;
import de.shop.util.Log;
import de.shop.util.ValidationService;

@Log
public class BestellverwaltungImpl implements Serializable, Bestellverwaltung {
	private static final long serialVersionUID = -9145947650157430928L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	private Kundenverwaltung kv;
	
	@Inject
	private BestellungDao dao;
	
	@Inject
	private KundeDao kundeDao;
	
	@Inject
	private ArtikelDao artikelDao;
	
	@Inject
	private VersandDao versandDao;
	
	@Inject
	private ValidationService validationService;
	
	@Inject
	@NeueBestellung
	private transient Event<Bestellung> event;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wird geloescht", this);
	}
	
	/**
	 */
	@Override
	public Bestellung findBestellungById(Long id) {
		final Bestellung bestellung = dao.find(id);
		return bestellung;
	}

	/**
	 */
	
	@Override
	public Bestellung findBestellungByIdMitLieferungen(Long id) {
		final Bestellung bestellung = dao.findSingle(Bestellung.FIND_BESTELLUNG_BY_ID_FETCH_LIEFERUNGEN,
				                                     with(Bestellung.PARAM_ID, id).build());
		return bestellung;
	}


	/**
	 */
	@Override
	public Kunde findKundeById(Long id) {
		final Kunde kunde = kundeDao.findSingle(Bestellung.FIND_KUNDE_BY_ID,
                                                   with(Bestellung.PARAM_ID, id).build());
		return kunde;
	}

	/**
	 */
	@Override
	public List<Bestellung> findBestellungenByKundeId(Long kundeId) {
		final List<Bestellung> bestellungen = dao.find(Bestellung.FIND_BESTELLUNGEN_BY_KUNDE,
                                                       with(Bestellung.PARAM_KUNDEID, kundeId).build());
		return bestellungen;
	}


	/**
	 */
	@Override
	public Bestellung createBestellung(Bestellung bestellung,
			                           Kunde kunde,
			                           Locale locale) {
		if (bestellung == null) {
			return null;
		}
		
		for (Bestellposition bp : bestellung.getBestellpositionen()) {
			LOGGER.log(FINEST, "Bestellposition: {0}", bp);				
		}
		
		// damit "kunde" dem EntityManager bekannt ("managed") ist
		kunde = kv.findKundeById(kunde.getId(), FetchType.MIT_BESTELLUNGEN);
		kunde.addBestellung(bestellung);
		bestellung.setKunde(kunde);
		
		// Keine IDs vor dem Abspeichern
		bestellung.setId(KEINE_ID);
		for (Bestellposition bp : bestellung.getBestellpositionen()) {
			bp.setId(KEINE_ID);
		}
		
		validateBestellung(bestellung, locale, Default.class);
		dao.create(bestellung);
		event.fire(bestellung);

		return bestellung;
	}
	
	private void validateBestellung(Bestellung bestellung, Locale locale, Class<?>... groups) {
		final Validator validator = validationService.getValidator(locale);
		
		final Set<ConstraintViolation<Bestellung>> violations = validator.validate(bestellung);
		if (violations != null && !violations.isEmpty()) {
			LOGGER.exiting("Bestellverwaltung", "createBestellung", violations);
			throw new BestellungValidationException(bestellung, violations);
		}
	}


	/**
	 */
	/*
	@Override
	public List<Artikel> ladenhueter(int anzahl) {
		final List<Artikel> artikel = artikelDao.find(Bestellposition.FIND_LADENHUETER, anzahl);
		return artikel;
	}
	*/
	
	/**
	 */

	@Override
	public List<Versand> findVersand(Long id) {
		final List<Versand> versand =
				              versandDao.find(Versand.FIND_VERSAND_BY_ID_FETCH_BESTELLUNGEN,
                                                with(Versand.PARAM_VERSAND_ID, id).build());
		
		return versand;
	}

	/**
	 */
	
	@Override
	public Versand createVersand(Versand versand, List<Bestellung> bestellungen) {
		if (versand == null || bestellungen == null || bestellungen.isEmpty()) {
			return null;
		}
		
		// Beziehungen zu existierenden Bestellungen aktualisieren
		
		// Ids ermitteln
		final List<Long> ids = new ArrayList<>();
		for (Bestellung b : bestellungen) {
			ids.add(b.getId());
		}
		
		bestellungen = dao.findBestellungenByIds(ids);
		versand.setBestellungen(bestellungen);
		for (Bestellung bestellung : bestellungen) {
			bestellung.addVersand(versand);
		}
		
		versand.setId(KEINE_ID);
		versand = versandDao.create(versand);
		
		return versand;
	}

}
