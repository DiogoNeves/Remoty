package at.mob.remoty;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class RemotyActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button pause = (Button)findViewById(R.id.button1);
        pause.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			  pressMediaKey();
			  
			  try {
				  ServerSocket server = new ServerSocket(1666);
				  server.setSoTimeout(10000 * 1000);
				  
	              // attempt to accept a connection
	              Socket client = server.accept();
	              Toast.makeText(getApplicationContext(), "YEAAAH", 1000);
	              client.close();
			  } catch (IOException e) {
				  Log.e("RMTY", e.toString());
			  }
			}
        });
    }
    
    private void pressMediaKey() {
    	Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE); 
        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
        sendOrderedBroadcast(downIntent, null);

        Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null); 
        KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
        upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
        sendOrderedBroadcast(upIntent, null);
    }
}