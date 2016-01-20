/**
 * Copyright 2016 Stuart Kent
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
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
            case R.id.map_view_activity_menu_entry:
                startActivity(new Intent(this, MapViewActivity.class));
                return true;
            case R.id.map_fragment_activity_menu_entry:
                startActivity(new Intent(this, MapFragmentActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
