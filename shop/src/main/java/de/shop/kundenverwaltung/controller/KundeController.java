package de.shop.kundenverwaltung.controller;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.Log;
import de.shop.util.Transactional;


/**
 * Dialogsteuerung fuer die Kundenverwaltung
 */
@Named("kc")
@RequestScoped
@Log
public class KundeController implements Serializable {
	private static final long serialVersionUID = -8817180909526894740L;
	
	private static final String FLASH_KUNDE = "kunde";
	private static final String FLASH_KUNDEN = "kunden";
	private static final String JSF_VIEW_KUNDE = "/kundenverwaltung/viewKunde";
	private static final String JSF_LIST_KUNDEN = "/kundenverwaltung/listKunden";
	
	@Inject
	private KundeService ks;
	
	@Inject
	private Flash flash;
	
	private Long kundeId;
	private String nachname;
	

	@Override
	public String toString() {
		return "KundeController [kundeId=" + kundeId + "nachname=" + nachname +"]";
	}

	public void setKundeId(Long kundeId) {
		this.kundeId = kundeId;
	}

	public Long getKundeId() {
		return kundeId;
	}
	
	public String getNachname(){
		return nachname;
	}
	
	public void setNachname(String nachname){
		this.nachname = nachname;
	}

	/**
	 * Action Methode, um einen Kunden zu gegebener ID zu suchen
	 * @return URL fuer Anzeige des gefundenen Kunden; sonst null
	 */
	@Transactional
	public String findKundeById() {
		final Kunde kunde = ks.findKundeById(kundeId, FetchType.NUR_KUNDE, null);
		if (kunde == null) {
			flash.remove(FLASH_KUNDE);
			return null;
		}
		
		flash.put(FLASH_KUNDE, kunde);
		return JSF_VIEW_KUNDE;
	}
	
	@Transactional
	public String findKundenByNachname() {
		final List<Kunde>  kunden = ks.findKundenByNachname(nachname, FetchType.NUR_KUNDE, null);
		if (kunden == null) {
			flash.remove(FLASH_KUNDEN);
			return null;
		}
		
		flash.put(FLASH_KUNDEN, kunden);
		return JSF_LIST_KUNDEN;
	}
}
