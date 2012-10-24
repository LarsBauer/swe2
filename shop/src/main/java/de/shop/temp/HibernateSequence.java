package de.shop.temp;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the hibernate_sequence database table.
 * 
 */
@Entity
@Table(name="hibernate_sequence")
public class HibernateSequence implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="next_val")
	private String nextVal;

	public HibernateSequence() {
	}

	public String getNextVal() {
		return this.nextVal;
	}

	public void setNextVal(String nextVal) {
		this.nextVal = nextVal;
	}

}