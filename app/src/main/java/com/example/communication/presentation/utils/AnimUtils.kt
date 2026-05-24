package com.example.communication.presentation.utils

import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.example.communication.R

/**
 * Extension functions and helpers for beautiful animations throughout the app.
 */
object AnimUtils {

    /** Staggered slide-up entrance for RecyclerView items */
    fun runLayoutAnimation(recyclerView: RecyclerView) {
        val context = recyclerView.context
        val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_stagger)
        recyclerView.layoutAnimation = controller
        recyclerView.adapter?.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }

    /** Count-up number animation for stat TextViews */
    fun countUp(target: android.widget.TextView, toValue: Int, durationMs: Long = 800L) {
        val animator = ValueAnimator.ofInt(0, toValue)
        animator.duration = durationMs
        animator.interpolator = android.view.animation.DecelerateInterpolator(1.5f)
        animator.addUpdateListener { target.text = it.animatedValue.toString() }
        animator.start()
    }

    /** Staggered entrance for a list of views (e.g. home stat cards) */
    fun staggerEnter(views: List<View>, delayStep: Long = 80L, baseDelay: Long = 100L) {
        views.forEachIndexed { i, view ->
            view.alpha = 0f
            view.translationY = 80f
            view.scaleX = 0.88f
            view.scaleY = 0.88f
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .scaleX(1f)
                .scaleY(1f)
                .setStartDelay(baseDelay + i * delayStep)
                .setDuration(420)
                .setInterpolator(OvershootInterpolator(1.2f))
                .start()
        }
    }

    /** Pulse / bounce animation on a single view */
    fun pulse(view: View) {
        view.animate()
            .scaleX(1.12f)
            .scaleY(1.12f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .setInterpolator(OvershootInterpolator(2f))
                    .start()
            }.start()
    }

    /** Item press feedback — scale down then bounce back */
    fun itemPress(view: View, action: () -> Unit) {
        view.animate()
            .scaleX(0.94f)
            .scaleY(0.94f)
            .setDuration(80)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(180)
                    .setInterpolator(OvershootInterpolator(2f))
                    .withEndAction { action() }
                    .start()
            }.start()
    }

    /** FAB entrance animation with spring overshoot */
    fun showFab(view: View) {
        view.scaleX = 0f
        view.scaleY = 0f
        view.alpha = 0f
        view.translationY = 120f
        view.visibility = View.VISIBLE
        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .translationY(0f)
            .setDuration(450)
            .setStartDelay(200)
            .setInterpolator(OvershootInterpolator(1.8f))
            .start()
    }

    /** Slide down + fade in a view from above */
    fun slideDownIn(view: View, delay: Long = 0L) {
        view.alpha = 0f
        view.translationY = -40f
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(delay)
            .setDuration(350)
            .setInterpolator(android.view.animation.DecelerateInterpolator(2f))
            .start()
    }

    /** Fade + scale in for dialogs / bottom sheets content */
    fun fadeScaleIn(view: View, delay: Long = 0) {
        view.alpha = 0f
        view.scaleX = 0.92f
        view.scaleY = 0.92f
        view.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setStartDelay(delay)
            .setDuration(300)
            .setInterpolator(OvershootInterpolator(1.1f))
            .start()
    }
}

/** Extension: apply card-press scale StateListAnimator programmatically */
fun View.applyPressScale() {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
        stateListAnimator = android.animation.AnimatorInflater.loadStateListAnimator(
            context,
            R.animator.card_press
        )
    }
}

/** Extension: start layout animation on RecyclerView when new data arrives */
fun RecyclerView.animateItems() {
    AnimUtils.runLayoutAnimation(this)
}
