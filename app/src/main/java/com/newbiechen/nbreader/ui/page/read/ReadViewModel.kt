package com.newbiechen.nbreader.ui.page.read

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.newbiechen.nbreader.R

/**
 *  author : newbiechen
 *  date : 2019-08-29 14:32
 *  description :
 */

class ReadViewModel : ViewModel() {

    val isNightMode = ObservableField(false)
    val isShowMenu = ObservableField(false)
    val isShowBrightMenu = ObservableField(false)
    val isShowSettingMenu = ObservableField(false)

    val topInAnim = ObservableField<Animation>()
    val topOutAnim = ObservableField<Animation>()
    val bottomInAnim = ObservableField<Animation>()
    val bottomOutAnim = ObservableField<Animation>()

    companion object {
        private const val ANIM_DURATION = 200L
    }

    fun init(context: Context) {
        topInAnim.set(loadAnimation(context, R.anim.slide_top_in))
        topOutAnim.set(loadAnimation(context, R.anim.slide_top_out))
        bottomInAnim.set(loadAnimation(context, R.anim.slide_bottom_in))
        bottomOutAnim.set(loadAnimation(context, R.anim.slide_bottom_out))
    }

    private fun loadAnimation(context: Context, id: Int): Animation {
        return AnimationUtils.loadAnimation(context, id).also {
            it.duration = ANIM_DURATION
        }
    }
}