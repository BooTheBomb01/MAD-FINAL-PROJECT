package edu.lewisu.cs.spreitzermatt.matthewspreitzercalendar;

import top.defaults.colorpicker.ColorPickerPopup;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class OptionsActivity extends AppCompatActivity {

    private static final String TAG = "OptionsActivity";
    private Button mSetColorButton;
    private Button mPickColorButton;
    private Button mSetTimeFormatButton;
    private TextView mTimeFormat;

    private View mColorPreview;
    int whatTime;
    private DetailActivity detailActivity = new DetailActivity();
    private int mDefaultColor;
    boolean whatFormat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        String mString = mPrefs.getString("timeformat", "false");





        mPickColorButton = findViewById(R.id.pick_color_button);
        mSetColorButton = findViewById(R.id.set_color_button);

        mSetTimeFormatButton = findViewById(R.id.time_format_button);
        mTimeFormat = findViewById(R.id.timeFormat);

        mColorPreview = findViewById(R.id.preview_selected_color);

        mDefaultColor = 0;

        if(mString.equals("true")){
            whatFormat = true;
            whatTime = 0;
            mTimeFormat.setText(R.string.timeFormat24);
        } else {
            mTimeFormat.setText(R.string.timeFormat12);
            whatFormat = false;
            whatTime = 1;
        }

        mPickColorButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        new ColorPickerPopup.Builder(   OptionsActivity.this).initialColor(
                                Color.RED).enableBrightness(true).enableAlpha(true).okTitle("Select").cancelTitle("Cancel")
                                .showIndicator(true).showValue(true).build()
                                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                                            @Override
                                            public void
                                            onColorPicked(int color) {
                                                mDefaultColor = color;
                                                mColorPreview.setBackgroundColor(mDefaultColor);
                                            }
                                        });
                    }
                });

                mSetColorButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, String.valueOf(mDefaultColor));

                    }
                });
        mSetTimeFormatButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(final View v) {
                SharedPreferences.Editor mEditor = mPrefs.edit();

                if (whatTime == 0){
                    mTimeFormat.setText(R.string.timeFormat24);
                    whatTime = whatTime +1;
                    whatFormat = true;
                    detailActivity.is24Hour=true;
                    mEditor.putString("timeformat", String.valueOf(whatFormat)).commit();
                } else {
                    mTimeFormat.setText(R.string.timeFormat12);
                    whatTime = whatTime -1;
                    whatFormat = false;
                    mEditor.putString("timeformat", String.valueOf(whatFormat)).commit();
                    detailActivity.is24Hour = false;
                }
                Log.d(TAG, String.valueOf(whatTime));

            }
        });
    }
}
