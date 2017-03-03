package slidingmenu;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout leftViewGroup = createLeftView();
		
		LinearLayout rightViewGroup = createRightView();
        
        final SlidingMenu mSlidingMenu = new SlidingMenu(this);
        mSlidingMenu.addLeftView(leftViewGroup);
        mSlidingMenu.addRightView(rightViewGroup);
        
        setContentView(mSlidingMenu);
        
    }
	private LinearLayout createRightView() {
		LinearLayout rightViewGroup = new LinearLayout(this);
		rightViewGroup.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		rightViewGroup.setBackgroundColor(Color.BLUE);
        TextView rightView = new TextView(this);
        rightView.setText("点击当前蓝色区域");
        rightViewGroup.addView(rightView);
		return rightViewGroup;
	}

	
	private LinearLayout createLeftView() {
		LinearLayout leftViewGroup = new LinearLayout(this);
		leftViewGroup.setLayoutParams(new LayoutParams(300, LayoutParams.FILL_PARENT));
		leftViewGroup.setBackgroundColor(Color.GREEN);
        TextView leftView = new TextView(this);
        leftView.setText("Left View");
        leftViewGroup.addView(leftView);
		return leftViewGroup;
	}


}