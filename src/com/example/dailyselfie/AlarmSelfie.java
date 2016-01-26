package com.example.dailyselfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmSelfie extends BroadcastReceiver {

	private final String SOMEACTION = "com.example.dailyselfie.takephoto.alarm.ACTION";

	public AlarmSelfie() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// generateNotification(context);
		String action = intent.getAction();
		if (SOMEACTION.equals(action)) {
			generateNotification(context);
		}
	}

	private void generateNotification(Context context) {

		int icon = R.drawable.ic_menu_camera;
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		String title = context.getString(R.string.app_name);
		String subTitle = "Time for another Selfie";

		// The Intent to be used when the user clicks on the Notification View
		Intent notificationIntent = new Intent(context, MainActivity.class);
		// The PendingIntent that wraps the underlying Intent
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		Notification notification = new Notification.Builder(context)
				.setContentTitle(title).setContentText(subTitle)
				.setSmallIcon(icon).setContentIntent(intent).build();

		// play the default sound for your notification
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);
	}
}