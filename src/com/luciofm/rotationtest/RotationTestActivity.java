package com.luciofm.rotationtest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.SlidingDrawer;
import android.widget.Toast;

public class RotationTestActivity extends FragmentActivity {

	protected static final String TAG = "RotationTestActivity";
	private ProgressDialog mProgress = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		/* Look if there is already an instance of LoaderFragment */
		LoaderFragment loader = (LoaderFragment) getSupportFragmentManager().findFragmentByTag("loader");
		if (loader == null) {
			loader = LoaderFragment.newInstance("http://www.kernel.org/pub/linux/kernel/v3.0/snapshots/patch-3.1-rc4-git2.bz2");
			getSupportFragmentManager().beginTransaction().add(loader, "loader").commit();
		}

		if (!loader.isLoaded())
			mProgress = ProgressDialog.show(this, "Rotation Test", "Loading web page...");
		else if (mProgress != null)
			mProgress.dismiss();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mProgress != null)
			mProgress.dismiss();
		mProgress = null;
	}

	public void onLoadFinished(String message) {
		if (mProgress != null)
			mProgress.dismiss();
		Toast.makeText(this, "Response: " + message, Toast.LENGTH_LONG).show();
	}

	public static class LoaderFragment extends Fragment {

		private boolean loaded = false;
		private RotationTestActivity activity = null;

		public static LoaderFragment newInstance(String url) {
			LoaderFragment f = new LoaderFragment();

			Bundle args = new Bundle();
			args.putString("url", url);
			f.setArguments(args);
			return f;
		}

		public boolean isLoaded() {
			return loaded;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setRetainInstance(true);

			String url = getArguments().getString("url");
			asyncTask.execute(url);
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			this.activity = (RotationTestActivity) activity;
		}

		@Override
		public void onDetach() {
			super.onDetach();
			this.activity = null;
		}

		private AsyncTask<String, Void, String> asyncTask = new AsyncTask<String, Void, String>() {

			protected String doInBackground(String... params) {
				Log.d(TAG, "Loading url: " + params[0]);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "Loaded";
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				loaded = true;
				if (activity != null)
					activity.onLoadFinished(result);
			}
		};
	}
}