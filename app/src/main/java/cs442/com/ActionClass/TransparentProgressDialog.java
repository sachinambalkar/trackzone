package cs442.com.ActionClass;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import cs442.com.pulse.R;

/**
 * Created by Sachin on 16-04-2015.
 */
public class TransparentProgressDialog extends Dialog
{
    private ImageView iv;
    private ImageView iv2;
    public TransparentProgressDialog(Context context, int resourceIdOfImage,int resourceIdOfImage2) {
        super(context, R.style.TransparentProgressDialog);
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        RelativeLayout layout = new RelativeLayout(context);
        //layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundResource(R.drawable.loadcenter);
      //  layout.set
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
   //     LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        iv = new ImageView(context);
        iv2 = new ImageView(context);
        iv.setImageResource(resourceIdOfImage);
        iv2.setImageResource(resourceIdOfImage2);
        iv.setVisibility(View.VISIBLE);
        iv2.setVisibility(View.VISIBLE);
        layout.addView(iv, params);
        layout.addView(iv2, params);

        addContentView(layout, params);
    }

    @Override
    public void show() {
        super.show();
        RotateAnimation anim = new RotateAnimation(0.0f, 360.0f , Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(300);


        RotateAnimation anim2 = new RotateAnimation(360.0f,0.0f , Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
        anim2.setInterpolator(new LinearInterpolator());
        anim2.setRepeatCount(Animation.INFINITE);
        anim2.setDuration(250);

        iv.setAnimation(anim2);
        iv2.setAnimation(anim);

        iv2.startAnimation(anim2);
       iv.startAnimation(anim);
    }
}