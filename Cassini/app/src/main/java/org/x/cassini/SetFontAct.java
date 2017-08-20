//package org.x.cassini;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.TextView;
//
//public class SetFontAct extends AppCompatActivity implements View.OnClickListener {
//
//    private TextView mono,rRegular,rCondensed,rLight,rThin,serif,gothic;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Utils.onActivityCreateSetTheme(this);
//        setContentView(R.layout.activity_set_font);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
////        initTextView();
//    }
//
//    @Override
//    public void onClick(View v)
//    {
//        // TODO Auto-generated method stub
//        switch (v.getId())
//        {
//            case R.id.settings_font_mono:
//                Utils.changeToTheme(MainActivity, Utils.THEME_Mono);
//                break;
//            case R.id.settings_font_roboto_thin:
//                Utils.changeToTheme(MainTextActivity, Utils.THEME_rThin);
//                break;
//        }
//    }
//}
