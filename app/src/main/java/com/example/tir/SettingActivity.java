package com.example.tir;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.net.ServerSocket;
import java.net.Socket;

public class SettingActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener{

    private static TextView tv;
    static Dialog d ;
    static Dialog dialogcolor;
    static String but;
    static int time = 10;
    static int volume = 8;
    static int spin = 10;
    static int damage = 10;
    static String color = "#FFFFFF";
    static int Set;

    private Socket tempClientSocket;
    private ServerSocket serverSocket;
    Thread serverThread = null;
    private Handler handler;
    public static final int SERVER_PORT = 1337;

    // имя файла настроек
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_TIMER = "time";
    public static final String APP_PREFERENCES_SPIN = "spin";
    public static final String APP_PREFERENCES_DAMAGE = "damage";
    public static final String APP_PREFERENCES_COLOR = "color";
    public static final String APP_PREFERENCES_VOLUME = "volume";
    private SharedPreferences mSettings;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        tv = (TextView) findViewById(R.id.textView);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        Button button_time = (Button) findViewById(R.id.button_time);
        Button button_volume = (Button) findViewById(R.id.button_prog);
        //Button button_damage = (Button) findViewById(R.id.button_damage);
        Button button_spin = (Button) findViewById(R.id.button_down);
        Button button_color = (Button) findViewById(R.id.button_target);
        //ImageView imageColor = (ImageView) findViewById(R.id.imageColor);

        button_time.setText(getString(R.string.buttonTime,time));
        button_volume.setText(getString(R.string.buttonProg,volume));
        //button_damage.setText(getString(R.string.buttonDamage,damage));
        button_spin.setText(getString(R.string.buttonDown,spin));
        //imageColor.setBackgroundColor(Color.parseColor(color));  // белый

        //-----------------------------------------------------------------------------
        handler = new Handler();
        //--------------------------Запуск сервера-------------------------------------
     //   this.serverThread = new Thread(new ServerThread());
     //   this.serverThread.start();
        //-----------------------------------------------------------------------------

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
      //  if (null != serverThread) {
      //      sendMessage("Disconnect");
      //      serverThread.interrupt();
      //      serverThread = null;
      //  }
    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

        Log.i("value is",""+newVal);


