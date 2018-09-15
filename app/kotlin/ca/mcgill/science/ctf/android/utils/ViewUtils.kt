package ca.mcgill.science.ctf.android.utils

import android.graphics.Color
import android.widget.ImageView
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon

fun ImageView.setIcon(icon: IIcon?, color: Int = Color.WHITE, sizeDp: Int = -1, builder: IconicsDrawable.() -> Unit = {}) {
    if (icon == null) {
        setImageDrawable(null)
    } else {
        val iconDrawable = IconicsDrawable(context).apply {
            icon(icon)
            color(color)
            if (sizeDp > 0) sizeDp(sizeDp)
            builder()
        }
        setImageDrawable(iconDrawable)
    }
}