package kelsos.mbremote.Views;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;
import com.google.inject.Inject;
import com.squareup.otto.Bus;
import kelsos.mbremote.Events.UserActionEvent;
import kelsos.mbremote.R;
import kelsos.mbremote.controller.RunningActivityAccessor;
import kelsos.mbremote.enums.UserInputEventType;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.lyrics)
public class LyricsView extends RoboSherlockActivity
{
	@InjectView(R.id.lyrics_list_view) ListView lyricsView;
	@InjectView(R.id.lyrics_view_track_info_label) TextView trackInfo;
	@Inject
	Bus bus;
	@Inject
	RunningActivityAccessor accessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		accessor.register(this);
		bus.post(new UserActionEvent(UserInputEventType.Lyrics));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.string_value_lyrics);
		String array[] = {""};
		lyricsView.setAdapter(new ArrayAdapter<String>(this,R.layout.lyrics_line_item,array));
    }

	@Override
	protected void onDestroy()
	{
		accessor.unRegister(this);
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

    public void updateLyricsData(String lyrics, String artist, String title)
    {
		String array[] = lyrics.split("\r\n");
		lyricsView.setAdapter(new ArrayAdapter<String>(this,R.layout.lyrics_line_item,array));
        trackInfo.setText(artist + " - " + title);
    }
}