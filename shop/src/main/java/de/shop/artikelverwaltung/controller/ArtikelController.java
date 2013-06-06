package de.shop.artikelverwaltung.controller;

import static de.shop.util.Constants.JSF_INDEX;
import static de.shop.util.Constants.JSF_REDIRECT_SUFFIX;
import static de.shop.util.Messages.MessagesType.ARTIKELVERWALTUNG;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.SUPPORTS;
import static javax.persistence.PersistenceContextType.EXTENDED;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;

import org.jboss.logging.Logger;
import org.richfaces.cdi.push.Push;
import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.artikelverwaltung.service.ArtikelValidationException;
import de.shop.artikelverwaltung.service.ArtikelverwaltungException;
import de.shop.auth.controller.AuthController;
import de.shop.util.AbstractShopException;
import de.shop.util.Client;
import de.shop.util.ConcurrentDeletedException;
import de.shop.util.Log;
import de.shop.util.Messages;
import de.shop.util.Transactional;


/**
 * Dialogsteuerung fuer die ArtikelService
 */
@Named("ac")
@SessionScoped
@TransactionAttribute(SUPPORTS)
@Log
public class ArtikelController implements Serializable {
	private static final long serialVersionUID = 1564024850446471639L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String JSF_ARTIKELVERWALTUNG = "/artikelverwaltung/";
	private static final String JSF_VIEW_ARTIKEL = JSF_ARTIKELVERWALTUNG + "viewArtikel";
	private static final String JSF_LIST_ARTIKEL = JSF_ARTIKELVERWALTUNG + "listArtikel";
	private static final String JSF_UPDATE_ARTIKEL = JSF_ARTIKELVERWALTUNG + "updateArtikel";
	//private static final String FLASH_ARTIKEL = "artikel";
	
	private static final String JSF_SELECT_ARTIKEL = JSF_ARTIKELVERWALTUNG + "selectArtikel";
	private static final String SESSION_VERFUEGBARE_ARTIKEL = "verfuegbareArtikel";

	private static final String MSG_KEY_UPDATE_ARTIKEL_CONCURRENT_UPDATE = "updateArtikel.concurrentUpdate";
	private static final String MSG_KEY_UPDATE_ARTIKEL_CONCURRENT_DELETE = "updateArtikel.concurrentDelete";

	private static final String REQUEST_ARTIKEL_ID = "artikelId";

	private String bezeichnung;
	private Long artikelId;
	private Artikel artikel;
	private Artikel neuerArtikel;
	private boolean geaendertArtikel;
	private List<Artikel> artikelList = Collections.emptyList();


	@PersistenceContext(type = EXTENDED)
	private transient EntityManager em;
	
	@Inject
	private ArtikelService as;
	
	@Inject
	private transient HttpServletRequest request;
	
	/*
	@Inject
	private Flash flash;
	*/
	
	@Inject
	private AuthController auth;
	
	@Inject
	@Client
	private Locale locale;
	
	@Inject
	@Push(topic = "marketing")
	private transient Event<String> neuerArtikelEvent;
	
	@Inject
	@Push(topic = "updateArtikel")
	private transient Event<String> updateArtikelEvent;
	
	@Inject
	private transient HttpSession session;
	
