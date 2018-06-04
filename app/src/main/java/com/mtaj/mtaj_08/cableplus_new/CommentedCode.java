package com.mtaj.mtaj_08.cableplus_new;

/**
 * Created by MTAJ-11 on 3/17/2017.
 */

public class CommentedCode {


     /* MDDialog.Builder mdalert = new MDDialog.Builder(CustomerSignatureActivity.this);
            mdalert.setTitle("Go to Settings to enable GPS");
            mdalert.setPositiveButton("SETTINGS", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                }
            });
            mdalert.setNegativeButton("CANCEL", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    URL = siteurl + "/GenerateBillReceiptForCollectionApp";

                    // GPSon(getApplicationContext());
                    //turnGpsOn(getApplicationContext());

                    //
                    // getLOcation();

                    // displayLocation();

                    Toast.makeText(CustomerSignatureActivity.this, latitude + "--" + longitude, Toast.LENGTH_SHORT).show();

                    CallVolley(URL);

                }
            });
            mdalert.setWidthMaxDp(600);
            mdalert.setShowTitle(true);
            mdalert.setShowButtons(true);
            mdalert.setBackgroundCornerRadius(5);

            MDDialog dialog = mdalert.create();
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            dialog.show();*/



    /* LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitudes = mLastLocation.getLatitude();
            double longitudes = mLastLocation.getLongitude();

            latitude=String.valueOf(latitudes);
            longitude=String.valueOf(longitudes);

            URL = siteurl + "/GenerateBillReceiptForCollectionApp";

            Toast.makeText(CustomerSignatureActivity.this, latitude + "--" + longitude, Toast.LENGTH_SHORT).show();

            CallVolley(URL);

           // Toast.makeText(CustomerSignatureActivity.this, latitudes + ", " + longitudes, Toast.LENGTH_SHORT).show();

        } else {


            AlertDialog.Builder adalert=new AlertDialog.Builder(CustomerSignatureActivity.this);
            adalert.setMessage("Go to Settings to enable GPS");
            adalert.setCancelable(true);
            adalert.setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                }
            });
            adalert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    URL = siteurl + "/GenerateBillReceiptForCollectionApp";

                    // GPSon(getApplicationContext());
                    //turnGpsOn(getApplicationContext());

                    //
                    // getLOcation();

                    // displayLocation();

                    Toast.makeText(CustomerSignatureActivity.this, latitude + "--" + longitude, Toast.LENGTH_SHORT).show();

                    CallVolley(URL);


                }
            });

            AlertDialog dialog=adalert.create();
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            dialog.show();

           *//* MDDialog.Builder mdalert = new MDDialog.Builder(CustomerSignatureActivity.this);
            mdalert.setTitle("Go to Settings to enable GPS");
            mdalert.setPositiveButton("SETTINGS", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                }
            });
            mdalert.setNegativeButton("CANCEL", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    URL = siteurl + "/GenerateBillReceiptForCollectionApp";

                    // GPSon(getApplicationContext());
                    //turnGpsOn(getApplicationContext());

                    //
                    // getLOcation();

                    // displayLocation();

                    Toast.makeText(CustomerSignatureActivity.this, latitude + "--" + longitude, Toast.LENGTH_SHORT).show();

                    CallVolley(URL);

                }
            });
            mdalert.setWidthMaxDp(600);
            mdalert.setShowTitle(true);
            mdalert.setShowButtons(true);
            mdalert.setBackgroundCornerRadius(5);

            MDDialog dialog = mdalert.create();
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            dialog.show();*//*
        }
*/
    // Toast.makeText(CustomerSignatureActivity.this, "Couldn't get the location. Make sure location is enabled", Toast.LENGTH_SHORT).show();


     /*URL = siteurl + "/withdiscount";

                    Toast.makeText(CustomerSignatureActivity.this, latitude + "--" + longitude, Toast.LENGTH_SHORT).show();

                    findViewById(R.id.llconfirm).setEnabled(false);

                    if(AllowRequest) {

                        CallVolley(URL);

                        TestCall();

                    }*/


     /*  @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;

        displayLocation();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }*/

    // URL = siteurl + "/GenerateBillReceiptForCollectionApp";

    // GPSon(getApplicationContext());
    //turnGpsOn(getApplicationContext());

    //
    // getLOcation();


    //  Toast.makeText(CustomerSignatureActivity.this, latitude + "--" + longitude, Toast.LENGTH_SHORT).show();


    // CallVolley(URL);

    // URL = siteurl + "/GenerateBillReceiptForCollectionApp";

    // GPSon(getApplicationContext());
    //turnGpsOn(getApplicationContext());

    //
    // getLOcation();


    //  Toast.makeText(CustomerSignatureActivity.this, latitude + "--" + longitude, Toast.LENGTH_SHORT).show();


    // CallVolley(URL);


   /* @SuppressWarnings("deprecation")
    public void GPSon(Context context) {
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        context.sendBroadcast(intent);

        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);
        }
    }

    private void turnGpsOn(Context context) {
        String beforeEnable = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        String newSet = String.format("%s,%s",
                beforeEnable,
                LocationManager.GPS_PROVIDER);
        try {
            Settings.Secure.putString(context.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED,
                    newSet);
        } catch (Exception e) {
        }
    }


    private void turnGPSOn() {

       *//* if(android.os.Build.VERSION.SDK_INT > 11){
            final Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
            intent.putExtra("enabled", true);
            sendBroadcast(intent);
        }*//*

        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);

        }
    }*/



}
