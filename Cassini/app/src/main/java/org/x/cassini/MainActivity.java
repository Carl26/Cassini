package org.x.cassini;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainPage";
    private TextView allEntries, timelineView, tags, starred, stories, settings, newEntry;
    private ImageView allEntriesIcon, timelineViewIcon, tagsIcon, starredIcon, storiesIcon, settingsIcon, goTo1, goTo2, goTo3, goTo4, goTo5, goTo6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Entered onCreate");

        // initialize various components
        initTextView();
        initImageView();
        findViewById(R.id.mainpage_relative_layout).requestFocus();

    }

    private void initTextView(){

        //all entries
        allEntries = (TextView) findViewById(R.id.main_all_entries);
        allEntries.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle = new Bundle();
                bundle.putString("allEntries", (String)allEntries.getText());
                Intent allEntriesAct = new Intent(getApplication(),AllEntriesActivity.class);
                allEntriesAct.putExtras(bundle);
            }
        });

        //timeline view
        timelineView = (TextView) findViewById(R.id.main_timeline_view);
        timelineView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle = new Bundle();
                bundle.putString("timelineView", (String)timelineView.getText());
                Intent timelineViewAct = new Intent(getApplication(),TimelineViewActivity.class);
                timelineViewAct.putExtras(bundle);
            }
        });

        //tags
        tags = (TextView) findViewById(R.id.main_tags);
        tags.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle = new Bundle();
                bundle.putString("tags", (String)tags.getText());
                Intent tagsAct = new Intent(getApplication(),TagsActivity.class);
                tagsAct.putExtras(bundle);
            }
        });

        //starred
        starred = (TextView) findViewById(R.id.main_starred);
        starred.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle = new Bundle();
                bundle.putString("starred", (String)starred.getText());
                Intent starredAct = new Intent(getApplication(),StarredActivity.class);
                starredAct.putExtras(bundle);
            }
        });

        //stories
        stories = (TextView) findViewById(R.id.main_stories);
        stories.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle = new Bundle();
                bundle.putString("stories", (String)stories.getText());
                Intent storiesAct = new Intent(getApplication(),StoriesActivity.class);
                storiesAct.putExtras(bundle);
            }
        });

        //settings
        settings = (TextView) findViewById(R.id.main_all_entries);
        settings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle = new Bundle();
                bundle.putString("settings", (String)settings.getText());
                Intent settingsAct = new Intent(getApplication(),SettingsActivity.class);
                settingsAct.putExtras(bundle);
            }
        });

        //new Entry
        newEntry = (TextView) findViewById(R.id.main_all_entries);
        newEntry.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle = new Bundle();
                bundle.putString("newEntry", (String)newEntry.getText());
                Intent newEntryAct = new Intent(getApplication(),NewEntryActivity.class);
                newEntryAct.putExtras(bundle);
            }
        });
    }

    private void initImageView(){
        allEntriesIcon = (ImageView) findViewById(R.id.main_all_entries_icon);
        timelineViewIcon = (ImageView) findViewById(R.id.main_timeline_view_icon);
        tagsIcon = (ImageView) findViewById(R.id.main_tags_icon);
        starredIcon = (ImageView) findViewById(R.id.main_starred_icon);
        storiesIcon = (ImageView) findViewById(R.id.main_stories_icon);
        settingsIcon = (ImageView) findViewById(R.id.main_settings);
        goTo1 = (ImageView) findViewById(R.id.main_goTo1);
        goTo2 = (ImageView) findViewById(R.id.main_goTo2);
        goTo3 = (ImageView) findViewById(R.id.main_goTo3);
        goTo4 = (ImageView) findViewById(R.id.main_goTo4);
        goTo5 = (ImageView) findViewById(R.id.main_goTo5);
        goTo6 = (ImageView) findViewById(R.id.main_goTo6);
    }

}
