package com.thryv.bible.views

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton


class BottomScrollAwayBehavior(context: Context, attributeSet: AttributeSet) : CoordinatorLayout.Behavior<FloatingActionButton>(context, attributeSet) {
    private val toolbarHeight: Int
    private var initialY: Float? = null

    init {
        this.toolbarHeight = getToolbarHeight(context)
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, view: FloatingActionButton, dependency: View): Boolean {
        return super.layoutDependsOn(parent, view, dependency) || dependency is AppBarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, view: FloatingActionButton, dependency: View): Boolean {
        val returnValue = super.onDependentViewChanged(parent, view, dependency)
        if (dependency is AppBarLayout) {
            val lp = view.layoutParams as CoordinatorLayout.LayoutParams
            val fabBottomMargin = lp.bottomMargin
            val distanceToScroll = view.height + fabBottomMargin
            val depY = dependency.y
            val initialY = initialY
            if (initialY == null) {
                this.initialY = depY
            } else {
                val depHeight = dependency.height
                val ratio = (depY - initialY) / depHeight
                view.translationY = -distanceToScroll * ratio
            }
        }
        return returnValue
    }

    companion object {
        fun getToolbarHeight(context: Context): Int {
            val tv = TypedValue()
            if (context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    TypedValue.complexToDimensionPixelSize(tv.data, context.theme.resources.displayMetrics)
                } else {
                    TypedValue.complexToDimensionPixelSize(tv.data, context.resources.displayMetrics)
                }
            }

            return 100
        }
    }
}
