package com.dicoding.tamantic.view.activity.scan

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.response.ScanResponse
import com.dicoding.tamantic.data.retrofit.ApiConfig
import com.dicoding.tamantic.databinding.ActivityDetailScanBinding
import com.dicoding.tamantic.view.utils.createCustomTempFile
import com.google.gson.Gson
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class DetailScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailScanBinding

    private var currentImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentImageUri = intent.getStringExtra("imageUri")?.let {
            Uri.parse(it)
        }

        showImage(currentImageUri)
        setupAction()
        setupView()
    }

    private fun setupAction() {
        binding.actionToEdit.setOnClickListener { startUCrop(currentImageUri!!) }
        binding.actionToGallery.setOnClickListener { gallery() }
        binding.actionBack.setOnClickListener { onBackPressed() }
        binding.actionScan.setOnClickListener {
            showLoading(true)
            uploadImage()
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            lifecycleScope.launch {
                try {
                    val imageFile = uriToFile(uri, this@DetailScanActivity)
                    val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                    val multipartBody = MultipartBody.Part.createFormData(
                        "image",
                        imageFile.name,
                        requestImageFile
                    )
                    ApiConfig.getApiService("").getDataScan(multipartBody).enqueue(object :
                        Callback<ScanResponse>{
                        override fun onResponse(
                            call: Call<ScanResponse>,
                            response: Response<ScanResponse>
                        ) {
                            val data = response.body()
                            if(data != null){
                                val intent = Intent(this@DetailScanActivity, ResultScanActivity::class.java)
                                intent.putExtra("IMAGE_SCAN", currentImageUri.toString())
                                intent.putExtra("SCAN_KEY", data)
                                startActivity(intent)
                                showLoading(false)
                            }else{
                                popupAlertFailed("Gambar tidak boleh kosong!")
                            }
                        }

                        override fun onFailure(call: Call<ScanResponse>, t: Throwable) {
                        }

                    })

                }catch(e: Exception){
                    popupAlertFailed("Gagal upload gambar")
                }                }
        }
    }

    fun uriToFile(imageUri: Uri, context: Context): File {
        val myFile = createCustomTempFile(context)
        val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
        outputStream.close()
        inputStream.close()
        return myFile
    }

    private fun showImage(image: Uri?) {
        binding.imageScan.setImageURI(image)
    }

    private fun gallery(){
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage(currentImageUri)
        } else {
            popupAlertFailed("Tidak ada yang di pilih")
        }
    }

    private fun startUCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "cropped"))
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1080, 1080)
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            showImage(resultUri)

        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            popupAlertFailed("failed to crop image $cropError")
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            window.setDecorFitsSystemWindows(true)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val statusBarColor = when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> ContextCompat.getColor(this, android.R.color.black)
            Configuration.UI_MODE_NIGHT_NO -> ContextCompat.getColor(this, R.color.white)
            else -> ContextCompat.getColor(this, R.color.white)
        }

        window.statusBarColor = statusBarColor
    }

    private fun popupAlertFailed(errorMessage: String) {
        val dialogBinding = layoutInflater.inflate(R.layout.element_popup_alert, null)
        val dialog = android.app.Dialog(this)
        dialog.setContentView(dialogBinding)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val btn_ok = dialogBinding.findViewById<Button>(R.id.alert_yes)
        btn_ok.setOnClickListener {
            dialog.dismiss()
        }

        val message = dialogBinding.findViewById<TextView>(R.id.alert_message)
        val title = dialogBinding.findViewById<TextView>(R.id.alert_title)
        title.text = "Gagal"
        message.text = errorMessage
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}