package de.shop.kundenverwaltung.service;

import static de.shop.util.AbstractDao.QueryParameter.with;
import static de.shop.util.Constants.KEINE_ID;
import static java.util.logging.Level.FINER;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import de.shop.kundenverwaltung.dao.KundeDao;
import de.shop.kundenverwaltung.dao.KundeDao.FetchType;
import de.shop.kundenverwaltung.dao.KundeDao.OrderType;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.domain.PasswordGroup;
import de.shop.util.IdGroup;
import de.shop.util.Log;
import de.shop.util.ValidationService;

/**
 * Anwendungslogik fuer die Kundenverwaltung
 */
@Log
public class Kundenverwaltung implements Serializable {
	private static final long serialVersionUID = -5520738420154763865L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	private KundeDao dao;
	
	@Inject
	private ValidationService validationService;
	
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
	public List<Kunde> findAllKunden(FetchType fetch, OrderType order) {
		final List<Kunde> kunden = dao.findAllKunden(fetch, order);
		return kunden;
	}
	
	/**
	 */
	public List<Kunde> findKundenByNachname(String nachname, FetchType fetch) {
		final List<Kunde> kunden = dao.findKundenByName(nachname, fetch);
		return kunden;
	}
	
	/*
	public List<String> findNachnamenByPrefix(String nachnamePrefix) {
		final List<String> nachnamen = dao.find(String.class, Kunde.FIND_NACHNAMEN_BY_PREFIX,
		                                        with(Kunde.PARAM_KUNDE_NACHNAME_PREFIX,
		                                        	 nachnamePrefix + '%').build());
		return nachnamen;
	}
	*/

	/**
	 */
	public Kunde findKundeById(Long id, FetchType fetch) {
		final Kunde kunde = dao.findKundeById(id, fetch);
		return kunde;
	}
	
	/*
	public List<Long> findIdsByPrefix(String idPrefix) {
		final List<Long> ids = dao.find(Long.class, AbstractKunde.FIND_IDS_BY_PREFIX,
		                                with(AbstractKunde.PARAM_KUNDE_ID_PREFIX,
		                                   	 idPrefix + '%').build());
		return ids;
	}
	*/
	
	/**
	 */
	public Kunde findKundeByEmail(String email) {
		final Kunde kunde = dao.findSingle(Kunde.FIND_KUNDE_BY_EMAIL,
                                                   with(Kunde.PARAM_KUNDE_EMAIL, email).build());
		return kunde;
	}
	
	/**
	 */
	/*
	public AbstractKunde findKundeMitWartungsvertraegenById(Long id) {
		final AbstractKunde kunde = dao.findSingle(AbstractKunde.FIND_KUNDE_BY_ID_FETCH_WARTUNGSVERTRAEGE,
                                                   with(AbstractKunde.PARAM_KUNDE_ID, id).build());
		return kunde;
	}
	*/

	/**
	 */
	public Kunde createKunde(Kunde kunde, Locale locale) {
		if (kunde == null) {
			return kunde;
		}

		// Werden alle Constraints beim Einfuegen gewahrt?
		validateKunde(kunde, locale, Default.class, PasswordGroup.class);
		
		// Pruefung, ob die Email-Adresse schon existiert
		final Kunde vorhandenerKunde = dao.findSingle(Kunde.FIND_KUNDE_BY_EMAIL,
                                                              with(Kunde.PARAM_KUNDE_EMAIL,
                                                               	   kunde.getEmail()).build());
		if (vorhandenerKunde != null) {
			throw new EmailExistsException(kunde.getEmail());
		}
		LOGGER.finest("Email-Adresse existiert noch nicht");
		
		kunde.setId(KEINE_ID);
		kunde = dao.create(kunde);
		
		return kunde;
	}
	
	private void validateKunde(Kunde kunde, Locale locale, Class<?>... groups) {
		// Werden alle Constraints beim Einfuegen gewahrt?
		final Validator validator = validationService.getValidator(locale);
		
		final Set<ConstraintViolation<Kunde>> violations = validator.validate(kunde, groups);
		if (!violations.isEmpty()) {
			throw new KundeValidationException(kunde, violations);
		}
	}
	
