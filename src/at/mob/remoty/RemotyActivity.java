package at.mob.remoty;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
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
        
        mHandler = new Handler();
        
        mStart = (Button)findViewById(R.id.start);
        mStart.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				getStart().setEnabled(false);
				new Thread(startServer).start();
			}
        });
    }
    
    public void setStart(Button mStart) {
		this.mStart = mStart;
	}

	public Button getStart() {
		return mStart;
	}

	private Runnable startServer = new Thread() {
    	public void run() {
	    	Socket client = null;
	    	ServerSocket server = null;
	    	Scanner reader = null;
	    	
			try {
				server = new ServerSocket(3232);
				server.setSoTimeout(10 * 1000);
	
				// wait for connection
				client = server.accept();
				reader = new Scanner(client.getInputStream());
			} catch (SocketTimeoutException e) {
				// print out TIMEOUT
				Log.e(TAG, e.toString());
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			} finally {
				closeServer(server);
			}
	
			if (client != null) {
				mHandler.post(setConnected);
				
				if (reader.hasNext() && reader.next().equals("1234")) {
					String data = null;
					AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
					boolean muteState = false;
					
					while (reader.hasNext()) {
						data = reader.nextLine();
						
						if (data.equals("PP")) {
							pressMediaKey(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
						} else if (data.equals("N")) {
							pressMediaKey(KeyEvent.KEYCODE_MEDIA_NEXT);
						} else if (data.equals("P")) {
							pressMediaKey(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
						} else if (data.equals("VU")) {
							audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
						} else if (data.equals("VD")) {
							audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
						} else if (data.equals("M")) {
							muteState = !muteState;
							audioManager.setStreamMute(AudioManager.STREAM_MUSIC, muteState);
						}
					}
				}
				
				closeClient(client);
			}
			
			mHandler.post(setDisconnected);
    	}
    };
    
    private void closeClient(Socket client) {
    	try {
    		if (client != null)
    			client.close();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
    }
    
    private void closeServer(ServerSocket server) {
    	try {
    		if (server != null)
    			server.close();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
    }
    
    private Runnable setConnected = new Runnable() {
		public void run() {
			Toast.makeText(getBaseContext(), "Connected!", Toast.LENGTH_SHORT).show();
		}
	};
	
	private Runnable setDisconnected = new Runnable() {
		public void run() {
			getStart().setEnabled(true);
			Toast.makeText(getBaseContext(), "Diconnected!", Toast.LENGTH_SHORT).show();
		}
	};
	
	private void pressMediaKey(int key) {
    	Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, key); 
        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
        sendOrderedBroadcast(downIntent, null);

        Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null); 
        KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, key);
        upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
        sendOrderedBroadcast(upIntent, null);
    }
    
    private Handler mHandler = null;
    private Button mStart = null;
    
    private static String TAG = "RMTY";
}