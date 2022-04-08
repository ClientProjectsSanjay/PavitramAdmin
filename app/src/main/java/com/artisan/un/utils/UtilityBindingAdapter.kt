package com.artisan.un.utils

import android.graphics.drawable.Drawable
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.DigitsKeyListener
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.drawerlayout.widget.DrawerLayout
import com.artisan.un.BuildConfig
import com.artisan.un.R
import com.artisan.un.apiModel.ProductDetails
import com.artisan.un.ui.common.ActivityWebView
import com.artisan.un.ui.home.NotificationActivity
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.textfield.TextInputEditText
import java.lang.NumberFormatException
import java.util.*

/**
 * Set custom [TextInputType] to the [TextInputEditText].
 */
@BindingAdapter(value = ["app:setDrawableEnd", "app:setInputType"], requireAll = true)
fun TextInputEditText.setCustomDrawableEnd(drawable: Drawable?, inputType: TextInputType?) {
    drawable?.let {
        this.setCompoundDrawablesWithIntrinsicBounds(null, null, it, null)
    }

    inputType?.let {
        this.inputType = when (it) {
            TextInputType.NONE -> InputType.TYPE_NULL
            TextInputType.TEXT -> InputType.TYPE_CLASS_TEXT
            TextInputType.PHONE -> InputType.TYPE_CLASS_PHONE
            TextInputType.NUMBER -> InputType.TYPE_CLASS_NUMBER
            TextInputType.DATE -> InputType.TYPE_CLASS_DATETIME
            TextInputType.EMAIL -> InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
            TextInputType.TEXT_PASSWORD -> InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        if (it == TextInputType.TEXT_PASSWORD) this.transformationMethod =
            PasswordTransformationMethod()
        else if (it == TextInputType.DATE) this.isFocusable = false
    }
}

@BindingAdapter("app:digits")
fun TextInputEditText.setDigits(digits: String?) {
    digits?.let { this.keyListener = DigitsKeyListener.getInstance(it) }
}

/**
 * Set date to [TextInputEditText] and use [getDate] to reverse binding.
 */
@BindingAdapter("app:setDate")
fun TextInputEditText.setDate(date: Date?) {
    date?.let {
        this.setText(formatDate(it))
        Log.d("PRINT", formatDate(it))
    }
}

@InverseBindingAdapter(attribute = "app:setDate", event = "android:textAttrChanged")
fun TextInputEditText.getDate(): Date = run {
    val input = this.text?.toString()
    toDate(input)
}

/**
 * Set drawable to the end of the [AutoCompleteTextView]
 */
@BindingAdapter("app:setDrawableEnd")
fun AutoCompleteTextView.setCustomDrawableEnd(drawable: Drawable?) {
    drawable?.let {
        this.setCompoundDrawablesWithIntrinsicBounds(null, null, it, null)
    }
}

/**
 * Build an [SpannableString] of [R.string.terms_and_privacy_policy] with differentiable [R.string.terms_conditions]
 * and [R.string.privacy_policy] strings and set to the [this] and performs respected click actions.
 */
@BindingAdapter("app:enableTermsAndConditionText")
fun TextView.enableTermsAndConditionText(doEnable: Boolean?) {
    doEnable?.let {
        val fullText = this.context.getString(R.string.terms_and_privacy_policy)
        val textList = listOf(
            this.context.getString(R.string.terms_conditions),
            this.context.getString(R.string.privacy_policy)
        )

        val spannableString = SpannableString(fullText)

        for (text in textList) {
            val span = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    when (text) {
                        this@enableTermsAndConditionText.context.getString(R.string.terms_conditions) -> {
                            this@enableTermsAndConditionText.context.navigateTo(ActivityWebView::class.java, arrayListOf(
                                Pair(PAGE_TITLE, this@enableTermsAndConditionText.context.getString(R.string.terms_conditions)),
                                Pair(WEB_URL, BuildConfig.TERMS_AND_CONDITIONS_URL)
                            ))
                        }
                        this@enableTermsAndConditionText.context.getString(R.string.privacy_policy) -> {
                            this@enableTermsAndConditionText.context.navigateTo(ActivityWebView::class.java, arrayListOf(
                                Pair(PAGE_TITLE, this@enableTermsAndConditionText.context.getString(R.string.privacy_policy)),
                                Pair(WEB_URL, BuildConfig.PRIVACY_POLICY_URL)
                            ))
                        }
                    }
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(
                        this@enableTermsAndConditionText.context,
                        R.color.orange
                    )
                }
            }

            spannableString.setSpan(
                span,
                fullText.indexOf(text),
                fullText.indexOf(text) + text.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        this.text = spannableString
        this.movementMethod = LinkMovementMethod.getInstance()
    }
}

/**
 * Allow to click on the view i.e. [this] and navigate the user to [NotificationActivity].
 */
@BindingAdapter("app:enableNotification")
fun ImageView.enableNotification(doEnable: Boolean?) {
    doEnable?.let {
        if (it) {
            this.setOnClickListener {
                this.context.navigateTo(NotificationActivity::class.java)
            }
        }
    }
}

/**
 * Set custom end padding to [TextInputEditText].
 */
@BindingAdapter("app:customPaddingEnd")
fun TextInputEditText.customPaddingEnd(value: Int?) {
    value?.let {
        val padding = (if (it > 0) it else 16).toPx(this.context).toInt()
        this.setPadding(this.paddingLeft, this.paddingTop, padding, this.paddingBottom)
    }
}

/**
 * Formats the dimension parameters of the [ProductDetails] to and [String] and set to [this].
 */
@BindingAdapter("app:productDimension")
fun TextView.productDimension(product: ProductDetails?) {
    product?.let {
        val height: String? = it.height?.let { data -> "${this.context.getString(R.string.height)}: $data ${it.height_unit}" }
        val width: String? = it.width?.let { data -> "${this.context.getString(R.string.width)}: $data ${it.width_unit}" }
        val length: String? = it.length?.let { data -> "${this.context.getString(R.string.length)}: $data ${it.length_unit}" }
        val weight: String? = it.weight?.let { data -> "${this.context.getString(R.string.weight)}: $data ${it.weight_unit}" }
        val volume: String? = it.vol?.let { data -> "${this.context.getString(R.string.volume)}: $data ${it.vol_unit}" }

        var finalString: String? = ""
        height?.let { string -> if(finalString?.isNotEmpty() == true) finalString += "\n"; finalString += string }
        width?.let { string -> if(finalString?.isNotEmpty() == true) finalString += "\n"; finalString += string }
        length?.let { string -> if(finalString?.isNotEmpty() == true) finalString += "\n"; finalString += string }
        volume?.let { string -> if(finalString?.isNotEmpty() == true) finalString += "\n"; finalString += string }
        weight?.let { string -> if(finalString?.isNotEmpty() == true) finalString += "\n"; finalString += string }

        this.text = finalString
    }
}

/**
 * Build an autocomplete dropdown and set to [AutoCompleteTextView].
 * If it is a [Spinner] set an [SpinnerAdapter] of dimensions.
 */
@BindingAdapter("app:dropDownMenu")
fun View.enableDropDown(menu: ArrayList<String>?) {
    if (this is AutoCompleteTextView) {
        menu?.let {
            val adapter = ArrayAdapter(this.context, android.R.layout.simple_list_item_1, it)
            this.setAdapter(adapter)
            this.threshold = 1
            this.setOnClickListener {
                this.context.hideKeyBoard(this.rootView)
                this.showDropDown()
            }
        }
    } else if (this is Spinner) {
        val list = arrayListOf("CM", "Meter", "Inch", "Feet")
        val adapter = ArrayAdapter(this.context, R.layout.view_spinner_text, R.id.text_view, list)
        this.adapter = adapter
    }
}

/**
 * Build an "top-right" and "bottom-right" round cornered [NavigationView] background.
 */
@BindingAdapter("app:makeRoundCorner")
fun NavigationView.makeRoundCorner(doRound: Boolean?) {
    doRound?.let {
        if (doRound) {
            val shapeDrawable = this.background as MaterialShapeDrawable
            shapeDrawable.shapeAppearanceModel = shapeDrawable.shapeAppearanceModel.toBuilder()
                .setTopRightCorner(CornerFamily.ROUNDED, 24.toPx(this.context))
                .setBottomRightCorner(CornerFamily.ROUNDED, 24.toPx(this.context))
                .build()
        }
    }
}

/**
 * Handles the close and open action of an [DrawerLayout] on click of the [this].
 */
@BindingAdapter("app:attachToNavigation")
fun View.attachToNavigation(layout: DrawerLayout?) {
    layout?.let {
        this.setOnClickListener {
            if (layout.isDrawerOpen(GravityCompat.START)) layout.closeDrawer(GravityCompat.START)
            else {
                this.context.hideKeyBoard(this)
                layout.openDrawer(GravityCompat.START)
            }
        }
    }
}

/**
 * Set default data to the [AppCompatSpinner] and [captureSelectedValue] to reverse bind the updated value.
 */
@BindingAdapter(value = ["selectedValue", "selectedValueAttrChanged"], requireAll = false)
fun bindSpinnerData(
    pAppCompatSpinner: AppCompatSpinner,
    newSelectedValue: String?,
    newTextAttrChanged: InverseBindingListener
) {
    pAppCompatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
            newTextAttrChanged.onChange()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
    if (newSelectedValue != null) {
        val pos = (pAppCompatSpinner.adapter as SpinnerAdapter).getPosition(newSelectedValue)
        pAppCompatSpinner.setSelection(pos, true)
    }
}

@InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
fun captureSelectedValue(pAppCompatSpinner: AppCompatSpinner): String {
    return (pAppCompatSpinner.adapter as SpinnerAdapter).getItem(pAppCompatSpinner.selectedItemPosition).toString()
}

/**
 * Load image from network using [Glide].
 * In case if the [image] do not contains an prefix the attach default server prefix i.e. [BuildConfig.BASE_API_URL].
 */
@BindingAdapter("app:loadImage")
fun ImageView.loadImage(image: String?) {
    image?.let {
        Glide.with(this.context)
            .load(if(Patterns.WEB_URL.matcher(it).matches()) it else BuildConfig.BASE_API_URL + it)
            .placeholder(ContextCompat.getDrawable(this.context, R.drawable.default_placeholder))
            .into(this)
    }
}

/**
 * Validate max [decimal] points allowed to type in an edittext [this].
 */
@BindingAdapter("app:maxDecimal")
fun EditText.maxDecimal(decimal: Int?) {
    decimal?.let {
        this.doOnTextChanged { text, _, _, _ ->
            if(text?.contains(".") == true ) {
                if(text[0] == '.') {
                    this.setText(text.toString().replace(".", "0."))
                    this.setSelection(this.text.length)
                } else {
                    val lengthAfterDecimal = text.substring(text.indexOf("."))
                    if(lengthAfterDecimal.length > (it + 1)) {
                        this.setText(text.dropLast(1))
                        this.setSelection(this.text.length)
                    }
                }
            }
        }
    }
}

/**
 * Validate max [maxPrice] allowed to type in an edittext [this].
 */
@BindingAdapter("app:limitPriceTo")
fun EditText.limitPriceTo(maxPrice: Int?) {
    maxPrice?.let {
        this.doOnTextChanged { text, _, _, _ ->
            if(text?.isNotEmpty() == true) {
                val amount = try { text.toString().toInt() }
                catch (exception: NumberFormatException) { 0 }

                if(amount <= 0) this.text = null
                else if(amount > maxPrice) {
                    this.setText(text.dropLast(1))
                    this.setSelection(this.text.length)
                }
            }
        }
    }
}