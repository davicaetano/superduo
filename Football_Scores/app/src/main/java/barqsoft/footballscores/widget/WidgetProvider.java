package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

/**
 * Created by davi on 9/7/15.
 */
public class WidgetProvider extends AppWidgetProvider{
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int i = 0; i<appWidgetIds.length; i++){
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
            views.setPendingIntentTemplate(R.id.list_widget,pendingIntent);

            views.setRemoteAdapter(R.id.list_widget, new Intent(context, WidgetService.class));
            views.setEmptyView(R.id.list_widget, R.id.widget_empty);

            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
        }
        super.onUpdate(context,appWidgetManager,appWidgetIds);
    }

}