      //  FloatingActionButton fab = findViewById(R.id.fab);
      //  fab.setOnClickListener(new View.OnClickListener() {
      //      @Override
      //      public void onClick(View view) {
      //          Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
      //                  .setAction("Action", null).show();
      //      }
      //  });
    }

    public void showTime(View view) // обработка кнопки время, вызов окна выбора значения
    {
        Button button_time = (Button) findViewById(R.id.button_time);


        final Dialog d = new Dialog(SettingActivity.this);

        d.setContentView(R.layout.dialog);
        TextView textDialog = (TextView) d.findViewById(R.id.textViewTarget);
        textDialog.setText(R.string.dialogTime);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(30);
        np.setMinValue(3);
        np.setValue(time);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                time = np.getValue();
                button_time.setText(getString(R.string.buttonTime,time));
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    public void showVolume(View view) // обработка кнопки громкость, вызов окна выбора значения
    {
        Button button_volume = (Button) findViewById(R.id.button_prog);
        final Dialog d = new Dialog(SettingActivity.this);
        d.setContentView(R.layout.dialog);
        TextView textDialog = (TextView) d.findViewById(R.id.textViewTarget);
        textDialog.setText(R.string.dialogProg);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(10);
        np.setMinValue(0);
        np.setValue(volume);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                volume = np.getValue();
                button_volume.setText(getString(R.string.buttonProg,volume));
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

    public void showDamage(View view) // обработка кнопки громкость, вызов окна выбора значения
    {

     //   Button button_damage = (Button) findViewById(R.id.button_damage);
        final Dialog d = new Dialog(SettingActivity.this);
        d.setContentView(R.layout.dialog);
        TextView textDialog = (TextView) d.findViewById(R.id.textViewTarget);
        textDialog.setText(R.string.dialogDamage);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(10);
        np.setMinValue(0);
        np.setValue(damage);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                damage = np.getValue();
                //button_damage.setText(getString(R.string.buttonDamage,damage));
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

    public void showSpin(View view) // обработка кнопки громкость, вызов окна выбора значения
    {
        Button button_spin = (Button) findViewById(R.id.button_down);
        final Dialog d = new Dialog(SettingActivity.this);
        d.setContentView(R.layout.dialog);
        TextView textDialog = (TextView) d.findViewById(R.id.textViewTarget);
        textDialog.setText(R.string.dialogDown);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(10);
        np.setMinValue(0);
        np.setValue(spin);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                spin = np.getValue();
                button_spin.setText(getString(R.string.buttonDown,spin));
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

    public void showColor(View view) // обработка кнопки громкость, вызов окна выбора значения
    {
        //Button button_color = (Button) findViewById(R.id.button_target);
        final Dialog d = new Dialog(SettingActivity.this);
        d.setContentView(R.layout.dialogcolor);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);

        final RadioGroup radioGroup = (RadioGroup) d.findViewById(R.id.radioGroupColor);
        final RadioButton radioButtonRed = (RadioButton) d.findViewById(R.id.radioButtonRed);
        final RadioButton radioButtonGreen = (RadioButton) d.findViewById(R.id.radioButtonGreen);
        final RadioButton radioButtonBlue = (RadioButton) d.findViewById(R.id.radioButtonBlue);
        final RadioButton radioButtonAll = (RadioButton) d.findViewById(R.id.radioButtonAll);
        radioGroup.clearCheck();
        switch (color){
            case "#DA0A28":
                radioButtonRed.setChecked(true);
                break;
            case "#3AB525":
                radioButtonGreen.setChecked(true);
                break;
            case "#072EA5":
                radioButtonBlue.setChecked(true);
                break;
            case "#FFFFFF":
                radioButtonAll.setChecked(true);
                break;
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
               // ImageView imageColor = (ImageView) findViewById(R.id.imageColor);
                switch (checkedId){
                    case R.id.radioButtonRed:
                        color = "#DA0A28";
                        //imageColor.setBackgroundColor(Color.parseColor(color));
                        break;
                    case R.id.radioButtonGreen:
                        color = "#3AB525";
                        //imageColor.setBackgroundColor(Color.parseColor(color));
                        break;
                    case R.id.radioButtonBlue:
                        color = "#072EA5";
                        //imageColor.setBackgroundColor(Color.parseColor(color));
                        break;
                    case R.id.radioButtonAll:
                        color = "#FFFFFF";
                        //imageColor.setBackgroundColor(Color.parseColor(color));
                        break;
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
       /* b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        */
        d.show();
    }




    //********************************************************************************************
    public void saveData(View view){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_TIMER, time);
        editor.putInt(APP_PREFERENCES_VOLUME, volume);
        editor.putInt(APP_PREFERENCES_DAMAGE, damage);
        editor.putInt(APP_PREFERENCES_SPIN, spin);
        editor.putString(APP_PREFERENCES_COLOR, color);
        editor.apply();
    }

    public void loadData(View view){
        if (mSettings.contains(APP_PREFERENCES_TIMER)) {
            // Получаем число из настроек
            time = mSettings.getInt(APP_PREFERENCES_TIMER, 0);
            Button button_time = (Button) findViewById(R.id.button_time);
            button_time.setText(getString(R.string.buttonTime,time));
        }

        if (mSettings.contains(APP_PREFERENCES_SPIN)) {
            spin = mSettings.getInt(APP_PREFERENCES_SPIN, 0);
            Button button_spin = (Button) findViewById(R.id.button_down);
            button_spin.setText(getString(R.string.buttonDown,spin));
        }

        if (mSettings.contains(APP_PREFERENCES_DAMAGE)) {
            damage = mSettings.getInt(APP_PREFERENCES_DAMAGE, 0);
            //Button button_damage = (Button) findViewById(R.id.button_damage);
            //button_damage.setText(getString(R.string.buttonDamage,damage));
        }

        if (mSettings.contains(APP_PREFERENCES_COLOR)) {
            color = mSettings.getString(APP_PREFERENCES_COLOR, "0");
           // ImageView imageColor = (ImageView) findViewById(R.id.imageColor);
            //imageColor.setBackgroundColor(Color.parseColor(color));
        }

        if (mSettings.contains(APP_PREFERENCES_VOLUME)) {
            volume = mSettings.getInt(APP_PREFERENCES_VOLUME, 0);
            Button button_volume = (Button) findViewById(R.id.button_prog);
            button_volume.setText(getString(R.string.buttonProg,volume));
        }

    }

    public void SendData(View view){
        //handler.sendMessage();
    }

}

