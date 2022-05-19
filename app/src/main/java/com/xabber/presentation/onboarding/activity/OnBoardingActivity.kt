package com.xabber.presentation.onboarding.activity

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider

import androidx.fragment.app.Fragment
import com.soundcloud.android.crop.BuildConfig.APPLICATION_ID
import com.soundcloud.android.crop.Crop

import com.xabber.R
import com.xabber.data.util.AppConstants.REQUEST_TAKE_PHOTO
import com.xabber.data.util.AppConstants.TEMP_FILE_NAME
import com.xabber.databinding.ActivityOnboardingBinding
import com.xabber.presentation.application.activity.ApplicationActivity
import com.xabber.presentation.onboarding.contract.Navigator
import com.xabber.presentation.onboarding.contract.ToolbarChanger
import com.xabber.presentation.onboarding.fragments.signin.SigninFragment
import com.xabber.presentation.onboarding.fragments.signup.SignupAvatarFragment
import com.xabber.presentation.onboarding.fragments.signup.SignupNicknameFragment
import com.xabber.presentation.onboarding.fragments.signup.SignupPasswordFragment
import com.xabber.presentation.onboarding.fragments.signup.SignupUserNameFragment
import com.xabber.presentation.onboarding.fragments.start.StartFragment
import me.relex.circleindicator.BuildConfig.APPLICATION_ID
import java.io.File
import java.io.IOException


class OnBoardingActivity : AppCompatActivity(), Navigator, ToolbarChanger {
    private val binding: ActivityOnboardingBinding by lazy {
        ActivityOnboardingBinding.inflate(layoutInflater)
    }

    private val viewModel: OnboardingViewModel by viewModels()

    private var nickName: String? = null
    private var userName: String? = null
    private var password: String? = null
    private var filePhotoUri: Uri? = null
    private var newAvatarImageUri: Uri? = null


    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(), ::onGotCameraPermissionResult
    )

    private val requestGalleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::onGotGalleryPermissionResult
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        subscribeToDataFromFragments()
        binding.onboardingToolbar.title = ""
        setSupportActionBar(binding.onboardingToolbar)
        if (savedInstanceState == null) addStartFragment()
    }

    private fun subscribeToDataFromFragments() {
        viewModel.nickName.observe(this) {
            nickName = it
        }
        viewModel.username.observe(this) {
            userName = it
        }
        viewModel.password.observe(this) {
            password = it
        }
    }

    private fun addStartFragment() {
        supportFragmentManager.beginTransaction().replace(
            R.id.onboarding_container, StartFragment()
        ).commit()
    }

    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.appearance, R.animator.disappearance)
            .addToBackStack(null)
            .replace(R.id.onboarding_container, fragment).commit()
    }

    override fun startSignupNicknameFragment() {
        launchFragment(SignupNicknameFragment())
    }

    override fun startSignupUserNameFragment() {
        launchFragment(SignupUserNameFragment())
    }

    override fun startSignupPasswordFragment() {
        launchFragment(SignupPasswordFragment())
    }

    override fun startSignupAvatarFragment() {
        launchFragment(SignupAvatarFragment())
    }

    override fun startSigninFragment() {
        launchFragment(SigninFragment())
    }


    override fun goToApplicationActivity(isSignedIn: Boolean) {
        val intent = Intent(this, ApplicationActivity::class.java)
        intent.putExtra("isSignedIn",true)
        startActivity(intent)
        finish()
        overridePendingTransition(R.animator.appearance, R.animator.disappearance)
    }

    override fun goBack() {
        onBackPressed()
    }

    override fun openCamera() {
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    override fun openGallery() {
        requestGalleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun onGotCameraPermissionResult(granted: Boolean) {
        if (granted) {
            takePhoto()
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                askUserForOpeningAppSettings()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            createTempImageFile(TEMP_FILE_NAME).let {
             //   filePhotoUri = getFileUri(it)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, filePhotoUri)
                startActivityForResult(
                    takePictureIntent,
                    REQUEST_TAKE_PHOTO
                )
            }
        } catch (e: IOException) {
            Log.e("qwe", e.stackTraceToString())
        }
    }


    fun createTempImageFile(name: String): File {
        return File.createTempFile(
            name,
            ".jpg",
            application.getExternalFilesDir(null)
        )
    }


    private fun askUserForOpeningAppSettings() {
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        if (packageManager.resolveActivity(
                appSettingsIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            ) == null
        ) {
            Toast.makeText(this, "Permissions denied forever", Toast.LENGTH_SHORT).show()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Permission denied")
                .setMessage("Would you like to open app settings if change your mind?")
                .setPositiveButton("Open") { _, _ ->
                    startActivity(appSettingsIntent)
                }
                .create()
                .show()
        }
    }

    private fun onGotGalleryPermissionResult(granted: Boolean) {
        if (granted) {
            chooseFromGallery()
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                askUserForOpeningAppSettings()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun chooseFromGallery() {
        Crop.pickImage(this)
    }


    override fun setTitle(titleResId: Int) {
        binding.onboardingToolbar.setTitle(titleResId)
    }

    override fun clearTitle() {
        binding.onboardingToolbar.title = ""
    }

    override fun setShowBack(isVisible: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(isVisible)
        supportActionBar?.setDisplayShowHomeEnabled(isVisible)
    }

//    private fun getFileUri(file: File): Uri {
//      //  return FileProvider.getUriForFile(
//      //      application, BuildConfig.APPLICATION_ID + ".provider",
//            file
//    //    )
//
//    }
}



