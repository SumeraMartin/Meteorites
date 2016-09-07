package com.sumera.meteorites;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.sumera.meteorites.model.Meteorite;
import com.sumera.meteorites.model.MeteoritesCache;

/**
 * Created by martin on 06/09/16.
 */

public class MeteoriteDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteorite_detail);

        setupToolbar();

        String id = getIntent().getStringExtra(MeteoriteDetailFragment.METEORITE_ID_KEY);
        Meteorite meteorite = MeteoritesCache.getMeteoriteById(id);
        initializeMeteoriteDetailFragment(meteorite);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeMeteoriteDetailFragment(Meteorite meteorite) {
        Bundle arguments = new Bundle();
        arguments.putString(MeteoriteDetailFragment.METEORITE_ID_KEY, meteorite.getId());
        MeteoriteDetailFragment fragment = new MeteoriteDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.meteorite_detail_container, fragment)
                .commit();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

}
