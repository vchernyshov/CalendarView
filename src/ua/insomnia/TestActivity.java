package ua.insomnia;

import java.util.Calendar;

import ua.insomnia.calendarview.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TestActivity extends Activity{

	private TextView txtTitle;
	private CalendarView calendarView;
	private Button btnNext;
	private Button btnPrev;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);

		txtTitle = (TextView) findViewById(R.id.txtTitle);
		calendarView = (CalendarView) findViewById(R.id.calendarView1);
		btnNext = (Button) findViewById(R.id.btnNext);
		btnPrev = (Button) findViewById(R.id.btnPrev);

		txtTitle.setText(calendarView.getCurrentMonth());

		btnNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Calendar c = (Calendar) calendarView.getNextCalendar().clone();
				calendarView.setCurrentCalendar(c);
				txtTitle.setText(calendarView.getCurrentMonth());
			}
		});

		btnPrev.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Calendar c = (Calendar) calendarView.getPrevioseCalendar()
						.clone();
				calendarView.setCurrentCalendar(c);
				txtTitle.setText(calendarView.getCurrentMonth());
			}
		});

		calendarView.setOnDayClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Toast.makeText(TestActivity.this, (String) view.getTag(),
						Toast.LENGTH_SHORT).show();
			}
		});
		
		calendarView.setOnMonthListener(new OnMonthChange() {
			
			@Override
			public void onChange(String month) {
				txtTitle.setText(month);
			}
		});
		
		
	}
}
