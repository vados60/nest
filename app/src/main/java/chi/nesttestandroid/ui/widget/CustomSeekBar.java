package chi.nesttestandroid.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Created by macbook on 18.12.15.
 */
public class CustomSeekBar extends SeekBar {

    private boolean mEnabled;

    public CustomSeekBar(Context context) {
        super(context);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEnabled) {
            return super.onTouchEvent(event);
        } else {
            return false;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }
}
