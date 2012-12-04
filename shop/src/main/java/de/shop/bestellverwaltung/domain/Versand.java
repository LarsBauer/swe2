package de.shop.bestellverwaltung.domain;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.validation.Valid;
import org.hibernate.validator.constraints.NotEmpty;

import de.shop.util.PreExistingGroup;


/**
 * The persistent class for the versand database table.
 * 
 */
@Entity
public class Versand implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue()
	@Column(name = "v_id", unique = true, nullable = false, updatable = false)
	private Long id;
	
	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	private Date aktualisiert;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	private Date erzeugt;

	@Column(nullable = false)
	private String versandart;

	private double versandkosten;
	
	@ManyToMany(mappedBy = "versand", cascade = { PERSIST, MERGE })
	@NotEmpty(message = "{bestellverwaltung.lieferung.bestellungen.notEmpty}", groups = PreExistingGroup.class)
	@Valid
	private List<Bestellung> bestellungen;

	public Versand() {
		super();
	}
	
	@PrePersist
	protected void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PreUpdate
	protected void preUpdate() {
		aktualisiert = new Date();
	}

	public Date getAktualisiert() {
		return (Date)this.aktualisiert.clone();
	}

	public void setAktualisiert(Date aktualisiert) {
		this.aktualisiert = (Date)aktualisiert.clone();
	}

	public Date getErzeugt() {
		return (Date)this.erzeugt.clone();
	}

	public void setErzeugt(Date erzeugt) {
		this.erzeugt = (Date)erzeugt.clone();
	}

	public Long getId() {
		return this.id;
	}

	public void setVId(Long id) {
		this.id = id;
	}

	public String getVersandart() {
		return this.versandart;
	}

	public void setVersandart(String versandart) {
		this.versandart = versandart;
	}

	public double getVersandkosten() {
		return this.versandkosten;
	}

	public void setVersandkosten(double versandkosten) {
		this.versandkosten = versandkosten;
	}
	
	public List<Bestellung> getBestellungen() {
		return bestellungen == null ? null : Collections.unmodifiableList(bestellungen);
	}
	
	public void setBestellungen(List<Bestellung> bestellungen) {
		if (this.bestellungen == null) {
			this.bestellungen = bestellungen;
			return;
		}
		
		// Wiederverwendung der vorhandenen Collection
		this.bestellungen.clear();
		if (bestellungen != null) {
			this.bestellungen.addAll(bestellungen);
		}
	}
	
	public void addBestellung(Bestellung bestellung) {
		bestellungen.add(bestellung);
	}
	
	@Override
	public String toString() {
		return "Lieferung [id=" + id + ", versandart=" + versandart + ", versandkosten=" + versandkosten
				+ ", erzeugt=" + erzeugt 
				+ ", aktualisiert=" + aktualisiert + ']';
	}

}