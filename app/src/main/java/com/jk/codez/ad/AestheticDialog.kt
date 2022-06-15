package com.jk.codez.ad

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.Keep
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import com.jk.codez.item.Item
import com.jk.codez.R
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView

/**
 * Aesthetic Dialog class
 * Use Builder to create a new instance.
 *
 * @author Gabriel The Code
 */

@Keep
class AestheticDialog {

    class Builder(
            //Necessary parameters
            @NonNull private val activity: Activity,
            @NonNull private val dialogStyle: DialogStyle) {

        private lateinit var code: Item
        private lateinit var btnClickListener: ButtonClickListener
        private lateinit var alertDialog: AlertDialog
        private var mapView: MapView? = null

        private val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(activity)

        private var isCancelable: Boolean = true
        private var isEdit: Boolean = false
        private var gravity: Int = Gravity.NO_GRAVITY
        private var animation: DialogAnimation = DialogAnimation.DEFAULT
        private lateinit var layoutView: View
        private var onClickListener: OnDialogClickListener = object : OnDialogClickListener {
            override fun onDismiss(dialog: Builder) {
                dialog.dismiss()
            }
        }
        private var isInitialized: Boolean = false

        fun setPosition(position: LatLng) {
            this.code.lat = position.latitude
            this.code.lng = position.longitude
            this.code.precise = true
        }

        fun getLayout(): View {
            return layoutView
        }

        fun getMapView(): MapView? {
            return mapView
        }

        @SuppressLint("InflateParams")
        fun init(): MapView? {
            when (dialogStyle) {
                DialogStyle.DETAIL -> {
                    layoutView = activity.layoutInflater.inflate(R.layout.item_detail, null)
                    val tvAddress = layoutView.findViewById<TextView>(R.id.tv_address)
                    val etCodes = layoutView.findViewById<TextView>(R.id.tv_codes)
                    val etNotes = layoutView.findViewById<TextView>(R.id.tv_notes)
                    val btnCancel = layoutView.findViewById<Button>(R.id.btn_cancel)
                    mapView = layoutView.findViewById(R.id.mv_detail)
                    tvAddress.text = String.format("%d %s", this.code.number, this.code.street)
                    if (this.code.codes != null) etCodes.text = this.code.codesString
                    etNotes.text = this.code.notes
                    btnCancel.setOnClickListener { onClickListener.onDismiss(this) }
                    dialogBuilder.setView(layoutView)
                    alertDialog = dialogBuilder.create()
                    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    alertDialog.window?.setGravity(Gravity.TOP)
                    this.chooseAnimation()
                    val height = activity.resources.getDimensionPixelSize(R.dimen.popup_height_code_edit_dialog)
                    alertDialog.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, height)
                    return this.mapView
                }
                DialogStyle.ITEM -> {
                    layoutView = activity.layoutInflater.inflate(R.layout.item_search_detail, null)
                    val numaddress = layoutView.findViewById<TextView>(R.id.tv_numaddress_detail)
                    numaddress.text = String.format("%d %s", this.code.number, this.code.street)
                    val codes = layoutView.findViewById<TextView>(R.id.tv_codes_detail)
                    codes.text = this.code.codesString
                    val notes = layoutView.findViewById<TextView>(R.id.tv_notes_detail)
                    notes.text = this.code.notes
                }
                DialogStyle.CODEZ -> {
                    layoutView = activity.layoutInflater.inflate(R.layout.dialog_code_edit, null)
                    val tvMode = layoutView.findViewById<TextView>(R.id.tv_mode)
                    val etNum = layoutView.findViewById<EditText>(R.id.etNum)
                    val etStreet = layoutView.findViewById<EditText>(R.id.etStreet)
                    val etCodes = layoutView.findViewById<EditText>(R.id.etCodes)
                    val etNotes = layoutView.findViewById<EditText>(R.id.etNotes)
                    val btnSave = layoutView.findViewById<Button>(R.id.btnSave)
                    val btnCancel = layoutView.findViewById<Button>(R.id.btnCancel)
                    val btnDelete = layoutView.findViewById<Button>(R.id.btnDelete)

                    mapView = layoutView.findViewById(R.id.mv_edit)
                    if (this.code.number != null) {
                        tvMode.text = activity.getText(R.string.editcode)
                        etNum.setText(this.code.number.toString())
                    } else {
                        tvMode.text = activity.getText(R.string.addcode)
                    }
                    etStreet.setText(this.code.street)
                    if (this.code.codes != null) etCodes.setText(this.code.codesString)
                    etNotes.setText(this.code.notes)
                    btnSave.setOnClickListener {
                        this.code.number = etNum.text.toString().toIntOrNull()
                        this.code.street = etStreet.text.toString()
                        this.code.codes = etCodes.text.toString().split(" ").toTypedArray()
                        this.code.notes = etNotes.text.toString()
                        btnClickListener.onSave(this)
                    }
                    btnCancel.setOnClickListener { onClickListener.onDismiss(this) }
                    btnDelete.setOnClickListener {
                        println(isEdit)
                        if (isEdit) btnClickListener.onDelete(this)
                    }
                    dialogBuilder.setView(layoutView)
                    alertDialog = dialogBuilder.create()
                    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    alertDialog.window?.setGravity(Gravity.TOP)
                    this.chooseAnimation()
                    val height = activity.resources.getDimensionPixelSize(R.dimen.popup_height_code_edit_dialog)
                    alertDialog.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, height)
                }
            }
            isInitialized = true
            return null
        }

        /**
         * Set is edit
         *
         * @param edit
         * @return this, for chaining.
         */
        @NonNull
        fun setIsEdit(edit: Boolean): Builder {
            this.isEdit = edit
            return this
        }

