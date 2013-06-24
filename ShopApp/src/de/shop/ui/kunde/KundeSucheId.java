package de.shop.ui.kunde;

import static de.shop.util.Constants.KUNDE_KEY;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import de.shop.R;
import de.shop.data.Kunde;
import de.shop.ui.main.Main;
import de.shop.ui.main.Prefs;

public class KundeSucheId extends Fragment implements OnClickListener {	
	private EditText kundeIdTxt;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		// attachToRoot = false, weil die Verwaltung des Fragments durch die Activity erfolgt
		return inflater.inflate(R.layout.kunden_suche_id, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		kundeIdTxt = (EditText) view.findViewById(R.id.kunde_id_edt);
    	
		// KundeSucheId (this) ist gleichzeitig der Listener, wenn der Suchen-Button angeklickt wird
		// und implementiert deshalb die Methode onClick() unten
		view.findViewById(R.id.btn_suchen).setOnClickListener(this);
		
	    // Evtl. vorhandene Tabs der ACTIVITY loeschen
    	final ActionBar actionBar = getActivity().getActionBar();
    	actionBar.setDisplayShowTitleEnabled(true);
    	actionBar.removeAllTabs();
    }
	
	@Override
	// Nur aufgerufen, falls setHasOptionsMenu(true) in onCreateView() aufgerufen wird
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.einstellungen:
				getFragmentManager().beginTransaction()
                                    .replace(R.id.details, new Prefs())
                                    .addToBackStack(null)
                                    .commit();
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override // OnClickListener
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_suchen:
				final String kundeIdStr = kundeIdTxt.getText().toString();
				final Long kundeId = Long.valueOf(kundeIdStr);
				final Main mainActivity = (Main) getActivity();
				final Kunde kunde = mainActivity.getKundeServiceBinder().sucheKundeById(kundeId);
				
				final Bundle args = new Bundle(1);
				args.putSerializable(KUNDE_KEY, kunde);
				
				final Fragment neuesFragment = new KundeDetails();
				neuesFragment.setArguments(args);
				
				// Kein Name (null) fuer die Transaktion, da die Klasse BackStageEntry nicht verwendet wird
				getFragmentManager().beginTransaction()
				                    .replace(R.id.details, neuesFragment)
				                    .addToBackStack(null)
				                    .commit();
				break;
				
			default:
				break;
		}
    }
}
