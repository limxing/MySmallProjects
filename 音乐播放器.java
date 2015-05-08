package com.example.limxing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.limxing.domain.Music;
import com.example.limxing.service.MusicService;

public class MainActivity extends Activity implements OnClickListener {
	private File file;
	private SharedPreferences sp;
	private SharedPreferences sp1;
	private ProgressDialog pd;
	private List<Music> musicList;
	private ListView lv;
	private Map<String, ?> musicMap;
	private ImageView shang;
	private ImageView play;
	private ImageView xia;
	private TextView tv;
	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		lv = (ListView) findViewById(R.id.lv);
		shang = (ImageView) findViewById(R.id.shang);
		play = (ImageView) findViewById(R.id.play);
		xia = (ImageView) findViewById(R.id.xia);
		shang.setOnClickListener(this);
		play.setOnClickListener(this);
		xia.setOnClickListener(this);
		tv = (TextView) findViewById(R.id.tv);

		musicList = new ArrayList<Music>();
		sp = MainActivity.this.getSharedPreferences("music", 0);
		sp1 = MainActivity.this.getSharedPreferences("info", 0);
		pd = new ProgressDialog(this);
		musicMap = sp.getAll();
		if (musicMap.isEmpty()) {
			seekMusic();
			putOutList();
		} else {
			putOutList();
		}
		position = -1;
		int po = sp1.getInt("position", -1);
		String tag = sp1.getString("play", "pause");
		if (po != -1 && tag.equals("playing")) {
			play.setImageResource(R.drawable.pause);
			play.setTag("playing");
		} else if (po != -1 && tag.equals("pause")) {
			play.setImageResource(R.drawable.play);
			play.setTag("pause");
		}
		// 设置点击事件
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MainActivity.this.position = position;
				Intent intent = new Intent(MainActivity.this,
						MusicService.class);
				intent.putExtra("name", musicList.get(position).getPath());
				startService(intent);
				play.setImageResource(R.drawable.pause);
				play.setTag("playing");
				tv.setText("正在播放：" + musicList.get(position).getName());

			}
		});

	}

	// 三个图标的点击事件
	@Override
	public void onClick(View v) {
		Intent intent = new Intent(MainActivity.this, MusicService.class);
		switch (v.getId()) {
		case R.id.play:

			if (position != -1) {
				if (v.getTag().equals("playing")) {
					intent.putExtra("name", "0");// 暂停
					play.setImageResource(R.drawable.play);
					play.setTag("pause");
					tv.setText("暂停播放：" + musicList.get(position).getName());
				} else {
					intent.putExtra("name", "1");// 继续播放
					play.setTag("playing");
					tv.setText("正在播放：" + musicList.get(position).getName());
					play.setImageResource(R.drawable.pause);
				}

			} else {
				this.position = 0;
				intent.putExtra("name", musicList.get(position).getPath());
				play.setTag("playing");
				tv.setText("正在播放：" + musicList.get(position).getName());
				play.setImageResource(R.drawable.pause);
			}
			break;

		case R.id.shang:

			if (position != 0) {
				MainActivity.this.position = position - 1;
				intent.putExtra("name", musicList.get(position).getPath());
				play.setImageResource(R.drawable.pause);
				play.setTag("playing");
				tv.setText("正在播放：" + musicList.get(position).getName());

			} else {
				Toast.makeText(MainActivity.this, "已经是第一个", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			break;
		case R.id.xia:
			if (position != musicList.size() - 1) {
				MainActivity.this.position = position + 1;
				intent.putExtra("name", musicList.get(position).getPath());
				play.setImageResource(R.drawable.pause);
				play.setTag("playing");
				tv.setText("正在播放：" + musicList.get(position).getName());

			} else {
				Toast.makeText(MainActivity.this, "已经是最后一个", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			break;
		}
		startService(intent);

	}

	// 循环遍历手机中的文件添加到集合中
	public void show(File file) {
		String[] files = file.list();
		if (files != null) {
			for (String s : files) {
				File newFile = new File(file, s);
				if (newFile.isFile() && s.endsWith(".mp3")) {
					musicList.add(new Music(newFile.toString(), s.substring(0,
							s.length() - 4)));
				} else {
					show(newFile);
				}
			}
		}
	}

	// 菜单的页面
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// 菜单被选中的事件
	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			MainActivity.this.onDestroy();
			return true;
		}
		// 点击菜单扫面文件，并书写到xml文件中
		if (id == R.id.check) {
			seekMusic();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// 搜索音乐的方法
	public void seekMusic() {
		file = new File("/storage/sdcard1");// 设置针对小米手机的外置卡路径
		// pd.setTitle("通知");
		pd.setMessage("正在扫描您手机中的音乐...");
		pd.show();
		new Thread() {
			public void run() {
				Editor editor = sp.edit();
				editor.clear();
				musicList.clear();
				show(file);
				for (Music m : musicList) {
					editor.putString(m.getPath(), m.getName());
				}
				editor.commit();
				pd.dismiss();
				// pd.closeOptionsMenu();

			};
		}.start();
	}

	// 把xml文件中的信息打印到ListView中
	public void putOutList() {
		musicList.clear();
		for (Object s : musicMap.keySet()) {
			musicList.add(new Music(s.toString(), musicMap.get(s).toString()));
		}

		lv.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = View
						.inflate(MainActivity.this, R.layout.item, null);
				TextView musicTitle = (TextView) view
						.findViewById(R.id.musicTitle);
				musicTitle.setText(musicList.get(position).getName());
				musicTitle.setTag(musicList.get(position).getPath());
				return view;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return musicList.size();
			}
		});

	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}

	@Override
	protected void onDestroy() {
		Editor editor = sp1.edit();
		editor.clear();
		editor.putInt("position", position);
		editor.putString("play", play.getTag().toString());
		editor.commit();
		super.onDestroy();
	}
}
