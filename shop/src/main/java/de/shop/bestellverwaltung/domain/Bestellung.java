package de.shop.bestellverwaltung.domain;


import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;

import java.io.Serializable;
import javax.persistence.*;

import de.shop.kundenverwaltung.domain.Kunde;

import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the bestellung database table.
 * 
 */
@Entity
public class Bestellung implements Serializable {
	private static final long serialVersionUID = 1L;

	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="b_id", unique=true, nullable=false, updatable=false)
	private Long id;

	@Column(nullable=false)
	private Date aktualisiert;

	@Column(nullable=false)
	private Date erzeugt;

	@ManyToOne(optional=false)
	@JoinColumn(name="kunde_fk", nullable=false, insertable=false, updatable=false)
	private Kunde kunde;
	
	@OneToMany(fetch = EAGER, cascade = { PERSIST, REMOVE })
	@JoinColumn(name = "bestellung_fk", nullable = false)
	private List<Bestellposition> bestellpositionen;
	
	@ManyToMany
	@JoinTable(name = "bestellung_versand",
			   joinColumns = @JoinColumn(name = "bestellung_fk"),
			                 inverseJoinColumns = @JoinColumn(name = "versand_fk"))
	private List<Versand> versand;

	@Column(nullable=false)
	private String status;

	public Bestellung() {
		super();
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public List<Bestellposition> getBestellpositionen() {
		return Collections.unmodifiableList(bestellpositionen);
	}
	
	public void setBestellpositionen(List<Bestellposition> bestellpositionen) {
		if (this.bestellpositionen == null) {
			this.bestellpositionen = bestellpositionen;
			return;
		}
		
		// Wiederverwendung der vorhandenen Collection
		this.bestellpositionen.clear();
		if (bestellpositionen != null) {
			this.bestellpositionen.addAll(bestellpositionen);
		}
	}
	
	public List<Versand> getVersand() {
		return Collections.unmodifiableList(versand);
	}
	public void setVersand(List<Versand> versand) {
		if (this.versand == null) {
			this.versand = versand;
			return;
		}
		
		// Wiederverwendung der vorhandenen Collection
		this.versand.clear();
		if (versand != null) {
			this.versand.addAll(versand);
		}
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

	public Kunde getKunde() {
		return this.kunde;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		final Long kundeId = kunde == null ? null : kunde.getId();
		return "Bestellung [id=" + id + ", kundeId=" + kundeId
		       + ", erzeugt=" + erzeugt
		       + ", aktualisiert=" + aktualisiert + ']';
	}

}