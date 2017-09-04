/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.znt.vodbox.dialog;

import java.lang.reflect.Field;
import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.entity.PlanInfor;
import com.znt.vodbox.utils.DateUtils;

/**
 * A simple dialog containing an {@link android.widget.DatePicker}.
 *
 * <p>
 * See the <a href="{@docRoot}guide/topics/ui/controls/pickers.html">Pickers</a>
 * guide.
 * </p>
 */
public class DoubleDatePickerDialog extends AlertDialog implements OnClickListener, OnDateChangedListener {

	private static final String START_YEAR = "start_year";
	private static final String START_MONTH = "start_month";
	private static final String START_DAY = "start_day";

	private final DatePicker mDatePicker_start;
	private final OnDateSetListener mCallBack;
	private TextView tvTitle = null;
	private Calendar calendar = null;
	
	private int year = 0;
	private int monthOfYear = 0;
	private int dayOfMonth = 0;

	/**
	 * The callback used to indicate the user is done filling in the date.
	 */
	public interface OnDateSetListener {

		/**
		 * @param view
		 *            The view associated with this listener.
		 * @param year
		 *            The year that was set.
		 * @param monthOfYear
		 *            The month that was set (0-11) for compatibility with
		 *            {@link java.util.Calendar}.
		 * @param dayOfMonth
		 *            The day of the month that was set.
		 */
		void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear, int startDayOfMonth);
	}


	/**
	 * @param context
	 *            The context the dialog is to run in.
	 * @param theme
	 *            the theme to apply to this dialog
	 * @param callBack
	 *            How the parent is notified that the date is set.
	 * @param year
	 *            The initial year of the dialog.
	 * @param monthOfYear
	 *            The initial month of the dialog.
	 * @param dayOfMonth
	 *            The initial day of the dialog.
	 */
	public DoubleDatePickerDialog(Context context, int theme, OnDateSetListener callBack, String time, String title) 
	{
		super(context, theme);

		mCallBack = callBack;

		Context themeContext = getContext();
		setButton(BUTTON_POSITIVE, "确认", this);
		setButton(BUTTON_NEGATIVE, "取消", this);
		
		calendar = Calendar.getInstance();
		if(TextUtils.isEmpty(time))
		{
			year = calendar.get(Calendar.YEAR);
			monthOfYear = calendar.get(Calendar.MONTH);
			dayOfMonth = calendar.get(Calendar.DATE);
		}
		else
		{
			String[] strs = time.split("-");
			//String timeStr = DateUtils.getStringTimeHead(dateLong);
			year = Integer.parseInt(strs[0]);
			monthOfYear = Integer.parseInt(strs[1]);
			dayOfMonth = Integer.parseInt(strs[2]);
		}
		// setButton(BUTTON_POSITIVE,
		// themeContext.getText(android.R.string.date_time_done), this);
		setIcon(0);

		LayoutInflater inflater = (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.date_picker_dialog, null);
		setView(view);
		mDatePicker_start = (DatePicker) view.findViewById(R.id.datePickerStart);
		tvTitle = (TextView) view.findViewById(R.id.tv_time_picker_title);
		mDatePicker_start.init(year, monthOfYear, dayOfMonth, this);
		// updateTitle(year, monthOfYear, dayOfMonth);
		tvTitle.setText(title);
		// ���Ҫ���ص�ǰ���ڣ���ʹ�����淽����
		/*if (!isDayVisible) 
		{
			hidDay(mDatePicker_start);
		}*/
	}
	
	public void showTimeDialog(String time)
	{
		if(!TextUtils.isEmpty(time))
		{
			String[] strs = time.split("-");
			//String timeStr = DateUtils.getStringTimeHead(dateLong);
			year = Integer.parseInt(strs[0]);
			monthOfYear = Integer.parseInt(strs[1]);
			dayOfMonth = Integer.parseInt(strs[2]);
			mDatePicker_start.init(year, monthOfYear, dayOfMonth, this);
		}
		
		show();
	}
	
	/**
	 * ����DatePicker�е�������ʾ
	 * 
	 * @param mDatePicker
	 */
	private void hidDay(DatePicker mDatePicker) {
		Field[] datePickerfFields = mDatePicker.getClass().getDeclaredFields();
		for (Field datePickerField : datePickerfFields) {
			if ("mDaySpinner".equals(datePickerField.getName())) {
				datePickerField.setAccessible(true);
				Object dayPicker = new Object();
				try {
					dayPicker = datePickerField.get(mDatePicker);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
				// datePicker.getCalendarView().setVisibility(View.GONE);
				((View) dayPicker).setVisibility(View.GONE);
			}
		}
	}

	public void onClick(DialogInterface dialog, int which) {
		// Log.d(this.getClass().getSimpleName(), String.format("which:%d",
		// which));
		// ����ǡ�ȡ ��ť���򷵻أ�����ǡ�ȷ ������ť��������ִ��
		if (which == BUTTON_POSITIVE)
			tryNotifyDateSet();
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day) {
		if (view.getId() == R.id.datePickerStart)
			mDatePicker_start.init(year, month, day, this);
		// updateTitle(year, month, day);
	}

	/**
	 * ��ÿ�ʼ���ڵ�DatePicker
	 *
	 * @return The calendar view.
	 */
	public DatePicker getDatePickerStart() {
		return mDatePicker_start;
	}


	/**
	 * Sets the start date.
	 *
	 * @param year
	 *            The date year.
	 * @param monthOfYear
	 *            The date month.
	 * @param dayOfMonth
	 *            The date day of month.
	 */
	public void updateStartDate(int year, int monthOfYear, int dayOfMonth) {
		mDatePicker_start.updateDate(year, monthOfYear, dayOfMonth);
	}


	private void tryNotifyDateSet() {
		if (mCallBack != null) {
			mDatePicker_start.clearFocus();
			mCallBack.onDateSet(mDatePicker_start, mDatePicker_start.getYear(), mDatePicker_start.getMonth(),
					mDatePicker_start.getDayOfMonth());
		}
	}

	@Override
	protected void onStop() {
		// tryNotifyDateSet();
		super.onStop();
	}

	@Override
	public Bundle onSaveInstanceState() {
		Bundle state = super.onSaveInstanceState();
		state.putInt(START_YEAR, mDatePicker_start.getYear());
		state.putInt(START_MONTH, mDatePicker_start.getMonth());
		state.putInt(START_DAY, mDatePicker_start.getDayOfMonth());
		return state;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int start_year = savedInstanceState.getInt(START_YEAR);
		int start_month = savedInstanceState.getInt(START_MONTH);
		int start_day = savedInstanceState.getInt(START_DAY);
		mDatePicker_start.init(start_year, start_month, start_day, this);

	}
}
