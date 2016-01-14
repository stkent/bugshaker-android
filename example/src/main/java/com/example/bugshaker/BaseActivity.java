package com.example.bugshaker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

abstract class BaseActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secured_activity_menu_entry:
                startActivity(new Intent(this, SecuredActivity.class));
                return true;
            case R.id.unsecured_activity_menu_entry:
                startActivity(new Intent(this, UnsecuredActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
