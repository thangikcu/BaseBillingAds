package com.mmgsoft.modules.libs.customview;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by khoahihi on 7/12/2017.
 * SavvyCom
 * dangkhoait1989@gmail.com
 */

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
	private final int space;
	private final boolean mIsVertical;

	public SpacesItemDecoration(int space, boolean isVertical) {
		this.space = space;
		mIsVertical = isVertical;
	}

	@Override
	public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
		if (!mIsVertical) {
//			if (parent.getChildLayoutPosition(view) == 0)
//				outRect.left = space;
			outRect.right = space;
		} else {
			outRect.bottom = space;

			// Add top margin only for the first item to avoid double space between items
//			if (parent.getChildLayoutPosition(view) == 0)
//				outRect.top = space;
		}
	}
}
