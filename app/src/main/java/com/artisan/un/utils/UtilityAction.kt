package com.artisan.un.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.BindingAdapter
import com.artisan.un.BuildConfig
import com.artisan.un.R
import com.artisan.un.helpers.PreferencesHelper
import com.artisan.un.utils.apis.UserAddress
import com.artisan.un.utils.customView.CustomTextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.Serializable
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Convert dp to px and returns and [Float] type converted pixels value.
 */
fun Int.toPx(context: Context) = run {
    this * context.resources.displayMetrics.density
}

/**
 * @return the pixels based on the [ratio] provided.
 */
fun pxRespectToDeviceWidth(context: Context, ratio: Float) = run {
    context.resources.displayMetrics.widthPixels / ratio
}

/**
 * Converts [Date] object to "dd/MM/yyyy" [String] format.
 */
fun formatDate(date: Date): String = run {
    val simpleDateFormat = SimpleDateFormat(DATE_FORMAT_TYPE_1, Locale.getDefault())
    simpleDateFormat.format(date)
}

/**
 * Converts "dd/MM/yyyy" format string date to [Date] object
 */
fun toDate(date: String?): Date = run {
    if (date != null) {
        val simpleDateFormat = SimpleDateFormat(DATE_FORMAT_TYPE_1, Locale.getDefault())
        simpleDateFormat.parse(date) ?: Date()
    } else {
        Date()
    }
}

/**
 * In case if log is allowed, displays the [otp] in a [Toast]. To enable/disable the Logs configure the [BuildConfig.LOG].
 */
fun Activity.showOtp(otp: Int?) {
    if(BuildConfig.LOG) otp?.let { Toast.makeText(this, "OTP: $otp", Toast.LENGTH_SHORT).show() }
}

/**
 * Performs and handle navigation between different activities.
 */
fun <T> Context.navigateTo(kClass: Class<T>, extra: ArrayList<Pair<String, Any?>> = arrayListOf()) {
    val intent = Intent(this, kClass)
    extra.forEach {
        intent.putExtra(
            it.first,
            when (it.second) {
                is Int -> it.second as Int
                is String -> it.second as String
                is Boolean -> it.second as Boolean
                is ArrayList<*> -> it.second as ArrayList<*>
                is Serializable -> it.second as Serializable
                else -> null
            }
        )
    }

    this.startActivity(intent)
}

/**
 * Redirects to the [url] to display the data. See [Intent.ACTION_VIEW].
 */
fun Context.redirectTo(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}

/**
 * Formats and return the [AddressData] as an [String].
 */
fun formatAddress(addressData: UserAddress?) : String = run{
    var address = ""
    address += addressData?.address_line_one?.let { "$it, " } ?: ""
    address += addressData?.address_line_two?.let { "$it, " } ?: ""
    address += addressData?.tehsil?.let { "$it, " } ?: ""
    address += addressData?.district?.let { "$it, " } ?: ""
    address += addressData?.state?.let { "$it, " } ?: ""
    address += addressData?.country?.let { "$it." } ?: ""
    address += addressData?.pincode?.let { " Pin $it" } ?: ""

    address
}

/**
 * Display a snack bar on the top on the screen.
 */
fun View.showMessage(message: String?) {
    message?.let {
        val snackBar = Snackbar.make(this, it, Snackbar.LENGTH_SHORT)
        val snackBarView = snackBar.view
        val layoutParams = snackBarView.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = Gravity.TOP

        snackBar.show()
    }
}

/**
 * Convert [file][this] to [MultipartBody.Part].
 */
fun File.toMultiPartFile(partName: String, type: String = "*/*"): MultipartBody.Part {
    return MultipartBody.Part.createFormData(
        partName,
        name,
        this.asRequestBody(type.toMediaTypeOrNull())
    )
}

/**
 * Build and display an [DatePickerDialog]. Default date if current date.
 */
fun TextInputEditText.toDatePicker() {
    val calendar = Calendar.getInstance()
    val dateDialog = DatePickerDialog(
        this.context,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            this.setText(formatDate(calendar.time))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    dateDialog.datePicker.maxDate = Calendar.getInstance().time.time
    dateDialog.show()
}

fun String?.isOn(): Boolean = run { this != null && this.equals("on", true) }

fun String?.validateNewImage(field: String, fields: HashMap<String, RequestBody?>, files: ArrayList<MultipartBody.Part>) {
    this?.let {
        if (Patterns.WEB_URL.matcher(this).matches()) {
            fields[field] = this.substring(BuildConfig.BASE_API_URL.length).toRequestBody()
        } else {
            files.add(File(it).toMultiPartFile(field, "image/*"))
        }
    }
}

/**
 * Extract the youtube video id from an [url].
 */
private fun getYoutubeId(url: String?): String? {
    return url?.let {
        val pattern = "https?://(?:[0-9A-Z-]+\\.)?(?:youtu\\.be/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|</a>))[?=&+%\\w]*"
        val compiledPattern: Pattern = Pattern.compile(
            pattern,
            Pattern.CASE_INSENSITIVE
        )
        val matcher: Matcher = compiledPattern.matcher(url)

        if (matcher.find()) matcher.group(1)
        else null
    }
}

/**
 * Build and return an thumbnail of an video using the video id. Video id is extracted using [getYoutubeId].
 */
fun getVideoThumbnail(url: String?) = run {
   "https://img.youtube.com/vi/${getYoutubeId(url)}/0.jpg"
}

/**
 * Checks the network connectivity status of the device.
 * @return "true" if device id connected to the internet and "false" if isn't.
 */
fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    } else {
        @Suppress("DEPRECATION") val nwInfo = connectivityManager.activeNetworkInfo ?: return false
        @Suppress("DEPRECATION") return nwInfo.isConnected
    }
}

/**
 * Deletes the old "FCM" token and generate a new token and save to the preferences using [PreferencesHelper].
 */
fun updateFCM(preferences: PreferencesHelper) {
    FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            it.result?.let { preferences.fcmToken }
        }
    }
}

/**
 * Force hides the soft keyboard.
 */
fun Context.hideKeyBoard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * Compare the size of a [file] with the [maxSize] allowed and returns "true" if [file] size is
 * less than [maxSize] otherwise return "false".
 */
fun validateFileSize(file: File, maxSize: Float): Boolean = run {
    val fileSize = file.length()/1048576F
    fileSize < maxSize
}

/**
 * Get uri for the file to save the picture clicked from the camera and store that to the private directory of the application.
 * That can't be accessed by outside or any other application.
 */
fun Activity.getPicturesFileUri(fileName: String): Uri? = run {
    val tempFile = try {
        File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName,).apply {
            if(!exists()) createNewFile()
        }
    } catch (e: Exception) { null }

    tempFile?.let {
        FileProvider.getUriForFile(this, "${packageName}.fileprovider", it)
    } ?: run {
        null
    }
}

fun Activity.downloadFile(url: String?) {
    url?.let {
        val lastSeparatorIndex = it.lastIndexOf(".")
        val mimeType =  it.substring(lastSeparatorIndex + 1)
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadManagerRequest = DownloadManager.Request(Uri.parse(it))
        downloadManagerRequest.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "${getString(R.string.app_name)}_${Date().time}.$mimeType"
        )
        downloadManagerRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        downloadManager.enqueue(downloadManagerRequest)
    }
}

@BindingAdapter(value = ["app:hint", "app:file"], requireAll = true)
fun CustomTextView.setFileNameAndHint(hint: String?, file: File?) {
    text = file?.let { file.name } ?: run { hint ?: context.getString(R.string.upload_supporting_file) }
}