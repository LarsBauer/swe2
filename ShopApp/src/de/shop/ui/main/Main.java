package de.shop.ui.main;

import static de.shop.util.Constants.KUNDE_KEY;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import de.shop.R;
import de.shop.data.Kunde;
import de.shop.service.KundeService;
import de.shop.service.KundeService.KundeServiceBinder;
import de.shop.ui.kunde.KundeDetails;

public class Main extends Activity implements OnClickListener {
	private static final String LOG_TAG = Main.class.getSimpleName();
	
	private KundeServiceBinder serviceBinder;
	
	// ServiceConnection ist ein Interface: anonyme Klasse verwenden, um ein Objekt davon zu erzeugen
	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
			Main.this.serviceBinder = (KundeServiceBinder) serviceBinder;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        findViewById(R.id.btn_suchen).setOnClickListener(this);
    }
    
	@Override
	protected void onStart() {
		final Intent intent = new Intent(this, KundeService.class);
		bindService(intent, serviceConnection, BIND_AUTO_CREATE);
		super.onStart();
	}

	@Override
	protected void onStop() {
		unbindService(serviceConnection);
		super.onStop();
	}

	@Override // OnClickListener
	public void onClick(View view) {
		final EditText kundeIdTxt = (EditText) findViewById(R.id.kunde_id);
		final String kundeIdStr = kundeIdTxt.getText().toString();
		Log.d(LOG_TAG, "kundeId = " + kundeIdStr);
		
		final Long kundeId = Long.parseLong(kundeIdStr);
		final Kunde kunde = serviceBinder.getKunde(kundeId);
		Log.d(LOG_TAG, kunde.toString());
		
		final Intent intent = new Intent(view.getContext(), KundeDetails.class);
		intent.putExtra(KUNDE_KEY, kunde);
		startActivity(intent);
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// inflate = fuellen
        getMenuInflater().inflate(R.menu.main, menu);
    	return true;
    }
}
