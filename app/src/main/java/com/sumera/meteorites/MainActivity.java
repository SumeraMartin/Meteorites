package com.sumera.meteorites;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sumera.meteorites.model.Meteorite;
import com.sumera.meteorites.model.MeteoritesProvider;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread() {
            @Override
            public void run() {
                try {
                    List<Meteorite> meteorites = MeteoritesProvider.getMeteorites();
                    Log.d("SUMERA", "" + meteorites.size());
                } catch(MeteoritesProvider.CantProvideMeteoritesException e) {
                    Log.d("SUMERA", "fali");
                }
            }
        }.start();

    }
}
