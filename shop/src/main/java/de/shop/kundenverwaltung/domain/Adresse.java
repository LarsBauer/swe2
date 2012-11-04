package de.shop.kundenverwaltung.domain;

import static de.shop.util.Constants.*;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "adresse")
public class Adresse implements Serializable {
	private static final long serialVersionUID = 2170899226912083774L;
	
	public static final int PLZ_LENGTH_MAX = 5;
	public static final int ORT_LENGTH_MIN = 2;
	public static final int ORT_LENGTH_MAX = 32;
	public static final int STRASSE_LENGTH_MIN = 2;
	public static final int STRASSE_LENGTH_MAX = 32;
	public static final int HAUSNR_LENGTH_MAX = 4;
	
	@Id
	@GeneratedValue
	@Column(name = "ad_id", unique = true, nullable = false, updatable = false, precision = LONG_ANZ_ZIFFERN)
	//	@Min(value = MIN_ID, message = "{kundenverwaltung.adresse.id.min}", groups = IdGroup.class)
	@Min(value = MIN_ID, message = "{kundenverwaltung.adresse.id.min}")
	private Long id = KEINE_ID;
	
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

	@Column(length = HAUSNR_LENGTH_MAX)
	@Size(max = HAUSNR_LENGTH_MAX, message = "{kundenverwaltung.adresse.hausnr.length}")
	private String hausnr;

	@OneToOne
	@JoinColumn(name = "kunde_fk", nullable = false)
	@NotNull(message = "{kundenverwaltung.adresse.kunde.notNull}")
	private Kunde kunde;


	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	private Date erzeugt;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	private Date aktualisiert;
	

	@SuppressWarnings("unused")
	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@SuppressWarnings("unused")
	@PreUpdate
	private void preUpdate() {
		aktualisiert = new Date();
	}


	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getPlz() {
		return plz;
	}
	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getOrt() {
		return ort;
	}
	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getStrasse() {
		return strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

	public String getHausnr() {
		return hausnr;
	}
	public void setHausnr(String hausnr) {
		this.hausnr = hausnr;
	}

	public void setKunde(Kunde pkunde) {
		this.kunde = pkunde;
	}
	public Kunde getKunde() {
		return kunde;
	}
	@Override
	public String toString() {
		return "Adresse [id=" + id + ", plz=" + plz + ", ort=" + ort + ", strasse=" + strasse + ", hausnr=" + hausnr
		       + ", erzeugt=" + erzeugt + ", aktualisiert=" + aktualisiert + ']';
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hausnr == null) ? 0 : hausnr.hashCode());
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
		
		if (plz == null) {
			if (other.plz != null) {
				return false;
			}
		}
		else if (!plz.equals(other.plz)) {
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
		
		if (strasse == null) {
			if (other.strasse != null) {
				return false;
			}
		}
		else if (!strasse.equals(other.strasse)) {
			return false;
		}
		
		if (hausnr == null) {
			if (other.hausnr != null) {
				return false;
			}
		}
		else if (!hausnr.equals(other.hausnr)) {
			return false;
		}
		
		return true;
	}
}