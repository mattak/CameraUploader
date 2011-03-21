package ylab.donut.camerauploader;

import android.content.*;
import android.content.res.TypedArray;
import android.preference.*;
import android.util.AttributeSet;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SeekBarPreference extends DialogPreference implements OnSeekBarChangeListener{
	private static final String LOGTAG="SeekBarPreference";
	private static final String androidns="http://schemas.android.com/apk/res/android";
	private static final int MAX_PROGRESS = 100;
	private static final int DEFAULT_PROGRESS = 50;
	private int currentProgress;
	private int oldProgress;
	private int defaultValue, max, value = 0;
	private String dialogMessage, suffixString;
	private SeekBar seekBar;
	private TextView splashText, valueText;
	private Context context;
	
	public SeekBarPreference(Context context, AttributeSet attrs){
		super(context, attrs);
		this.context = context;
		
		dialogMessage = attrs.getAttributeValue(androidns, "dialogMessage");
		suffixString = attrs.getAttributeValue(androidns,"text");
		defaultValue = attrs.getAttributeIntValue(androidns,"defaultValue", 90);
		max = attrs.getAttributeIntValue(androidns,"max", 100);
	}
	
	
	protected View onCreateDialogView(){
		LinearLayout.LayoutParams params;
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		
		// splash
		splashText = new TextView(context);
		if(dialogMessage != null){
			splashText.setText(dialogMessage);
		}
		layout.addView(splashText);
		
		valueText = new TextView(context);
		valueText.setGravity(Gravity.CENTER_HORIZONTAL);
		valueText.setTextSize(32);
		params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layout.addView(valueText, params);
		
		seekBar = new SeekBar(context);
		seekBar.setOnSeekBarChangeListener(this);
		layout.addView(seekBar, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		
		if(shouldPersist()){
			value = getPersistedInt(defaultValue);
		}
		
		seekBar.setMax(max);
		seekBar.setProgress(value);
		return layout;
	}
	
	protected void onBindDialogView(View v){
		super.onBindDialogView(v);
		seekBar.setMax(max);
		seekBar.setProgress(value);
	}
	
	protected void onSetInitialValue(boolean restore, Object defaultValue){
		super.onSetInitialValue(restore, defaultValue);
		if(restore){
			//android.util.Log.w(LOGTAG,"defaultValue setted(r):"+defaultValue.toString());
			value = shouldPersist() ? getPersistedInt(this.defaultValue) : this.defaultValue;
		}else{
			//android.util.Log.w(LOGTAG,"defaultValue setted :"+defaultValue.toString());
			value = (Integer)defaultValue;
		}
	}
	
	// Tracking
	//--------------------
	
	public void onProgressChanged(SeekBar seekbar, int value, boolean fromTouch){
		String t = String.valueOf(value);
		valueText.setText(suffixString == null? t :t.concat(suffixString));
		this.value = value;
		if(shouldPersist()){
			persistInt(value);
		}
		callChangeListener(new Integer(value));
	}
	public void onStartTrackingTouch(SeekBar seekbar){}
	public void onStopTrackingTouch(SeekBar seekbar){}
	
	// Public Util
	//--------------------
	public void setMax(int max){ this.max = max;}
	public int getMax(){return this.max;}
	
	public void setProgress(int progress){
		value = progress;
		if( seekBar != null ){
			seekBar.setProgress(progress);
		}
	}
	public int getProgress(){return value;}
}
