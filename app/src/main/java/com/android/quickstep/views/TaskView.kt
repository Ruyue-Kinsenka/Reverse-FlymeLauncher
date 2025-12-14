package com.android.quickstep.views

import android.content.Context
import android.util.FloatProperty
import android.view.View

open class TaskView(context: Context) : View(context) {
    
    private var dismissTranslationX: Float = 0f
    private var taskOffsetTranslationX: Float = 0f
    private var taskResistanceTranslationX: Float = 0f
    private var splitSelectTranslationX: Float = 0f
    private var gridEndTranslationX: Float = 0f
    
    private var dismissTranslationY: Float = 0f
    private var taskOffsetTranslationY: Float = 0f
    private var taskResistanceTranslationY: Float = 0f
    private var splitSelectTranslationY: Float = 0f

    private var curveTransX = 0f
    private var curveTransY = 0f
    
    fun getPrimaryTaskOffsetTranslationProperty(): FloatProperty<TaskView>? {
        return null
    }
    
    fun isFocusedTask(): Boolean {
        return false
    }
    
    fun setGridTranslationY(gridTranslationY: Float) {
    }
    
    fun isDesktopTask(): Boolean {
        return true
    }
    
    fun getTaskViewId(): Int {
        return 0
    }
    
    fun getScrollAdjustment(b: Boolean): Float {
        return 0.1f
    }
    
    fun setGridTranslationX(v: Float) {
    }
    
    fun getSecondaryTaskOffsetTranslationProperty(): FloatProperty<TaskView>? {
        return null
    }
    
    public fun getPersistentTranslationX(): Float {
        return 0f
    }
    
    public fun getPersistentTranslationY(): Float {
        return 0f
    }
    
    private fun applyTranslationX() {
        setTranslationX(dismissTranslationX + taskOffsetTranslationX + taskResistanceTranslationX + splitSelectTranslationX + gridEndTranslationX + getPersistentTranslationX() + curveTransX)
    }
    
    private fun applyTranslationY() {
        setTranslationY(dismissTranslationY + taskOffsetTranslationY + taskResistanceTranslationY + splitSelectTranslationY + getPersistentTranslationY() + curveTransY )
    }

    fun setCurveTranslationX(x: Float) {
        curveTransX = x
        applyTranslationX()
    }

    fun setCurveTranslationY(y: Float) {
        curveTransY = y
        applyTranslationY()
    }

    fun resetViewTransforms() {
        curveTransX = 0f
        curveTransY = 0f
    }
}