	/**
	 */
	public Kunde updateKunde(Kunde kunde, Locale locale) {
		if (kunde == null) {
			return null;
		}

		// Werden alle Constraints beim Modifizieren gewahrt?
		validateKunde(kunde, locale, Default.class, PasswordGroup.class, IdGroup.class);
		
		// Pruefung, ob die Email-Adresse schon existiert
		final Kunde vorhandenerKunde = dao.findSingle(Kunde.FIND_KUNDE_BY_EMAIL,
                                                              with(Kunde.PARAM_KUNDE_EMAIL,
                                                            	   kunde.getEmail()).build());
		if (vorhandenerKunde != null && vorhandenerKunde.getId().longValue() != kunde.getId().longValue()) {
			throw new EmailExistsException(kunde.getEmail());
		}
		LOGGER.finest("Email-Adresse existiert noch nicht");

		kunde = dao.update(kunde);
		return kunde;
	}

	/**
	 */
	public void deleteKunde(Kunde kunde) {
		if (kunde == null) {
			return;
		}
		
		// Bestellungen laden, damit sie anschl. ueberprueft werden koennen
		kunde = findKundeById(kunde.getId(), FetchType.MIT_BESTELLUNGEN);
		if (kunde == null) {
			return;
		}
		
		// Gibt es Bestellungen?
		if (!kunde.getBestellungen().isEmpty()) {
			throw new KundeDeleteBestellungException(kunde);
		}

		dao.delete(kunde);
	}

	/**
	 */
	public List<Kunde> findKundenByPLZ(String plz) {
		final List<Kunde> kunden = dao.find(Kunde.FIND_KUNDEN_BY_PLZ,
                                                    with(Kunde.PARAM_KUNDE_ADRESSE_PLZ, plz).build());
		return kunden;
	}

	/**
	 */
	/*
	public List<AbstractKunde> findKundenBySeit(Date seit) {
		final List<AbstractKunde> kunden = dao.find(AbstractKunde.FIND_KUNDEN_BY_DATE,
                                                    with(AbstractKunde.PARAM_KUNDE_SEIT, seit).build());
		return kunden;
	}
	*/
	
	/**
	 */
	/*
	public List<AbstractKunde> findPrivatkundenFirmenkunden() {
		final List<AbstractKunde> kunden = dao.find(AbstractKunde.FIND_PRIVATKUNDEN_FIRMENKUNDEN);
		return kunden;
	}
	*/
	
	/**
	 */
	public List<Kunde> findKundenByNachnameCriteria(String nachname) {
		final List<Kunde> kunden = dao.findKundenByNachname(nachname);
		return kunden;
	}
	
	/**
	 */
	public List<Kunde> findKundenMitMinBestMenge(short minMenge) {
		final List<Kunde> kunden = dao.findKundenMitMinBestMenge(minMenge);
		return kunden;
	}
	
	/**
	 */
	/*
	public List<Wartungsvertrag> findWartungsvertraege(Long kundeId) {
		final List<Wartungsvertrag> wartungsvertraege =
				                    wartungsvertraDao.find(Wartungsvertrag.FIND_WARTUNGSVERTRAEGE_BY_KUNDE_ID,
                                                           with(Wartungsvertrag.PARAM_KUNDE_ID, kundeId).build());
		return wartungsvertraege;
	}
	*/
	
	/**
	 */
	/*
	public Wartungsvertrag createWartungsvertrag(Wartungsvertrag wartungsvertrag,
			                                     AbstractKunde kunde) {
		if (wartungsvertrag == null || kunde == null) {
			return null;
		}
		
		kunde = dao.findKundeById(kunde.getId(), FetchType.NUR_KUNDE);
		wartungsvertrag.setKunde(kunde);
		kunde.addWartungsvertrag(wartungsvertrag);
		
		wartungsvertrag = wartungsvertraDao.create(wartungsvertrag);
		return wartungsvertrag;
	}
	*/
}
