package com.deb.wealthtest.util

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.deb.wealthtest.R

/**
 * Created by Ram on 11/4/17.
 */

object FontUtils {

    fun getFont(context: Context, fontName: String): Typeface {
        return ResourcesCompat.getFont(context, R.font.font_file)!!
    }
}
