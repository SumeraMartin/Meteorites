package com.sumera.meteorites;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sumera.meteorites.model.Meteorite;
import com.sumera.meteorites.model.MeteoritesRequest;

import java.util.List;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    public Action1<List<Meteorite>> m_onAction = new Action1<List<Meteorite>>() {
        @Override
        public void call(List<Meteorite> meteorites) {
            Log.d("SUMERA", "" + meteorites.size());
        }
    };

    public Action1<Throwable> m_onError = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            Log.d("SUMERA", "FAIL");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MeteoritesRequest.performAsyncRequest(m_onAction, m_onError);
    }
}
