package mx.ken.letitgo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;


public class MainActivity extends ActionBarActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private double gravity[];
    private double linear_acceleration[];
    private long currentTime;
    private long prevTime;
    private long Dtime;
    private double maxVel;
    private double maxHeight;
    private double velocidad;
    private double deltaVel;
    private double Height;
    private TextView tvAltura;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvAltura = (TextView) findViewById(R.id.tv_altura);
        Button btnReset = (Button) findViewById(R.id.button);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maxHeight = 0;
                velocidad = 0;
                currentTime = 0;
                prevTime = 0;
                maxVel = 0;
                tvAltura.setText("Height : 0");
            }
        });
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        gravity = new double[3];
        linear_acceleration = new double[3];

        maxHeight = 0;
        velocidad = 0;
        currentTime = 0;
        prevTime = 0;
        maxVel = 0;

        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float alpha = 0.8f;
        currentTime = getCurrentTime();
        //Log.i("myLog", "tiempo :" + (currentTime - prevTime));
        if (prevTime == 0) {
            prevTime = currentTime;
            return;

        }
        Dtime = currentTime - prevTime;
        //Log.i("myLog", "Dtime : " + Dtime);
        prevTime = currentTime;


        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
//        Log.i("myLog", "gravedad 0 :" + gravity[0]);
//        Log.i("myLog", "gravedad 1 :" + gravity[1]);
//        Log.i("myLog", "gravedad 2 :" + gravity[2]);

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = Math.pow(event.values[0] - gravity[0], 2);
        linear_acceleration[1] = Math.pow(event.values[1] - gravity[1], 2);
        linear_acceleration[2] = Math.pow(event.values[2] - gravity[2], 2);

        //linear_acceleration[0] = Math.pow(2, event.values[0]);
        //linear_acceleration[1] = Math.pow(2, event.values[1]);
        //linear_acceleration[2] = Math.pow(2, event.values[2]);

        double total_acceleration = linear_acceleration[0] + linear_acceleration[1] + linear_acceleration[2];
        total_acceleration = Math.sqrt(total_acceleration);
        if (total_acceleration < 2) {
            Height = 0;
            velocidad = 0;
            currentTime = 0;
            prevTime = 0;
            maxVel = 0;
            return;
        }

        deltaVel = total_acceleration * Dtime / 1000;
        velocidad = velocidad + deltaVel;
        Log.i("myLog", "aceleracion: " + total_acceleration);
        Log.i("myLog", "velocidad : " + velocidad);
        Log.i("myLog", "velocidad max anterio : " + maxVel);
        if (maxVel < velocidad) {
            Log.i("myLog", "entro al if");
            maxVel = velocidad;
            Height = Math.pow(maxVel, 2) / (2 * 9.7);
            if (maxHeight < Height)
                maxHeight = Height;
        }


        //Log.i("myLog", "velocidad : " + velocidad);
        Log.i("myLog", "altura max : " + maxHeight);
        Log.i("myLog", "velocidad max nueva : " + maxVel);
        Log.i("myLog", "..................................");
        tvAltura.setText("Height : " + maxHeight);


//        Log.i("myLog", "aceleracion lineal 0 :" + linear_acceleration[0]);
//        Log.i("myLog", "aceleracion lineal 1 :" + linear_acceleration[1]);
//        Log.i("myLog", "aceleracion lineal 2 :" + linear_acceleration[2]);
//        if (total_acceleration > 4)
        //Log.i("myLog", "total acceleration :" + total_acceleration);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public long getCurrentTime() {

        Date date = new Date();
        return date.getTime();

    }
}
