package com.iut.pi.emploitemps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class ResultActivity extends Activity {
    private static final String ALIAS = "alias";
    private static final String CODE_GROUPE = "codeGroupe";
    private static final String CODE_SEANCE = "codeSeance";
    private static final String DATE_SEANCE = "dateSeance";
    private static final String DUREE_SEANCE = "dureeSeance";
    private static final String HEURE_SEANCE = "heureSeance";
    private static final String MODULE_NOM = "moduleNom";
    private static final String NOM_GROUPE = "nomGroupe";
    private static final String NOM_PROF = "nomProf";
    private Bundle extras;
    private ImageView mPreviousMonth, mNextMonth;
    private Button mButton;
    private GridView mGrid;
    private GridCellAdapter adapter;
    private int month, year;
    private String json, jsonStr;
    private URL url;
    HashMap<String, String> contact = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mPreviousMonth = (ImageView) findViewById(R.id.prevMonth);
        mNextMonth = (ImageView) findViewById(R.id.nextMonth);
        mButton = (Button) findViewById(R.id.selected);
        mGrid = (GridView) findViewById(R.id.calendar);

        extras = getIntent().getExtras();

        Calendar calendar = Calendar.getInstance();
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);

        adapter = new GridCellAdapter(getApplicationContext(), R.id.calendar_day_gridcell, month, year);
        adapter.notifyDataSetChanged();
        mGrid.setAdapter(adapter);

        json = extras.getString("JSON");

        try {
            url = new URL("http://agile.pierrebourgeois.fr:8080/v1/cours/" + extras.getString("Code"));
            new JSONParser().execute(url);
            boolean exit = false;
            while (!exit) {
                if (json != null) {
                    exit = true;
                }
            }

        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

    }

    public void getList(View view) {
        Intent intent = new Intent(getApplicationContext(), CoursActivity.class);
        extras = getIntent().getExtras();
        intent.putExtras(extras);
        intent.putExtra("JSON2", json);
        intent.putExtra("JSON3", jsonStr);
        intent.putExtra("Map", contact);
        startActivity(intent);
    }

    public class JSONParser extends AsyncTask<URL, Void, Void> {
        InputStream is;

        @Override
        protected Void doInBackground(URL... request) {
            try {
                Gson gson = new GsonBuilder().create();
                Type type = new TypeToken<ArrayList<Cours>>() {
                }.getType();
                getJSONFromUrl(request[0]);
                List<Cours> cours = gson.fromJson(jsonStr, type);

                for (Cours c : cours) {
                    // adding each child node to HashMap key => value
                    contact.put(ALIAS, c.getAlias());
                    contact.put(CODE_GROUPE, c.getCodeGroupe());
                    contact.put(CODE_SEANCE, c.getCodeSeance());
                    contact.put(DATE_SEANCE, c.getDateSeance());
                    contact.put(DUREE_SEANCE, c.getDureeSeance());
                    if (c.getHeureSeance().length() > 3) {
                        String lel = "";
                        lel = c.getHeureSeance().substring(0, 2) + "h" + c.getHeureSeance().substring(2, c.getHeureSeance().length());
                        contact.put(HEURE_SEANCE, lel);
                    } else {
                        String lel = "";
                        lel = c.getHeureSeance().substring(0, 1) + "h" + c.getHeureSeance().substring(1, c.getHeureSeance().length());
                        contact.put(HEURE_SEANCE, lel);
                    }
                    contact.put(MODULE_NOM, c.getModuleNom());
                    contact.put(NOM_GROUPE, c.getNomGroupe());
                    contact.put(NOM_PROF, c.getNomProf());
                }
            } catch (Exception e) {
                Log.e("Exception", e.toString());
            }
            return null;
        }

        public String getJSONFromUrl(URL request) {
            // Making HTTP request
            try {
                HttpURLConnection connect = (HttpURLConnection) request.openConnection();
                connect.setRequestMethod("GET");

                is = new BufferedInputStream(connect.getInputStream());
                String response = org.apache.commons.io.IOUtils.toString(is, "UTF-8");
                ResultActivity.this.json = response;
                return response;
            } catch (Exception e) {
                Log.e("Error", e.toString());
            }
            return "";
        }

    }

    // Inner Class
    public class GridCellAdapter extends BaseAdapter implements View.OnClickListener {
        private static final String tag =
                "GridCellAdapter";
        private final Context _context;

        private final List<String> list;
        private static final int DAY_OFFSET = 1;
        private final String[] weekdays = new String[]{
                "Sun", "Mon", "Tue",
                "Wed", "Thu", "Fri", "Sat"
        };
        private String[] months = {
                "January", "February", "March",
                "April", "May", "June", "July", "August", "September",
                "October", "November", "December"
        };
        private int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30,
                31, 30, 31};
        private int daysInMonth;
        private int currentDayOfMonth;
        private int currentWeekDay;
        private Button gridcell;
        private TextView num_events_per_day;
        private HashMap<String, Integer> eventsPerMonthMap = new HashMap<>();
        private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd - MM - yyyy");

        // Days in Current Month
        public GridCellAdapter(Context context, int textViewResourceId,
                               int month, int year) {
            super();
            this._context = context;
            this.list = new ArrayList<String>();
            Log.d(tag, " ==>Passed in Date FOR Month:" + month + ""
                    + "Year:" + year);
            Calendar calendar = Calendar.getInstance();
            setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
            setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
            Log.d(tag, "New Calendar:=" + calendar.getTime().toString());
            Log.d(tag, "CurrentDayOfWeek:" + getCurrentWeekDay());
            Log.d(tag, "CurrentDayOfMonth:" + getCurrentDayOfMonth());

            // Print Month
            printMonth(month, year);

            // Find Number of Events
            eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
        }

        private String getMonthAsString(int i) {
            return months[i];
        }

        private String getWeekDayAsString(int i) {
            return weekdays[i];
        }

        private int getNumberOfDaysOfMonth(int i) {
            return daysOfMonth[i];
        }

        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        /**
         * Prints Month
         *
         * @param mm
         * @param yy
         */
        private void printMonth(int mm, int yy) {
            Log.d(tag, " ==>printMonth: mm : " + mm + " +  yy:" + yy);
            int trailingSpaces = 0;
            int daysInPrevMonth = 0;
            int prevMonth = 0;
            int prevYear = 0;
            int nextMonth = 0;
            int nextYear = 0;

            int currentMonth = mm - 1;
            String currentMonthName = getMonthAsString(currentMonth);
            daysInMonth = getNumberOfDaysOfMonth(currentMonth);

            Log.d(tag, "Current Month:" + currentMonthName + "having"
                    + daysInMonth + "days.");

            GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);
            Log.d(tag, "Gregorian Calendar:= " + cal.getTime().toString());

            if (currentMonth == 11) {
                prevMonth = currentMonth - 1;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                nextMonth = 0;
                prevYear = yy;
                nextYear = yy + 1;
                Log.d(tag, " *->PrevYear: " + prevYear + "PrevMonth: " + prevMonth + "NextMonth : " + nextMonth + "NextYear : " + nextYear);
            } else if (currentMonth == 0) {
                prevMonth = 11;
                prevYear = yy - 1;
                nextYear = yy;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                nextMonth = 1;
                Log.d(tag, " **–>PrevYear: " + prevYear + "PrevMonth : "
                        + prevMonth + " NextMonth : " + nextMonth
                        + " NextYear : " + nextYear);
            } else {
                prevMonth = currentMonth - 1;
                nextMonth = currentMonth + 1;
                nextYear = yy;
                prevYear = yy;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                Log.d(tag, " ***—>PrevYear: " + prevYear + " PrevMonth : "
                        + prevMonth + " NextMonth : " + nextMonth
                        + " NextYear : " + nextYear);
            }

            int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
            trailingSpaces = currentWeekDay;

            Log.d(tag, "Week Day : " + currentWeekDay + " is "
                    + getWeekDayAsString(currentWeekDay));
            Log.d(tag, "No.Trailing space to Add : " + trailingSpaces);
            Log.d(tag, "No.of Days in Previous Month : " + daysInPrevMonth);

            if (cal.isLeapYear(cal.get(Calendar.YEAR)))
                if (mm == 2)
                    ++daysInMonth;
                else if (mm == 3)
                    ++daysInPrevMonth;

            // Trailing Month days
            for (int i = 0; i < trailingSpaces; i++) {
                Log.d(tag, "PREV MONTH : = " + prevMonth + "=>" + getMonthAsString(prevMonth) + " "
                        + String.valueOf((daysInPrevMonth
                        - trailingSpaces + DAY_OFFSET)
                        + i));
                list.add(String
                        .valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i)
                        + "-GREY" + "-" + getMonthAsString(prevMonth) + "-" + prevYear);
            }

            // Current Month Days
            for (int i = 1; i <= daysInMonth; i++) {
                Log.d(currentMonthName, String.valueOf(i) + " "
                        + getMonthAsString(currentMonth) + "" + yy);
                if (i == getCurrentDayOfMonth()) {
                    list.add(String.valueOf(i) + " - BLUE" + "-"
                            + getMonthAsString(currentMonth) + "-" + yy);
                } else {
                    list.add(String.valueOf(i) + " - WHITE" + "-"
                            + getMonthAsString(currentMonth) + "-" + yy);
                }
            }

            // Leading Month days
            for (int i = 0; i < list.size() % 7; i++) {
                Log.d(tag, "NEXT MONTH : = " + getMonthAsString(nextMonth));
                list.add(String.valueOf(i + 1) + " - GREY" + "-"
                        + getMonthAsString(nextMonth) + "-" + nextYear);
            }
        }

        /**
         * NOTE: YOU NEED TO IMPLEMENT THIS PART Given the YEAR, MONTH, retrieve
         * ALL entries from a SQLite database for that month. Iterate over the
         * List of All entries, and get the dateCreated, which is converted into
         * day.
         *
         * @param year
         * @param month
         * @return
         */
        private HashMap<String, Integer> findNumberOfEventsPerMonth(int year,
                                                                    int month) {
            HashMap<String, Integer> map = new HashMap<String, Integer>();

            return map;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) _context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.screen_gridcell, parent, false);
            }

            // Get a reference to the Day gridcell
            gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
            gridcell.setOnClickListener(this);

            // ACCOUNT FOR SPACING
            Log.d(tag, "Current Day : " + getCurrentDayOfMonth());
            String[] day_color = list.get(position).split(" -");
            String theday = day_color[0];
            String themonth = day_color[0];
            String theyear = day_color[0];
            if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {
                if (eventsPerMonthMap.containsKey(theday)) {
                    num_events_per_day = (TextView) row
                            .findViewById(R.id.num_events_per_day);
                    Integer numEvents = (Integer) eventsPerMonthMap.get(theday);
                    num_events_per_day.setText(numEvents.toString());
                }
            }

            // Set the Day GridCell
            gridcell.setText(theday);
            gridcell.setTag(theday + " -" + themonth + "-" + theyear);
            Log.d(tag, "Setting GridCell " + theday + "-" + themonth + "-"
                    + theyear);

            if (day_color[0].equals("GREY")) {
                gridcell.setTextColor(getResources()
                        .getColor(R.color.lightGrey));
            }
            if (day_color[0].equals("WHITE")) {
                gridcell.setTextColor(getResources().getColor(
                        R.color.white));
            }
            if (day_color[0].equals("BLUE")) {
                gridcell.setTextColor(getResources().getColor(R.color.orange));
            }
            return row;
        }

        @Override
        public void onClick(View view) {
            String date_month_year = (String) view.getTag();
            Log.e("Selected date", date_month_year);
            mButton.setText(date_month_year);
            try {
                Date parsedDate = dateFormatter.parse(date_month_year);
                Log.d(tag, "Parsed Date : " + parsedDate.toString());

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public int getCurrentDayOfMonth() {
            return currentDayOfMonth;
        }

        private void setCurrentDayOfMonth(int currentDayOfMonth) {
            this.currentDayOfMonth = currentDayOfMonth;
        }

        public void setCurrentWeekDay(int currentWeekDay) {
            this.currentWeekDay = currentWeekDay;
        }

        public int getCurrentWeekDay() {
            return currentWeekDay;
        }
    }
}
