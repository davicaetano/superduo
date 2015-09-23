package barqsoft.footballscores.widget;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.data.DatabaseContract;

/**
 * Created by davi on 9/7/15.
 */
public class WidgetService extends RemoteViewsService {
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;
            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if(data != null){
                    data.close();
                }
                int i = 1;
                Date fragmentdate = new Date(System.currentTimeMillis()+((i-2)*86400000));
                SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
                String[] arg = new String[1];
                arg[0] = mformat.format(fragmentdate);
                ContentResolver contentResolver = getContentResolver();
                data = contentResolver.query(DatabaseContract.scores_table.buildScoreWithDate(), null, null, arg, null);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),R.layout.widget_list_item);
                String score;
                String score1 = data.getString(data.getColumnIndex(DatabaseContract.scores_table.HOME_GOALS_COL))+"";
                String score2 = data.getString(data.getColumnIndex(DatabaseContract.scores_table.AWAY_GOALS_COL))+"";
                if (score1.equals("-1") || score2.equals("-1")){
                    score = " - ";
                }else{
                    score = score1 + " - " + score2;
                }
                views.setTextViewText(R.id.home_name,data.getString(data.getColumnIndex(DatabaseContract.scores_table.HOME_COL)));
                views.setContentDescription(R.id.home_name, data.getString(data.getColumnIndex(DatabaseContract.scores_table.HOME_COL)));

                views.setTextViewText(R.id.score_textview, score);
                views.setContentDescription(R.id.score_textview, Utilies.getContentScores(Integer.parseInt(score1),Integer.parseInt(score2)));

                views.setTextViewText(R.id.away_name, data.getString(data.getColumnIndex(DatabaseContract.scores_table.AWAY_COL)));
                views.setContentDescription(R.id.away_name, data.getString(data.getColumnIndex(DatabaseContract.scores_table.AWAY_COL)));

                views.setTextColor(R.id.home_name, getResources().getColor(R.color.blue07));
                views.setTextColor(R.id.score_textview, getResources().getColor(R.color.blue07));
                views.setTextColor(R.id.away_name, getResources().getColor(R.color.blue07));

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                views.setOnClickFillInIntent(R.id.widget_list_item, intent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(data.getColumnIndex(DatabaseContract.scores_table.MATCH_ID));
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}

