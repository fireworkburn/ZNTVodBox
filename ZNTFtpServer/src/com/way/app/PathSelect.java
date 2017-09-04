package com.way.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.way.ftp.Defaults;
import com.way.ftp.FtpServerService;
import com.way.swipeback.SwipeBackListActivity;
import com.znt.ftp.R;

public class PathSelect extends SwipeBackListActivity implements
		Button.OnClickListener {
	private List<String> items = null;
	private List<String> paths = null;
	private static String rootPath = "/";
	private static String curPath = Defaults.chrootDir;
	private TextView mPath;
	private Button mOkButton;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.pathselect);
		mPath = (TextView) findViewById(R.id.mPath);
		mOkButton = (Button) findViewById(R.id.buttonConfirm);
		mOkButton.setOnClickListener(this);
		Button buttonCancle = (Button) findViewById(R.id.buttonCancle);
		buttonCancle.setOnClickListener(this);
		curPath = PreferenceUtils.getPrefString(this,
				FtpServerService.CHOOSE_DIR_KEY, Defaults.chrootDir);
		getFileDir(curPath);
	}

	private void getFileDir(String filePath) {
		mPath.setText(filePath);
		items = new ArrayList<String>();
		paths = new ArrayList<String>();
		File f = new File(filePath);
		File[] files = f.listFiles();
		boolean root = filePath.equals(rootPath);
		if (!root) {
			items.add("back");
			paths.add(f.getParent());
			mOkButton.setEnabled(true);
		} else {
			mOkButton.setEnabled(false);
		}

		// String exs = Environment.getExternalStorageDirectory()
		// .getAbsolutePath();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.isDirectory()) {
					// if (root && !file.getPath().startsWith(exs))
					// continue;
					items.add(file.getName());
					paths.add(file.getPath());
				}
			}
		}
		setListAdapter(new PathSelectAdapter(this, items, paths));
	}

	public void onClick(View button) {
		if (!button.isEnabled())
			return;
		switch (button.getId()) {
		case R.id.buttonConfirm:
			PreferenceUtils.setPrefString(this,
					FtpServerService.CHOOSE_DIR_KEY, curPath);
			Intent data = new Intent(PathSelect.this, MainActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("file", curPath);
			data.putExtras(bundle);
			setResult(2, data);
			finish();
			break;
		case R.id.buttonCancle:
			finish();
			break;
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		curPath = paths.get(position);
		getFileDir(paths.get(position));
	}

	public class PathSelectAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private List<String> items;
		private List<String> paths;
		private Bitmap backIcon;
		private Bitmap dirIcon;

		public PathSelectAdapter(Context context, List<String> itemsIn,
				List<String> pathsIn) {
			backIcon = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.back);
			dirIcon = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.folder);
			mInflater = LayoutInflater.from(context);
			items = itemsIn;
			paths = pathsIn;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.pathselect_row, null);
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.text);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			File f = new File(paths.get(position).toString());
			if (items.get(position).toString().equals("back")) {
				holder.text.setText("back");
				holder.icon.setImageBitmap(backIcon);
			} else {
				holder.text.setText(f.getName());
				holder.icon.setImageBitmap(dirIcon);
			}
			return convertView;
		}

		private class ViewHolder {
			TextView text;
			ImageView icon;
		}

		public long getItemId(int position) {
			return position;
		}

		public Object getItem(int position) {
			return items.get(position);
		}

		public int getCount() {
			return items.size();
		}
	}

}
