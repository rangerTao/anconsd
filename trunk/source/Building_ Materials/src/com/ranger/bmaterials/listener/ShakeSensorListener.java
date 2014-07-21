/**
 * 
 */
package com.ranger.bmaterials.listener;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;

/**
 * 
 * @author hcq
 * @version 2012-10-23 ����2:02:00
 */

public final class ShakeSensorListener {
	private SensorManager sensorMgr;
	private ShakeSensorEventListener shake_sensor_listener;
	private Sensor sensor;
	private IShakeSensor isl;
	private boolean flag;
	private Vibrator vibrator;

	public ShakeSensorListener(Context cx, IShakeSensor isl) {
		this.isl = isl;
		sensorMgr = (SensorManager) cx.getSystemService(Context.SENSOR_SERVICE);
		shake_sensor_listener = new ShakeSensorEventListener();
		sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		sensorMgr.registerListener(shake_sensor_listener, sensor,
				SensorManager.SENSOR_DELAY_UI);

		vibrator = (Vibrator) cx.getSystemService(Service.VIBRATOR_SERVICE);
	}

	public void onPause() {
		if (!flag) {
			sensorMgr.unregisterListener(shake_sensor_listener);
			flag = true;
		}
	}

	public void onResume() {
		if (flag) {
			sensorMgr.registerListener(shake_sensor_listener, sensor,
					SensorManager.SENSOR_DELAY_UI);
			flag = false;
		}
	}

	private class ShakeSensorEventListener implements SensorEventListener {

		private long lastUpdate;
		private float last_x = -1.0f, last_z = -1.0f, last_y = -1.0f;

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				long curTime = System.currentTimeMillis();
				long diftTime = curTime - lastUpdate;
				if (diftTime > 100) {
					lastUpdate = curTime;
					float x = event.values[0];
					float y = event.values[1];
					float z = event.values[2];
					float speed = Math
							.abs(x + y + z - last_x - last_y - last_z)
							* 100
							/ diftTime;
					if (speed > 15) {
						synchronized (isl) {
							vibrator.vibrate(500);
							isl.onShake();
						}
					}
					last_x = x;
					last_y = y;
					last_z = z;
				}
			}
		}
	}

	public interface IShakeSensor {
		public void onShake();
	}
}