//        /**
//         * Set dialog title text
//         *
//         * @param title
//         * @return this, for chaining.
//         */
//        @NonNull
//        fun setTitle(@NonNull title: String): Builder {
//            this.title = title
//            return this
//        }
//        /**
//         * Set dialog message text
//         *
//         * @param message
//         * @return this, for chaining.
//         */
//        @NonNull
//        fun setMessage(@NonNull message: String): Builder {
//            this.message = message
//            return this
//        }

        /**
         * Set code to edit
         *
         * @param code
         * @return this, for chaining.
         */
        @NonNull
        fun setItem(@NonNull code: Item): Builder {
            this.code = code
            init()
            return this
        }

        /**
         * Get code
         *
         * @return code
         */
        @NonNull
        fun getItem(): Item {
            return this.code
        }

        /**
         * Set button click listener
         *
         * @param listener
         * @return this, for chaining.
         */
        @NonNull
        fun setButtonClickListener(@NonNull listener: ButtonClickListener): Builder {
            this.btnClickListener = listener
            return this
        }

//        /**
//         * Set dialog mode. Defined by default to false
//         *
//         * @param isDarkMode
//         * @return this, for chaining.
//         */
//        @NonNull
//        fun setDarkMode(@NonNull isDarkMode: Boolean): Builder {
//            this.isDarkMode = isDarkMode
//            return this
//        }
//        /**
//         * Set an OnClickListener to the dialog
//         *
//         * @param onDialogClickListener interface for callback event on click of button.
//         * @return this, for chaining.
//         */
//        @NonNull
//        fun setOnClickListener(onDialogClickListener: OnDialogClickListener): Builder {
//            this.onClickListener = onDialogClickListener
//            return this
//        }
//        /**
//         * Define if the dialog is cancelable
//         *
//         * @param isCancelable
//         * @return this, for chaining.
//         */
//        @NonNull
//        fun setCancelable(isCancelable: Boolean): Builder {
//            this.isCancelable = isCancelable
//            return this
//        }
//        /**
//         * Define the display duration of the dialog
//         *
//         * @param duration in milliseconds
//         * @return this, for chaining.
//         */
//        @NonNull
//        fun setDuration(duration: Int): Builder {
//            if (duration != 0) {
//                this.duration = duration
//                Handler(Looper.getMainLooper()).postDelayed({
//                    this.dismiss()
//                }, duration.toLong())
//            }
//            return this
//        }
//        /**
//         * Set the gravity of the dialog
//         *
//         * @param gravity in milliseconds
//         * @return this, for chaining.
//         */
//        @NonNull
//        fun setGravity(gravity: Int): Builder {
//            this.gravity = gravity
//            return this
//        }

        /**
         * Set the animation of the dialog
         *
         * @param animation in milliseconds
         * @return this, for chaining.
         */
        @NonNull
        fun setAnimation(animation: DialogAnimation): Builder {
            this.animation = animation
            this.chooseAnimation()
            return this
        }

        /**
         * Dismiss the dialog
         *
         * @return Aesthetic Dialog instance.
         */
        @NonNull
        fun dismiss(): AestheticDialog {
            if (alertDialog.isShowing) {
                alertDialog.dismiss()
            }
            return AestheticDialog()
        }

        /**
         * Choose the dialog animation according to the parameter
         *
         */
        @NonNull
        private fun chooseAnimation() {
            when (animation) {
                DialogAnimation.ZOOM -> {
                    alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimationZoom
                }
                DialogAnimation.FADE -> {
                    alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimationFade
                }
                DialogAnimation.CARD -> {
                    alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimationCard
                }
                DialogAnimation.SHRINK -> {
                    alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimationShrink
                }
                DialogAnimation.SWIPE_LEFT -> {
                    alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimationSwipeLeft
                }
                DialogAnimation.SWIPE_RIGHT -> {
                    alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimationSwipeRight
                }
                DialogAnimation.IN_OUT -> {
                    alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimationInOut
                }
                DialogAnimation.SPIN -> {
                    alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimationSpin
                }
                DialogAnimation.SPLIT -> {
                    alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimationSplit
                }
                DialogAnimation.DIAGONAL -> {
                    alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimationDiagonal
                }
                DialogAnimation.WINDMILL -> {
                    alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimationWindMill
                }
                DialogAnimation.SLIDE_UP -> {
                    alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimationSlideUp
                }
                DialogAnimation.SLIDE_DOWN -> {
                    alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimationSlideDown
                }
                DialogAnimation.SLIDE_LEFT -> {
                    alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimationSlideLeft
                }
                DialogAnimation.SLIDE_RIGHT -> {
                    alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimationSlideRight
                }
                DialogAnimation.DEFAULT ->{
                    alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
                }
            }
        }




        /**
         * Displays the dialog according to the parameters of the Builder
         *
         * @return Aesthetic Dialog instance.
         */
        @SuppressLint("InflateParams")
        @NonNull
        fun show(): AestheticDialog {
            alertDialog.setCancelable(isCancelable)
            if (gravity != Gravity.NO_GRAVITY) {
                alertDialog.window?.setGravity(gravity)
            }
            alertDialog.show()
            return AestheticDialog()
        }
    }
}