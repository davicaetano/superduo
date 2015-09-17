package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.R;
import barqsoft.footballscores.data.DatabaseContract;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class myFetchService extends IntentService
{
    public static final String LOG_TAG = myFetchService.class.getSimpleName();
    public myFetchService()
    {
        super(myFetchService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        getData(getResources().getString(R.string.query_code1));
        getData(getResources().getString(R.string.query_code2));

        return;
    }

    private void getData (String timeFrame)
    {
        //Creating fetch URL

        final String BASE_URL = getResources().getString(R.string.base_url); //Base URL
        final String QUERY_TIME_FRAME = getResources().getString(R.string.query_time_frame); //Time Frame parameter to determine days

        Uri fetch_build = Uri.parse(BASE_URL).buildUpon().appendQueryParameter(QUERY_TIME_FRAME, timeFrame).build();
        //Log.v(LOG_TAG, "The url we are looking at is: "+fetch_build.toString()); //log spam
        HttpURLConnection m_connection = null;
        BufferedReader reader = null;
        String JSON_data = null;
        //Opening Connection
        try {
            URL fetch = new URL(fetch_build.toString());
            m_connection = (HttpURLConnection) fetch.openConnection();
            m_connection.setRequestMethod(getResources().getString(R.string.request_method));
            m_connection.addRequestProperty(getResources().getString(R.string.auth_token_key),getString(R.string.api_key));
            m_connection.connect();

            // Read the input stream into a String
            InputStream inputStream = m_connection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            JSON_data = buffer.toString();
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG,e.getMessage());
        }
        finally {
            if(m_connection != null)
            {
                m_connection.disconnect();
            }
            if (reader != null)
            {
                try {
                    reader.close();
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG,e.getMessage());
                }
            }
        }
        try {
            if (JSON_data != null) {
                //This bit is to check if the data contains any matches. If not, we call processJson on the dummy data
                JSONArray matches = new JSONObject(JSON_data).getJSONArray(getResources().getString(R.string.json_query_FIXTURES));
                if (matches.length() == 0) {
                    //if there is no data, call the function on dummy data
                    //this is expected behavior during the off season.
                    processJSONdata(getString(R.string.dummy_data), getApplicationContext(), false);
                    return;
                }
                processJSONdata(JSON_data, getApplicationContext(), true);
            } else {
                Log.d(LOG_TAG,getResources().getString(R.string.server_error1) );
            }
        }
        catch(Exception e)
        {
            Log.e(LOG_TAG,e.getMessage());
        }
    }
    private void processJSONdata (String JSONdata,Context mContext, boolean isReal)
    {
        final String BUNDESLIGA1 = getResources().getString(R.string.json_code_BUNDESLIGA1);
        final String BUNDESLIGA2 = getResources().getString(R.string.json_code_BUNDESLIGA2);
        final String LIGUE1 = getResources().getString(R.string.json_code_LIGUE1);
        final String LIGUE2 = getResources().getString(R.string.json_code_LIGUE2);
        final String PREMIER_LEAGUE = getResources().getString(R.string.PREMIER_LEAGUE);
        final String PRIMERA_DIVISION = getResources().getString(R.string.json_code_PRIMERA_DIVISION);
        final String SEGUNDA_DIVISION = getResources().getString(R.string.json_code_SEGUNDA_DIVISION);
        final String SERIE_A = getResources().getString(R.string.json_code_SERIE_A);
        final String PRIMERA_LIGA = getResources().getString(R.string.json_code_PRIMERA_LIGA);
        final String Bundesliga3 = getResources().getString(R.string.json_code_Bundesliga3);
        final String EREDIVISIE = getResources().getString(R.string.json_code_EREDIVISIE);
        final String CHAMPIONS = getResources().getString(R.string.json_code_CHAMPIONS);

        final String SEASON_LINK = getResources().getString(R.string.json_query_SEASON_LINK);
        final String MATCH_LINK = getResources().getString(R.string.json_query_MATCH_LINK);
        final String FIXTURES = getResources().getString(R.string.json_query_FIXTURES);
        final String LINKS = getResources().getString(R.string.json_query_LINKS);
        final String SOCCER_SEASON = getResources().getString(R.string.json_query_SOCCER_SEASON);
        final String SELF = getResources().getString(R.string.json_query_SELF);
        final String MATCH_DATE = getResources().getString(R.string.json_query_MATCH_DATE);
        final String HOME_TEAM = getResources().getString(R.string.json_query_HOME_TEAM);
        final String AWAY_TEAM = getResources().getString(R.string.json_query_AWAY_TEAM);
        final String RESULT = getResources().getString(R.string.json_query_RESULT);
        final String HOME_GOALS = getResources().getString(R.string.json_query_HOME_GOALS);
        final String AWAY_GOALS = getResources().getString(R.string.json_query_AWAY_GOALS);
        final String MATCH_DAY = getResources().getString(R.string.json_query_MATCH_DAY);

        //Match data
        String League = null;
        String mDate = null;
        String mTime = null;
        String Home = null;
        String Away = null;
        String Home_goals = null;
        String Away_goals = null;
        String match_id = null;
        String match_day = null;


        try
        {
            JSONArray matches = new JSONObject(JSONdata).getJSONArray(FIXTURES);


            //ContentValues to be inserted
            Vector<ContentValues> values = new Vector <ContentValues> (matches.length());
            for(int i = 0;i < matches.length();i++)
            {

                JSONObject match_data = matches.getJSONObject(i);
                League = match_data.getJSONObject(LINKS).getJSONObject(SOCCER_SEASON).
                        getString(getResources().getString(R.string.href));
                League = League.replace(SEASON_LINK, "");
                if(     League.equals(PREMIER_LEAGUE)      ||
                        League.equals(SERIE_A)             ||
                        League.equals(BUNDESLIGA1)         ||
                        League.equals(BUNDESLIGA2)         ||
                        League.equals(PRIMERA_DIVISION)    ||
                        League.equals(CHAMPIONS)
                        )
                {
                    match_id = match_data.getJSONObject(LINKS).getJSONObject(SELF).getString(getResources().getString(R.string.href));
                    match_id = match_id.replace(MATCH_LINK, "");
                    if(!isReal){
                        //This if statement changes the match ID of the dummy data so that it all goes into the database
                        match_id=match_id+Integer.toString(i);
                    }

                    mDate = match_data.getString(MATCH_DATE);
                    mTime = mDate.substring(mDate.indexOf(getResources().getString(R.string.index1)) + 1,
                            mDate.indexOf(getResources().getString(R.string.index2)));
                    mDate = mDate.substring(0,mDate.indexOf(getResources().getString(R.string.index1)));

                    SimpleDateFormat match_date = new SimpleDateFormat(getResources().getString(R.string.date_format1) );
                    match_date.setTimeZone(TimeZone.getTimeZone(getResources().getString(R.string.time_zone)));
                    try {
                        Date parseddate = match_date.parse(mDate+mTime);
                        SimpleDateFormat new_date = new SimpleDateFormat(getResources().getString(R.string.date_format2) );
                        new_date.setTimeZone(TimeZone.getDefault());
                        mDate = new_date.format(parseddate);
                        mTime = mDate.substring(mDate.indexOf(":") + 1);
                        mDate = mDate.substring(0,mDate.indexOf(":"));

                        if(!isReal){
                            //This if statement changes the dummy data's date to match our current date range.
                            Date fragmentdate = new Date(System.currentTimeMillis()+((i-2)*86400000));
                            SimpleDateFormat mformat = new SimpleDateFormat(getResources().getString(R.string.date_format3) );
                            mDate=mformat.format(fragmentdate);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(LOG_TAG,e.getMessage());
                    }
                    Home = match_data.getString(HOME_TEAM);
                    Away = match_data.getString(AWAY_TEAM);
                    Home_goals = match_data.getJSONObject(RESULT).getString(HOME_GOALS);
                    Away_goals = match_data.getJSONObject(RESULT).getString(AWAY_GOALS);
                    match_day = match_data.getString(MATCH_DAY);
                    ContentValues match_values = new ContentValues();
                    match_values.put(DatabaseContract.scores_table.MATCH_ID,match_id);
                    match_values.put(DatabaseContract.scores_table.DATE_COL,mDate);
                    match_values.put(DatabaseContract.scores_table.TIME_COL,mTime);
                    match_values.put(DatabaseContract.scores_table.HOME_COL,Home);
                    match_values.put(DatabaseContract.scores_table.AWAY_COL,Away);
                    match_values.put(DatabaseContract.scores_table.HOME_GOALS_COL,Home_goals);
                    match_values.put(DatabaseContract.scores_table.AWAY_GOALS_COL,Away_goals);
                    match_values.put(DatabaseContract.scores_table.LEAGUE_COL,League);
                    match_values.put(DatabaseContract.scores_table.MATCH_DAY,match_day);

                    values.add(match_values);
                }
            }
            int inserted_data = 0;
            ContentValues[] insert_data = new ContentValues[values.size()];
            values.toArray(insert_data);
            mContext.getContentResolver().bulkInsert(DatabaseContract.BASE_CONTENT_URI,insert_data);

        }
        catch (JSONException e)
        {
            Log.e(LOG_TAG,e.getMessage());
        }

    }
}