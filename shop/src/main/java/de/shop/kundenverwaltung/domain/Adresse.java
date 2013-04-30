package de.shop.kundenverwaltung.domain;


import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.MIN_ID;
import static javax.persistence.TemporalType.TIMESTAMP;
import static de.shop.util.Constants.ERSTE_VERSION;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.jboss.logging.Logger;

import de.shop.util.IdGroup;


/**
 * The persistent class for the adresse database table.
 * 
 */
@Entity
@Table(name = "adresse")
public class Adresse implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	public static final int PLZ_LENGTH_MAX = 5;
	public static final int ORT_LENGTH_MIN = 2;
	public static final int ORT_LENGTH_MAX = 32;
	public static final int STRASSE_LENGTH_MIN = 2;
	public static final int STRASSE_LENGTH_MAX = 32;
	public static final int HAUSNR_LENGTH_MAX = 4;

	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false, updatable = false)
	@Min(value = MIN_ID, message = "{kundenverwaltung.adresse.id.min}", groups = IdGroup.class)
	private Long id = KEINE_ID;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;

	@Column(length = PLZ_LENGTH_MAX, nullable = false)
	@NotNull(message = "{kundenverwaltung.adresse.plz.notNull}")
	@Pattern(regexp = "\\d{5}", message = "{kundenverwaltung.adresse.plz}")
	private String plz;

	@Column(length = ORT_LENGTH_MAX, nullable = false)
	@NotNull(message = "{kundenverwaltung.adresse.ort.notNull}")
	@Size(min = ORT_LENGTH_MIN, max = ORT_LENGTH_MAX, message = "{kundenverwaltung.adresse.ort.length}")
	private String ort;

	@Column(length = STRASSE_LENGTH_MAX, nullable = false)
	@NotNull(message = "{kundenverwaltung.adresse.strasse.notNull}")
	@Size(min = STRASSE_LENGTH_MIN, max = STRASSE_LENGTH_MAX, message = "{kundenverwaltung.adresse.strasse.length}")
	private String strasse;

	@Column(length = HAUSNR_LENGTH_MAX, nullable = false)
	@Size(max = HAUSNR_LENGTH_MAX, message = "{kundenverwaltung.adresse.hausnr.length}")
	private String hausnummer;

	@OneToOne
	@JoinColumn(name = "kunde_fk", nullable = false)
	@NotNull(message = "{kundenverwaltung.adresse.kunde.notNull}")
	@JsonIgnore
	private Kunde kunde;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date erzeugt;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date aktualisiert;


	public Adresse() {
		super();
	}
	
	public Adresse(String plz, String ort, String strasse, String hausnr) {
		super();
		this.plz = plz;
		this.ort = ort;
		this.strasse = strasse;
		this.hausnummer = hausnr;
	}
	
	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}

	@PreUpdate
	private void preUpdate() {
		aktualisiert = new Date();
	}
	
	@PostPersist
	private void postPersist() {
		LOGGER.debugf("Neue Adresse mit ID=%s", id);
	}
	
	@PostUpdate
	private void postUpdate() {
		LOGGER.debugf("Adresse mit ID=%d aktualisiert: version=%d", id, version);
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getPlz() {
		return this.plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getOrt() {
		return this.ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getStrasse() {
		return this.strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

	public String getHausnummer() {
		return this.hausnummer;
	}

	public void setHausnummer(String hausnummer) {
		this.hausnummer = hausnummer;
	}

	public Kunde getKunde() {
		return this.kunde;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}

	public Date getErzeugt() {
		return erzeugt == null ? null : (Date) erzeugt.clone();
	}

	public void setErzeugt(Date erzeugt) {
		this.erzeugt = erzeugt == null ? null : (Date) erzeugt.clone();
	}

	public Date getAktualisiert() {
		return aktualisiert == null ? null : (Date) aktualisiert.clone();
	}

	public void setAktualisiert(Date aktualisiert) {
		this.aktualisiert = aktualisiert == null ? null : (Date) aktualisiert.clone();
	}

	@Override
	public String toString() {
		return "Adresse [id=" + id + ", plz=" + plz + ", ort=" + ort + ", strasse=" + strasse
		       + ", hausnummer=" + hausnummer + ']';
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hausnummer == null) ? 0 : hausnummer.hashCode());
		result = prime * result + ((ort == null) ? 0 : ort.hashCode());
		result = prime * result + ((plz == null) ? 0 : plz.hashCode());
		result = prime * result + ((strasse == null) ? 0 : strasse.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Adresse other = (Adresse) obj;
		if (hausnummer == null) {
			if (other.hausnummer != null) {
				return false;
			}
		}
		else if (!hausnummer.equals(other.hausnummer)) {
			return false;
		}
		if (ort == null) {
			if (other.ort != null) {
				return false;
			}
		}
		else if (!ort.equals(other.ort)) {
			return false;
		}
		if (plz == null) {
			if (other.plz != null) {
				return false;
			}
		}
		else if (!plz.equals(other.plz)) {
			return false;
		}
		if (strasse == null) {
			if (other.strasse != null) {
				return false;
			}
		}
		else if (!strasse.equals(other.strasse)) {
			return false;
		}
		return true;
	}

}
