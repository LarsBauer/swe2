package de.shop;

import de.shop.data.Kunde;
import de.shop.ui.kunde.KundeDetails;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import static de.shop.util.Constants.KUNDE_KEY;

public class Main extends Activity implements OnClickListener {
	private static final String LOG_TAG = Main.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
	
	@Override // OnClickListener
	public void onClick(View view) {
		final EditText kundeIdTxt = (EditText) findViewById(R.id.kunde_id);
		final String kundeId = kundeIdTxt.getText().toString();
		
		final Kunde kunde = getKunde(kundeId);
		
		// NICHT: new KundeDetails() !!!
		
		final Intent intent = new Intent(view.getContext(), KundeDetails.class);
		intent.putExtra(KUNDE_KEY, kunde);
		startActivity(intent);
	}
	
    private Kunde getKunde(String kundeIdStr) {
    	final Long kundeId = Long.valueOf(kundeIdStr);
    	final Kunde kunde = new Kunde(kundeId, "Name" + kundeIdStr);
    	Log.v(LOG_TAG, kunde.toString());
    	
    	return kunde;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
