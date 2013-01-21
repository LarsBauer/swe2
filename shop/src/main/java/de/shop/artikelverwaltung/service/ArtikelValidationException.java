package de.shop.artikelverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.AbstractShopException;

@ApplicationException(rollback = true)
public class ArtikelValidationException extends AbstractShopException {
	private static final long serialVersionUID = 4255133082483647701L;
	private final Artikel artikel;
	private final Collection<ConstraintViolation<Artikel>> violations;

	public ArtikelValidationException(Artikel artikel,
			                        Collection<ConstraintViolation<Artikel>> violations) {
		super("Ungueltiger Artikel: " + artikel + ", Violations: " + violations);
		this.artikel = artikel;
		this.violations = violations;
	}

	public Artikel getArtikel() {
		return artikel;
	}

	public Collection<ConstraintViolation<Artikel>> getViolations() {
		return violations;
	}
}

