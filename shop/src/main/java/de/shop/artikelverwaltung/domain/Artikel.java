package de.shop.artikelverwaltung.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the artikel database table.
 * 
 */
@Entity
@Table(name = "Artikel")
@NamedQueries({
	@NamedQuery(name = Artikel.FIND_ARTIKEL_BY_NAME,
		query = "SELECT      a"
				+ " FROM     Artikel a"
				+ " WHERE    a.name LIKE :" + Artikel.PARAM_NAME
				+ " ORDER BY a.id ASC"),
	
	@NamedQuery(name = Artikel.FIND_ARTIKEL_MAX_PREIS,
		query = "SELECT		 a"
				+ " FROM 	Artikel a"
				+ " WHERE	a.preis LIKE :" + Artikel.PARAM_PREIS
				+ " ORDER BY a.id ASC"),	
})

public class Artikel implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final int NAME_LENGTH_MAX = 32;
	private static final int GROESSE_LENGTH_MAX = 3;
	
	private static final String PREFIX = "Artikel.";
	public static final String FIND_ARTIKEL_BY_NAME = PREFIX + "findArtikelByName";
	public static final String FIND_ARTIKEL_MAX_PREIS = PREFIX + "findArtikelByMaxPreis";

	public static final String PARAM_NAME = "name";
	public static final String PARAM_PREIS = "preis";
	

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="a_id", unique=true, nullable=false, updatable=false)
	private Long id;

	@Column(nullable=false)
	private Date aktualisiert;

	@Column(nullable=false)
	private Date erzeugt;

	@Column(name="groesse", length=GROESSE_LENGTH_MAX, nullable=false)
	private String groesse;

	@Column(name="name", length=NAME_LENGTH_MAX, nullable=false)
	private String name;

	private double preis;

	public Artikel() {
		super();
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public void setErzeugt(Timestamp erzeugt) {
		this.erzeugt = (Date)erzeugt.clone();
	}

	public String getGroesse() {
		return this.groesse;
	}

	public void setGroesse(String groesse) {
		this.groesse = groesse;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPreis() {
		return this.preis;
	}

	public void setPreis(double preis) {
		this.preis = preis;
	}
	
	@Override
	public String toString() {
		return "Artikel [id=" + id + ", name=" + name
		       + ", preis=" + preis + ", groesse=" +groesse
		       + ", erzeugt=" + erzeugt
			   + ", aktualisiert=" + aktualisiert + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((name == null) ? 0 : name.hashCode());
		long temp;
		temp = Double.doubleToLongBits(preis);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Artikel other = (Artikel) obj;
		
		
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		}
		else if (!name.equals(other.name)) {
			return false;
		}
		
		if (Double.doubleToLongBits(preis) != Double.doubleToLongBits(other.preis)) {
			return false;
		}
		return true;
	}

}