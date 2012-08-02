package com.amlogic.tetheringsettings;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

//Hazel add }
public class AmlogicSettingsActivity extends Activity
{
    public static final String TAG = "AmlogicSettingsActivity";
//    public static final String REGISTER = "register";
//    public static final String FORGETPASSWORD = "forgetPassword";
    public static boolean isProxy = false;
    public static boolean isShownText = false;
	private static ListView mListView;
	private ImageView mImageUp,mImageDown;
//	private static int isLan = 0;
	
	int[] mArrayRes = {
			R.string.network_settings,
			R.string.video_settings,
			R.string.sound_settings,
			R.string.System_update,
			R.string.application_settings,
			R.string.language_input,
			R.string.time_settings,
			R.string.System_info
		};
	//
	
	private ListViewAdapter mListViewAdapter;
	private static int mCurCheckPosition = 0;
	private int mPosition = 0;
	private int mShownCheckPosition = -1;
	private LinearLayout mLeftLayout;
	private LinearLayout mRightLayout;
	protected boolean mBound = false;
	private String mSubChoice = "";
//	protected static CityDataService mService; 
	
	private RelativeLayout mMainLayout;
	private ImageView muteImage;
	private ProgressBar volumeProgress;
	private TextView volumeText;
	private TextView muteText;

	boolean mIsMute = false;
	private boolean volKeyDown = false;
	private int storedVol;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        DisplayMetrics util = getResources().getDisplayMetrics();
        Log.d("bang", "desenty is " + util.density);
        Log.d("bang", "width is " + util.widthPixels);
        Log.d("bang", "height is " + util.heightPixels);
        
        mImageUp = (ImageView) findViewById(R.id.imageup);
        mImageDown = (ImageView) findViewById(R.id.imagedown);
        mMainLayout = (RelativeLayout)findViewById(R.id.main_layout);
        
        
        try
        {
            mPosition = Settings.System.getInt(this.getContentResolver(),"curChoice");
        }
        catch (SettingNotFoundException e)
        {
            e.printStackTrace();
        }
		Bundle bundle = new Bundle();
	    bundle = this.getIntent().getExtras();
	    
        if (bundle != null)
        {
            if (mPosition == 1)
                mCurCheckPosition = 4;
            else
                mCurCheckPosition = bundle.getInt("curChoice", 0);
            mSubChoice = bundle.getString("subChoice", "");
        }
	    
	    if((mCurCheckPosition < 0) || (mCurCheckPosition > mArrayRes.length)){
	    	mCurCheckPosition = 0;
	    }
	    