	@Inject
	private Messages messages;

	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}

	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	@Override
	public String toString() {
		return "ArtikelController [bezeichnung=" + bezeichnung + "]";
	}

	public String getBezeichnung() {
		return bezeichnung;
	}

	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}
	
	public Long getArtikelId() {
		return artikelId;
	}
	
	public void setArtikelId(Long artikelId) {
		this.artikelId = artikelId;
	}
	
	public Artikel getArtikel() {
		return artikel;
	}
	
	public void setArtikel(Artikel artikel) {
		this.artikel = artikel;
	}
	
	public Artikel getNeuerArtikel() {
		return neuerArtikel;
	}
	
	public void setNeuerArtikel(Artikel neuerArtikel) {
		this.neuerArtikel = neuerArtikel;
	}
	
	public List<Artikel> getArtikelList() {
		return artikelList;
	}

	@Transactional
	public String findArtikelByBezeichnung() {
		if (bezeichnung == null || bezeichnung.isEmpty()) {
			artikelList = as.findVerfuegbareArtikel();

			return JSF_LIST_ARTIKEL;
		}
		
		try {
		artikelList = as.findArtikelByBezeichnung(bezeichnung);
		}
		catch (ArtikelValidationException e) {
			final Collection<ConstraintViolation<Artikel>> violations = e.getViolations();
			messages.error(violations, null);
			return null;
		}

		return JSF_LIST_ARTIKEL;
	}
	
	@TransactionAttribute(REQUIRED)
	public String details(Artikel ausgewaehlterArtikel) {
		if (ausgewaehlterArtikel == null) {
			return null;
		}
		
		this.artikel = as.findArtikelById(ausgewaehlterArtikel.getId());
		this.artikelId = artikel.getId();
		
		return JSF_VIEW_ARTIKEL;
	}
		
	@Transactional
	public String selectArtikel() {
		if (session.getAttribute(SESSION_VERFUEGBARE_ARTIKEL) != null) {
			return JSF_SELECT_ARTIKEL;
		}
		
		final List<Artikel> alleArtikel = as.findVerfuegbareArtikel();
		session.setAttribute(SESSION_VERFUEGBARE_ARTIKEL, alleArtikel);
		return JSF_SELECT_ARTIKEL;
	}
	
	@Transactional
	@TransactionAttribute(REQUIRED)
	public String createArtikel() {
		
		try {
			neuerArtikel = as.createArtikel(neuerArtikel, locale);
		}
		catch (ArtikelValidationException e) {
			final String outcome = createArtikelErrorMsg(e);
			return outcome;
		}

		// Push-Event fuer Webbrowser
		//neuerArtikelEvent.fire(String.valueOf(neuerArtikel.getId()));
		
		// Aufbereitung fuer viewArtikel.xhtml
		artikelId = neuerArtikel.getId();
		artikel = neuerArtikel;
		neuerArtikel = null;  // zuruecksetzen
		
		return JSF_VIEW_ARTIKEL + JSF_REDIRECT_SUFFIX;
	}
	
	private String createArtikelErrorMsg(AbstractShopException e) {
		final Class<? extends AbstractShopException> exceptionClass = e.getClass();
		final ArtikelValidationException orig = (ArtikelValidationException) e;
		messages.error(orig.getViolations(), null);
		return null;
	}
	
	public void createEmptyArtikel() {
		if (neuerArtikel != null) {
			return;
		}

		neuerArtikel = new Artikel();	
	}
	
	@Transactional
	@TransactionAttribute(REQUIRED)
	public String update() {
		auth.preserveLogin();
		
		if (!geaendertArtikel || artikel == null) {
			return JSF_INDEX;
		}
		
		LOGGER.tracef("Aktualisierter Artikel: %s", artikel);
		try {
			artikel = as.updateArtikel(artikel, locale);
		}
		catch (ArtikelverwaltungException | ArtikelValidationException
			  | OptimisticLockException | ConcurrentDeletedException e) {
			final String outcome = updateErrorMsg(e, artikel.getClass());
			return outcome;
		}

		// Push-Event fuer Webbrowser
		updateArtikelEvent.fire(String.valueOf(artikel.getId()));
		
		// ValueChangeListener zuruecksetzen
		geaendertArtikel = false;
		
		// Aufbereitung fuer viewArtikel.xhtml
		artikelId = artikel.getId();
		
		return JSF_VIEW_ARTIKEL + JSF_REDIRECT_SUFFIX;
	}
	
	private String updateErrorMsg(RuntimeException e, Class<? extends Artikel> artikelClass) {
		final Class<? extends RuntimeException> exceptionClass = e.getClass();
		if (exceptionClass.equals(ArtikelValidationException.class)) {
			final ArtikelValidationException orig = (ArtikelValidationException) e;
			final Collection<ConstraintViolation<Artikel>> violations = orig.getViolations();
			messages.error(violations, null);
		}
		else if (exceptionClass.equals(OptimisticLockException.class)) {
				messages.error(ARTIKELVERWALTUNG, MSG_KEY_UPDATE_ARTIKEL_CONCURRENT_UPDATE, null);
		}
		else if (exceptionClass.equals(ConcurrentDeletedException.class)) {
				messages.error(ARTIKELVERWALTUNG, MSG_KEY_UPDATE_ARTIKEL_CONCURRENT_DELETE, null);
		}
		return null;
	}
	
	public String selectForUpdate(Artikel ausgewaehlterArtikel) {
		if (ausgewaehlterArtikel == null) {
			return null;
		}
		
		artikel = ausgewaehlterArtikel;
		
		return JSF_UPDATE_ARTIKEL;
	}
	
	public void geaendert(ValueChangeEvent e) {
		if (geaendertArtikel) {
			return;
		}
		
		if (e.getOldValue() == null) {
			if (e.getNewValue() != null) {
				geaendertArtikel = true;
			}
			return;
		}

		if (!e.getOldValue().equals(e.getNewValue())) {
			geaendertArtikel = true;				
		}
	}
}
