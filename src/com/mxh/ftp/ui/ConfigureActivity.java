﻿

package com.mxh.ftp.ui;

import java.io.File;

import com.mxh.ftp.ui.R;
import com.mxh.ftp.util.Defaults;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
/**
 * 设置用户名,密码,端口号,共享目录,并保存于sharePreference中
 * @author XiangHang Mi
 *
 */
public class ConfigureActivity extends Activity implements OnClickListener {
	private EditText usernameBox;
	private EditText passwordBox;
	private EditText portNumberBox;
	private EditText chrootDirBox;
	private CheckBox wifiCheckBox;
	private CheckBox netCheckBox;
	
	private Button saveButton;
	private Button cancelButton;
	
	public final static String USERNAME = "username";
	public final static String PASSWORD = "password";
	public final static String PORTNUM = "portNum";
	public final static String CHROOTDIR = "chrootDir";
	public final static String ACCEPT_WIFI = "allowWifi";
	public final static String ACCEPT_NET = "allowNet";
	
    public ConfigureActivity() {
	}
	
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		
		SharedPreferences settings = null;
		// Inflate our UI from its XML layout description.
        setContentView(R.layout.configure_activity);
		
        saveButton = (Button) findViewById(R.id.config_save);
        saveButton.setOnClickListener(this);
        cancelButton = (Button) findViewById(R.id.config_cancel);
        cancelButton.setOnClickListener(this);
        
        usernameBox = (EditText) findViewById(R.id.config_username);
        passwordBox = (EditText) findViewById(R.id.config_password);
        portNumberBox = (EditText) findViewById(R.id.config_portnum);
        chrootDirBox = (EditText) findViewById(R.id.config_chroot);
        wifiCheckBox = (CheckBox) findViewById(R.id.config_wifi_checkbox);
        netCheckBox = (CheckBox) findViewById(R.id.config_net_checkbox);
        
		settings = getSharedPreferences(Defaults.getSettingsName(),
				Defaults.getSettingsMode());
		
		String username = settings.getString(USERNAME, "");
		String password = settings.getString(PASSWORD, "");
		int portNumber = settings.getInt(PORTNUM, Defaults.getPortNumber());
		String chroot = settings.getString(CHROOTDIR, Defaults.chrootDir);
		boolean acceptNet = settings.getBoolean(ACCEPT_NET, Defaults.acceptNet);
		boolean acceptWifi = settings.getBoolean(ACCEPT_WIFI, Defaults.acceptWifi);
		
		// The String named chroot holds the default chroot directory. If the
		// directory doesn't actually exist in the file system, then use
		// the root directory instead.
		File chrootTest = new File(chroot); 
		if(!chrootTest.isDirectory() || !chrootTest.canRead()) {
			chroot = "/";
		}
		
		usernameBox.setText(username);
		passwordBox.setText(password);
		portNumberBox.setText(Integer.toString(portNumber));
		chrootDirBox.setText(chroot);
		wifiCheckBox.setChecked(acceptWifi);
		netCheckBox.setChecked(acceptNet);
		
	}
	
	protected void onStart() {
		super.onStart();
	}
	
	protected void onResume() {
		super.onResume();
	}
	
	protected void onPause() {
    	super.onPause();
    }
    
    protected void onStop() {
    	super.onStop();
    }
    
    protected void onDestroy() {
    	super.onDestroy();
    }

	public void onClick(View v) {
		// This function is called when the user clicks "save."
		
		if(v == cancelButton) {
			finish();
			return;
		}
		// Let's validate all the input fields.
		String errString = null;
		int portNum = 0;
		String username = usernameBox.getText().toString();
		String password = passwordBox.getText().toString();
		String portNumberString = portNumberBox.getText().toString();
		String chrootDir = chrootDirBox.getText().toString();
		boolean acceptWifi = wifiCheckBox.isChecked();
		boolean acceptNet = netCheckBox.isChecked();
		
		validateBlock: {
			if(!username.matches("[a-zA-Z0-9]+")) {
				errString = getString(R.string.username_validation_error);
				break validateBlock;
			}
			if (!password.matches("[a-zA-Z0-9]+")){
				errString = getString(R.string.password_validation_error); 
				break validateBlock;
			}

			try {
				portNum = Integer.parseInt(portNumberString);
			} catch (Exception e) {
				portNum = 0;
			}
			if(portNum <= 0 || portNum > 65535) {
				errString = getString(R.string.port_validation_error);
				break validateBlock;
			}
			
			if(!(new File(chrootDir).isDirectory())) {
				errString = getString(R.string.chrootDir_validation_error);
				break validateBlock;
			}
			// At least one of the wifi/net listeners must be enabled,
			// otherwise there's nothing for the server to do.
			if(!acceptNet && !acceptWifi) {
				errString = getString(R.string.at_least_one_listener);
				break validateBlock;
			}
		}
		if(errString != null) {
			AlertDialog dialog =  new AlertDialog.Builder(this).create();
        	dialog.setMessage(errString);
        	dialog.setTitle(getText(R.string.instructions_label));
        	// This whole mess just adds a do-nothing "OK" button to the dialog
        	dialog.setButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int which) {
        				return;
        		} });
        	dialog.show();
        	return;
		}
		
		// Validation was successful, save the settings object
		SharedPreferences settings = getSharedPreferences(
				Defaults.getSettingsName(), Defaults.getSettingsMode());
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putString(USERNAME, username);
		editor.putString(PASSWORD, password);
		editor.putInt(PORTNUM, portNum);
		editor.putString(CHROOTDIR, chrootDir);
		editor.putBoolean(ACCEPT_WIFI, acceptWifi );
		editor.putBoolean(ACCEPT_NET, acceptNet);
		editor.commit();
		
		finish();  // close this Activity
	}
}
