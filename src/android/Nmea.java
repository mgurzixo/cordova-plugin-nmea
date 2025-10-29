package net.kyosho.nmea;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
@TargetApi(Build.VERSION_CODES.N)
public class Nmea extends CordovaPlugin implements OnNmeaMessageListener, LocationListener {

  /**
   * TAG
   */
  private final String TAG = Nmea.class.getSimpleName();

  /**
   * Action key for registering event callback.
   */
  private static final String ACTION_WATCH = "watch";

  /**
   * Action key for unregistering event callback.
   */
  private static final String ACTION_CLEAR_WATCH = "clearWatch";

  /**
   * RequestCode
   */
  private static final int REQUEST_CODE_WATCH = 100;

  /**
   * LocationManager
   */
  private LocationManager locationManager;

  /**
   * callback
   */
  private CallbackContext callback;

  /**
   * Javascript entry point.
   *
   * @param action          The action to execute.
   * @param args            The exec() arguments.
   * @param callbackContext The callback context used when calling back into
   *                        JavaScript.
   * @return result
   */
  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
    switch (action) {
      case ACTION_WATCH:
        this.watch(callbackContext);
        return true;
      case ACTION_CLEAR_WATCH:
        this.clearWatch(callbackContext);
        return true;
      default:
        callbackContext.error(String.format("Unsupported action. (action=%s)", action));
    }
    return false;
  }

  /**
   * start watching
   *
   * @param callbackContext callback
   */
  private void watch(final CallbackContext callbackContext) {
    cordova.getThreadPool().execute(() -> {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        PluginResult pluginResult = new PluginResult(
          PluginResult.Status.ERROR,
          "NMEA watch requires Android API 24 or higher"
        );
        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);
        return;
      }

      if (null != callback) {
        clearWatch();
      }

      callback = callbackContext;

      if (cordova.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
        tryStartWatch();
      } else {
        cordova.requestPermission(Nmea.this, REQUEST_CODE_WATCH, Manifest.permission.ACCESS_FINE_LOCATION);
      }
    });
  }

  /**
   * start watching
   */
  @SuppressLint("MissingPermission")
  private void watch() {
    if (null == locationManager) {
      locationManager = (LocationManager) this.cordova.getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    locationManager.addNmeaListener(this);
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
  }

  /**
   * clear watching
   *
   * @param callbackContext callback
   */
  private void clearWatch(final CallbackContext callbackContext) {
    cordova.getThreadPool().execute(() -> {
      try {
        clearWatch();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", "clearWatch");
        // Callback with result.
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);
      } catch (JSONException e) {
        Log.e(TAG, e.getMessage(), e);

        // Callback with result.
        PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);
      }
    });
  }

  /**
   * clear watching
   */
  private void clearWatch() {
    if (null != locationManager) {
      locationManager.removeNmeaListener(this);
      locationManager.removeUpdates(this);
    }

    if (null != callback) {
      callback = null;
    }
  }

  /**
   * onRequestPermissionResult
   *
   * @param requestCode  requestCode
   * @param permissions  permissions
   * @param grantResults grantResults
   * @throws JSONException JSONException
   */
  @Override
  public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults)
    throws JSONException {
    super.onRequestPermissionResult(requestCode, permissions, grantResults);

    if (callback == null) {
      return;
    }

    if (permissions == null || permissions.length <= 0) {
      // Callback with result.
      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "Permissions is null or empty.");
      pluginResult.setKeepCallback(false);
      callback.sendPluginResult(pluginResult);
      callback = null;
      return;
    }

    if (grantResults == null || grantResults.length != permissions.length) {
      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "Grant results mismatch.");
      pluginResult.setKeepCallback(false);
      callback.sendPluginResult(pluginResult);
      callback = null;
      return;
    }

    boolean hasPermissions = true;
    for (int i = 0; i < permissions.length; i++) {
      if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
        hasPermissions = false;
        break;
      }
    }

    if (!hasPermissions) {
      // Callback with result.
      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "Permissions is not allowed.");
      pluginResult.setKeepCallback(false);
      callback.sendPluginResult(pluginResult);
      callback = null;
      return;
    }

    if (requestCode == REQUEST_CODE_WATCH) {
      tryStartWatch();
    } else {
      // Callback with result.
      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "Unsupported operation.");
      pluginResult.setKeepCallback(false);
      callback.sendPluginResult(pluginResult);
      callback = null;
    }
  }

  /**
   * onDestroy
   */
  @Override
  public void onDestroy() {
    super.onDestroy();
    clearWatch();
  }

  /**
   * onNmeaMessage
   *
   * @param message   NMEA message
   * @param timestamp timestamp
   */
  @Override
  public void onNmeaMessage(String message, long timestamp) {
    if (null == callback) {
      clearWatch();
      return;
    }

    try {
      // create object
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("timestamp", timestamp);
      jsonObject.put("message", message);

      // Callback with result.
      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
      pluginResult.setKeepCallback(true);
      callback.sendPluginResult(pluginResult);
    } catch (JSONException e) {
      Log.e(TAG, e.getMessage(), e);

      // Callback with result.
      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
      pluginResult.setKeepCallback(false);
      callback.sendPluginResult(pluginResult);
      callback = null;
    }
  }

  /**
   * onLocationChanged
   *
   * @param location location
   */
  @Override
  public void onLocationChanged(Location location) {
    // Log.d(TAG, "onLocationChanged");
  }

  /**
   * onStatusChanged
   *
   * @param provider provider
   * @param status   status
   * @param extras   extras
   */
  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
    // Log.d(TAG, "onStatusChanged");
  }

  /**
   * onProviderEnabled
   *
   * @param provider provider
   */
  @Override
  public void onProviderEnabled(String provider) {
    // Log.d(TAG, "onProviderEnabled");
  }

  /**
   * onProviderDisabled
   *
   * @param provider provider
   */
  @Override
  public void onProviderDisabled(String provider) {
    // Log.d(TAG, "onProviderDisabled");
  }

  private void tryStartWatch() {
    if (callback == null) {
      return;
    }

    try {
      this.watch();

      JSONObject jsonObject = new JSONObject();
      jsonObject.put("id", "watch");
      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
      pluginResult.setKeepCallback(true);
      callback.sendPluginResult(pluginResult);
    } catch (SecurityException | JSONException ex) {
      Log.e(TAG, ex.getMessage(), ex);

      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, ex.getMessage());
      pluginResult.setKeepCallback(false);
      callback.sendPluginResult(pluginResult);
      callback = null;
    }
  }
}