	    System.out.println("----" + mCurCheckPosition + "====" + mSubChoice);
	    setImageVisibility(mCurCheckPosition);

//		volumeLayout = (RelativeLayout) findViewById(R.id.volume_layout);
//		muteImage = (ImageView) findViewById(R.id.mute_image);
//		volumeProgress = (ProgressBar) findViewById(R.id.volume_progress);
//		volumeText = (TextView) findViewById(R.id.volume);
//		muteText = (TextView) findViewById(R.id.mute_text);
        mListView = (ListView)findViewById(R.id.list); 
        mLeftLayout = (LinearLayout) findViewById(R.id.layout_left);
        mRightLayout = (LinearLayout) findViewById(R.id.layout_right);
        mListViewAdapter = new ListViewAdapter(this, mArrayRes);
		mListView.setAdapter(mListViewAdapter);
		mListView.setDivider(null);
		//mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mListView.setSelection(mCurCheckPosition);
        mListView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> content, View view,
                    int position, long arg3)
            {
                // mListView.setItemChecked(position, true);
                showDetails(position);
                // mListView.setFocusable(false);
            }

        });
		mListView.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				change_background(hasFocus);				
			}
			
		});
		/*FragmentManager fragmentManager= getFragmentManager();
		fragmentManager.addOnBackStackChangedListener(new OnBackStackChangedListener(){

			@Override
			public void onBackStackChanged() {
				// TODO Auto-generated method stub
				
			}
			
		});*/
		mListView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if((keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) || (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)){
					return true;
				}
				return false;
			}
			
		});
		mListView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> mAdapterView, View view,
					int position, long arg3) {
//				setImageVisibility(position);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		showDetails(mCurCheckPosition);  
		change_background(false);
		//change_background(true);
    }
    
    protected void change_background(boolean hasFocus) {
//		if(hasFocus){
//			//Log.d(TAG,"left has focus!");
//			mLeftLayout.setBackgroundResource(R.drawable.bk_left);
////			mRightLayout.setBackgroundResource(R.drawable.bk_right);
//		}else{
//			//Log.d(TAG,"left lost focus!");
//			mLeftLayout.setBackgroundResource(R.drawable.bk_left);
////			mRightLayout.setBackgroundResource(R.drawable.bk_right_none);			
//		}
	}

	public class ListViewAdapter extends BaseAdapter {
    	 
    	//private Context mContext;
		public int[] mArray;
		private LayoutInflater mLayoutInflater;
		private int selectedPosition = -1;  
		
		public void setSelectedPosition(int position) {   
			selectedPosition = position;   
		}

		public ListViewAdapter(Context context, int[] array) { 
			Log.d(TAG,"array size = "+array.length);
    		//mContext = context; 
            mArray = array; 
            mLayoutInflater = LayoutInflater.from(context); 
        } 

		@Override
		public int getCount() {
			return mArray.length;
		}

		@Override
		public Object getItem(int position) {
			return mArray[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
 
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) { 
				holder = new ViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.setting_list, null); 
				holder.textView = (TextView) convertView.findViewById(R.id.textTitle);
				
				convertView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,
				        getResources().getDimensionPixelSize(R.dimen.settings_list_height)));
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.textView.setText(mArray[position]);
			if(selectedPosition == position){
				if(convertView.hasFocus()){
					holder.textView.setBackgroundResource(R.drawable.tab_none); 					
				}else{
					holder.textView.setBackgroundResource(R.drawable.btn_1);
				}
			}else{
				holder.textView.setBackgroundResource(R.drawable.tab_none); 
			}
			return convertView; 
		}

		public class ViewHolder { 
		    public TextView textView;
		} 

    }
	
	public static void setTitleFocus(boolean enable){
		if(enable){
			mListView.setFocusable(true);
			mListView.requestFocus();
		}else{ 
			if(mListView.isFocusable()){
				mListView.clearFocus();
				mListView.setFocusable(false);
			}
		}
	}
	 
    private void showDetails(int index)
    {
        ConfigFocus.Items_t = new ArrayList<ItemInfo>();
        ConfigFocus.Master = new ArrayList<ItemInfo>();
        mCurCheckPosition = index;
        // Settings.System.putInt(this.getContentResolver(), "curChoice", index);
        {
            Fragment fragment = getFragment(index);
            if (fragment != null)
            {
                SettingFragment sf = new SettingFragment(this);
                sf.setFragment(fragment, false);
                mShownCheckPosition = index;
                mListViewAdapter.setSelectedPosition(index);
                mListViewAdapter.notifyDataSetChanged();
            }
        }
    }
	
    private Fragment getFragment(int index)
    {
        Log.d(TAG, "index is " + index);
        Fragment fragment = null;
        switch (index)
        {
        case 0:
            fragment = (Fragment) new TestingSettings();
            break;
            
        }
        return fragment;
    }
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        FragmentManager fragmentManager = getFragmentManager();
        // Log.e(TAG,"back stack = "+fragmentManager.getBackStackEntryCount());
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (isShownText)
            {
                Log.d(TAG, "key back");
                return true;
            }
            if (isProxy)
            {
                Log.e(TAG, "save network proxy setting!");
                NetworkProxy networkProxy = (NetworkProxy) fragmentManager
                        .findFragmentById(R.id.setting_content_fragment);
                if (!networkProxy.saveProxy())
                {
                    return true;
                }
                isProxy = false;
            }
            if ((fragmentManager.getBackStackEntryCount() == 0)
                    && !mListView.isFocusable())
            {
                setTitleFocus(true);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public static void closeActivity()
    {
        
    }
	
	private void setImageVisibility(int pos) { // 1 up,0 down
		if (pos < mArrayRes.length - 8)
			mImageDown.setVisibility(View.VISIBLE);
		if (pos == mArrayRes.length - 1)
			mImageDown.setVisibility(View.INVISIBLE);
		if (pos > 7) // pos > 8
			mImageUp.setVisibility(View.VISIBLE);
		if (pos == 0)
			mImageUp.setVisibility(View.INVISIBLE);
	}
	
}
