package com.example.message;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.message.domain.Message;

public class MainActivity extends Activity {
	private ListView lv;
	private Message message = new Message();
	private List<Message> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		lv = (ListView) findViewById(R.id.lv);
		list = new ArrayList<Message>();
		new Thread() {
			public void run() {

				File file = new File(Environment.getExternalStorageDirectory()
						.toString() + "/backSms.xml");
				try {
					FileInputStream fis = new FileInputStream(file);
					XmlPullParser parser = Xml.newPullParser();
					parser.setInput(fis, "utf-8");
					int type = parser.getEventType();
					while (type != XmlPullParser.END_DOCUMENT) {
						if (type == XmlPullParser.START_TAG) {
							if ("address".equals(parser.getName())) {
								message.setAddress(parser.nextText());
							}
							if ("date".equals(parser.getName())) {
								message.setData(Integer.valueOf(parser
										.nextText()));
							}
							if ("type".equals(parser.getName())) {
								message.setType(Integer.valueOf(parser
										.nextText()));
							}
							if ("body".equals(parser.getName())) {
								message.setBody(parser.nextText());
							}
						} else if (type == XmlPullParser.END_TAG) {
							list.add(message);
							message = new Message();
						}
						type = parser.next();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				lv.setAdapter(new BaseAdapter() {
					@Override
					public int getCount() {
						return list.size();
					}

					@Override
					public View getView(int position, View convertView,
							ViewGroup parent) {
						View view = View.inflate(MainActivity.this,
								R.layout.item, null);
						TextView tv_address = (TextView) view
								.findViewById(R.id.tv_address);
						TextView tv_data = (TextView) view
								.findViewById(R.id.tv_data);
						TextView tv_body = (TextView) view
								.findViewById(R.id.tv_body);
						tv_address.setText(list.get(position).getAddress());
						tv_data.setText(time(list.get(position).getData()));
						tv_body.setText(list.get(position).getBody());
						return view;
					}

					@Override
					public Object getItem(int position) {
						return null;
					}

					@Override
					public long getItemId(int position) {
						return 0;
					}
				});
			};
		}.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_settings) {
			onBackPressed();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public static String time(long data) {
		Date d = new Date(data);
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
		return sdf.format(d);

	}
}
