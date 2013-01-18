package de.shop.artikelverwaltung.service;

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

import com.google.common.base.Strings;

import de.shop.artikelverwaltung.dao.ArtikelDao;
import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.Log;
import de.shop.util.ValidationService;

@Log
public class Artikelverwaltung implements Serializable {
	private static final long serialVersionUID = 3076865030092242363L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	private ArtikelDao dao;
	
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
	public List<Artikel> findVerfuegbareArtikel() {
		final List<Artikel> artikelListe = dao.find(Artikel.FIND_VERFUEGBARE_ARTIKEL);
		return artikelListe;
	}

	
	/**
	 */
	public Artikel findArtikelById(Long id) {
		final Artikel artikel = dao.find(id);
		return artikel;
	}
	
	/**
	 */
	public List<Artikel> findArtikelByIds(List<Long> ids) {
		final List<Artikel> artikel = dao.findArtikelByIds(ids);
		return artikel;
	}

	
	/**
	 */
	public List<Artikel> findArtikelByName(String name) {
		if (Strings.isNullOrEmpty(name)) {
			final List<Artikel> artikelListe = findVerfuegbareArtikel();
			return artikelListe;
		}
		
		final List<Artikel> artikelListe = dao.find(Artikel.FIND_ARTIKEL_BY_NAME,
				                                    with(Artikel.PARAM_NAME,
				                                    	 "%" + name + "%").build());
		
		return artikelListe;
	}
	
	/**
	 */
	public List<Artikel> findArtikelByMaxPreis(double preis) {
		final List<Artikel> artikelListe = dao.find(Artikel.FIND_ARTIKEL_MAX_PREIS,
				                                    with(Artikel.PARAM_PREIS_MAX, preis).build());
		return artikelListe;
	}
	
	public Artikel createArtikel(Artikel artikel, Locale locale) {
		if (artikel == null) {
			return artikel;
		}
		
		validateArtikel(artikel, locale, Default.class);
		
		artikel.setId(KEINE_ID);
		artikel = dao.create(artikel);
		
		return artikel;
	}
	
	private void validateArtikel(Artikel artikel, Locale locale, Class<?>... groups) {
		// Werden alle Constraints beim Einfuegen gewahrt?
		final Validator validator = validationService.getValidator(locale);
		
		final Set<ConstraintViolation<Artikel>> violations = validator.validate(artikel, groups);
		if (!violations.isEmpty()) {
			throw new ArtikelValidationException(artikel, violations);
		}
	}
	
}
