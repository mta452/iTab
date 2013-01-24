/*
 * Copyright (C) 2012 Muhammad Tayyab Akram <dear_tayyab@yahoo.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itab.graphics;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.util.TypedValue;

public class TabBitmap {

	private static final int UNSELECTED_COLOR = 0xFF555555;
	private static final int SELECTED_COLOR = 0xFF278BF2;
	private static final int SHADOW_COLOR = 0xFF202020;
	
	private static Bitmap createScaledBitmap(Bitmap src, int inset) {
		Bitmap bmp = Bitmap.createBitmap(src.getWidth() + (inset * 2), src.getHeight() + (inset * 2), Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		
		Rect srcRect = new Rect(0, 0, src.getWidth(), src.getHeight());
		Rect dstRect = new Rect(inset, inset, src.getWidth() + inset, src.getHeight() + inset);
		
		canvas.drawBitmap(src, srcRect, dstRect, paint);
		
		return bmp;
	}
	
	private static Bitmap create(Resources res, Bitmap src, boolean isSelected) {
		int colors[] = new int[4];
		float locations[] = new float[] { 0, 0.62f, 0.64f, 1 };
		
		if (isSelected) {
			colors[0] = 0xFFFFFFFF;
			colors[1] = 0x66FFFFFF;
			colors[2] = 0x00FFFFFF;
			colors[3] = 0x60FFFFFF;
		} else {
			colors[0] = 0xBBFFFFFF;
			colors[1] = 0x38FFFFFF;
			colors[2] = 0x37FFFFFF;
			colors[3] = 0x1AFFFFFF;
		}
		
		float dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, res.getDisplayMetrics());

		Bitmap shadedBitmap = Bitmap.createBitmap(src.getWidth() + ((int) dp * 2), src.getHeight() + ((int) dp * 2), Config.ARGB_8888);
		Canvas canvas = new Canvas(shadedBitmap);
		canvas.drawColor(isSelected ? SELECTED_COLOR : UNSELECTED_COLOR);

		Shader gradientShader = new LinearGradient(0, 0, src.getWidth() / 4, src.getHeight(), colors, locations, TileMode.CLAMP);
		
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Style.FILL);
		paint.setShader(gradientShader);

		canvas.drawRect(0, 0, src.getWidth(), src.getHeight(), paint);
		
		Bitmap scaledBitmap = TabBitmap.createScaledBitmap(src, (int) dp);
		BitmapShader bitmapShader = new BitmapShader(scaledBitmap, TileMode.CLAMP, TileMode.CLAMP);
		
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		paint.setShader(bitmapShader);

		canvas.drawRect(0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), paint);
		scaledBitmap.recycle();
		
		Bitmap alphaBitmap = shadedBitmap.extractAlpha();
		BlurMaskFilter blurMaskFilter = new BlurMaskFilter(isSelected ? dp * 1.25f : dp, BlurMaskFilter.Blur.OUTER);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
		paint.setMaskFilter(blurMaskFilter);
		paint.setColor(SHADOW_COLOR);
		paint.setShader(null);

		canvas.drawBitmap(alphaBitmap, 0, isSelected ? dp * 1.25f : -dp, paint);
		alphaBitmap.recycle();
		
		return shadedBitmap;
	}
	
	private static Bitmap create(Resources res, int resID, boolean isSelected) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
	    
		Bitmap scr = BitmapFactory.decodeResource(res, resID, options);
		Bitmap tab = TabBitmap.create(res, scr, isSelected);
		
		scr.recycle();
		
		return tab;
	}
	
	public static Bitmap createSelectedBitmap(Resources res, Bitmap src) {
		return TabBitmap.create(res, src, true);
	}
	
	public static Bitmap createSelectedBitmap(Resources res, int id) {
		return TabBitmap.create(res, id, true);
	}
	
	public static Bitmap createUnselectedBitmap(Resources res, Bitmap src) {
		return TabBitmap.create(res, src, false);
	}
	
	public static Bitmap createUnselectedBitmap(Resources res, int id) {
		return TabBitmap.create(res, id, false);
	}
}
