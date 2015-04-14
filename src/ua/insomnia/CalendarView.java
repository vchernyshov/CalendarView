package ua.insomnia;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ua.insomnia.calendarview.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CalendarView extends LinearLayout implements OnSwipeListener {
	
	public static final String TAG = "CalendarView";

	private boolean DEBUG = true;
	private boolean swipeEnable = true;
	private GestureDetector detector;
	private OnMonthChange monthListener;

	private int colorNoCurrentMonth = getResources().getColor(
			R.color.no_current_month);
	private int colorCurrentMonthBeforCurrentDay = getResources().getColor(
			R.color.current_month_before_current_day);
	private int colorCurrentMonthAfterCurrentDay = getResources().getColor(
			R.color.current_month_after_current_day);
	private int colorCurrentMonthCurrentDay = getResources().getColor(
			R.color.current_month_current_day);

	private static final int CELL_IN_ROW = 7;
	private static final int ROW_COUNT = 7;

	private ArrayList<TextView> cellArray = new ArrayList<TextView>();
	private String[] dayNames = new String[CELL_IN_ROW];
	private int firstDayOfMonth;
	private int daysInCurrentMonth;
	private int daysInPreviousMonth;

	private int currentDay;

	private Calendar current = Calendar.getInstance();
	private Calendar now = Calendar.getInstance();
	private Calendar next;
	private Calendar previous;

	public CalendarView(Context context) {
		super(context);
		initCalendars();
		initCalendarView();
		initTitles();
		init();
	}

	@SuppressLint("NewApi")
	public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initCalendars();
		initCalendarView();
		initTitles();
		init();
	}

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initCalendars();
		initCalendarView();
		initTitles();
		init();
	}

	private void init() {
		getFirstDayOfMonth();
		getDaysInCurrentMonth();
		getDaysInPreviousMonth();
		getCurrentDay();

		// init indexes for previous month
		int startIndex = CELL_IN_ROW;
		int endIndex = startIndex + getRealFirstDayOfMonth();
		int daysCount = daysInPreviousMonth - getRealFirstDayOfMonth() + 1;

		if (DEBUG)
			Log.d(TAG, "prev\nstartIndex = " + startIndex + " endIndex = "
					+ endIndex + " daysCount = " + daysCount);

		for (int i = startIndex; i < endIndex; i++) {
			TextView tw = cellArray.get(i);
			tw.setText(String.valueOf(daysCount));
			tw.setTextColor(colorNoCurrentMonth);
			int month = previous.get(Calendar.MONTH);
			int year = previous.get(Calendar.YEAR);
			setTagTo(tw, daysCount, month, year);
			daysCount++;
		}

		// recount indexes for current month
		startIndex = CELL_IN_ROW + getRealFirstDayOfMonth();
		endIndex = startIndex + daysInCurrentMonth;
		daysCount = 1;

		if (DEBUG)
			Log.d(TAG, "current\nstartIndex = " + startIndex + " endIndex = "
					+ endIndex + " daysCount = " + daysCount);

		for (int i = startIndex; i < endIndex; i++) {
			TextView tw = cellArray.get(i);
			tw.setText(String.valueOf(daysCount));

			int localColor;

			if (isBeforeNow())
				localColor = colorCurrentMonthBeforCurrentDay;
			else if (isAfterNow())
				localColor = colorCurrentMonthAfterCurrentDay;
			else {
				if (daysCount < currentDay)
					localColor = colorCurrentMonthBeforCurrentDay;
				else if (daysCount == currentDay)
					localColor = colorCurrentMonthCurrentDay;
				else
					localColor = colorCurrentMonthAfterCurrentDay;
			}

			tw.setTextColor(localColor);
			int month = current.get(Calendar.MONTH);
			int year = current.get(Calendar.YEAR);
			setTagTo(tw, daysCount, month, year);
			daysCount++;
		}

		// recount indexes for next month
		startIndex = CELL_IN_ROW + getRealFirstDayOfMonth()
				+ daysInCurrentMonth;
		endIndex = cellArray.size();
		daysCount = 1;

		if (DEBUG)
			Log.d(TAG, "next\nstartIndex = " + startIndex + " endIndex = "
					+ endIndex + " daysCount = " + daysCount);

		for (int i = startIndex; i < endIndex; i++) {
			TextView tw = cellArray.get(i);
			tw.setText(String.valueOf(daysCount));
			tw.setTextColor(colorNoCurrentMonth);
			int month = next.get(Calendar.MONTH);
			int year = next.get(Calendar.YEAR);
			setTagTo(tw, daysCount, month, year);
			daysCount++;
		}

	}

	public void setOnDayClickListener(OnClickListener l) {
		for (int i = 0; i < cellArray.size(); i++) {
			TextView tw = cellArray.get(i);
			// enable click event only for day cell
			if (i >= CELL_IN_ROW) {
				tw.setClickable(true);
				tw.setOnClickListener(l);
			}
		}
	}

	private void setTagTo(View view, int day, int month, int year) {
		Calendar c = Calendar.getInstance();
		String tag = null;
		String format = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		c.set(year, month, day);
		tag = sdf.format(c.getTime());
		view.setTag(tag);
	}

	private void getCurrentDay() {
		currentDay = current.get(Calendar.DATE);
	}

	private void getFirstDayOfMonth() {
		int day = current.get(Calendar.DAY_OF_MONTH);
		current.set(Calendar.DAY_OF_MONTH, 1);
		firstDayOfMonth = current.get(Calendar.DAY_OF_WEEK);
		current.set(Calendar.DAY_OF_MONTH, day);
	}

	private void getDaysInCurrentMonth() {
		daysInCurrentMonth = current.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	private void getDaysInPreviousMonth() {
		daysInPreviousMonth = previous.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	private int getRealFirstDayOfMonth() {
		// Sunday
		if (firstDayOfMonth == 1)
			return 6;
		// Monday
		if (firstDayOfMonth == 2)
			return 0;
		// Tuesday
		if (firstDayOfMonth == 3)
			return 1;
		// Wednesday
		if (firstDayOfMonth == 4)
			return 2;
		// Thursday
		if (firstDayOfMonth == 5)
			return 3;
		// Friday
		if (firstDayOfMonth == 6)
			return 4;
		// Saturday
		if (firstDayOfMonth == 7)
			return 5;
		return 0;
	}

	private void initTitles() {
		DateFormatSymbols dsf = new DateFormatSymbols(Locale.getDefault());
		String[] symbols = dsf.getShortWeekdays();

		for (int i = 0; i < CELL_IN_ROW - 1; i++) {
			dayNames[i] = symbols[i + 2];
		}
		dayNames[6] = symbols[1];

		for (int i = 0; i < CELL_IN_ROW; i++) {
			TextView tw = cellArray.get(i);
			tw.setText(dayNames[i]);
		}
	}

	private void initCalendars() {

		previous = (Calendar) current.clone();
		next = (Calendar) current.clone();

		int month = current.get(Calendar.MONTH);

		if (month == Calendar.JANUARY) {
			previous.add(Calendar.YEAR, -1);
			previous.set(Calendar.MONTH, Calendar.DECEMBER);
		} else
			previous.add(Calendar.MONTH, -1);

		if (month == Calendar.DECEMBER) {
			next.add(Calendar.YEAR, 1);
			next.set(Calendar.MONTH, Calendar.JANUARY);
		} else
			next.add(Calendar.MONTH, 1);
	}

	@SuppressLint("NewApi")
	private void initCalendarView() {

		this.setOrientation(LinearLayout.VERTICAL);

		SwipeDetector sd = new SwipeDetector(this);
		detector = new GestureDetector(getContext(), sd);

		for (int i = 0; i < ROW_COUNT; i++) {
			LinearLayout row = createRow();
			this.addView(row, i);
		}

		for (int i = 0; i < this.getChildCount(); i++) {
			LinearLayout row = (LinearLayout) this.getChildAt(i);
			for (int j = 0; j < row.getChildCount(); j++) {
				TextView tw = (TextView) row.getChildAt(j);
				tw.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View arg0, MotionEvent event) {
						detector.onTouchEvent(event);
						return false;
					}
				});

				cellArray.add(tw);
			}
		}
	}

	private LinearLayout createRow() {
		LinearLayout row = (LinearLayout) View.inflate(getContext(),
				R.layout.row, null);
		return row;
	}
	
	private boolean isBeforeNow() {
		if (current.before(now))
			return true;
		return false;
	}
	
	private boolean isAfterNow(){
		if (current.after(now))
			return true;
		return false;
	}

	private boolean isNow() {
		if (current.compareTo(now) == 0)
			return true;
		else
			return false;
	}

	public void setCurrentCalendar(Calendar calendar) {
		current = calendar;
		initCalendars();
		init();
	}

	public Calendar getCurrentCalendar() {
		return current;
	}

	public Calendar getPrevioseCalendar() {
		return previous;
	}

	public Calendar getNextCalendar() {
		return next;
	}

	@SuppressLint("NewApi")
	public String getCurrentMonth() {
		String format = "LLLL";
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String month = sdf.format(current.getTime());
		return month.toUpperCase(Locale.getDefault());
	}

	@Override
	public void onSwipeLeft() {
		
		if (swipeEnable) {
			if (DEBUG)
				Log.d(TAG, "onSwipeLeft. month - " + getCurrentMonth());

			setCurrentCalendar(next);

			if (monthListener != null)
				monthListener.onChange(getCurrentMonth());
		}
	}

	@Override
	public void onSwipeRight() {
		
		if (swipeEnable) {
			if (DEBUG)
				Log.d(TAG, "onSwipeRight. month - " + getCurrentMonth());

			setCurrentCalendar(previous);

			if (monthListener != null)
				monthListener.onChange(getCurrentMonth());
		}
	}

	public void setOnMonthListener(OnMonthChange monthListener) {
		this.monthListener = monthListener;
	}

}
