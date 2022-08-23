package com.artisan.un.ui.home

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.artisan.un.BuildConfig
import com.artisan.un.R
import com.artisan.un.baseClasses.BaseActivity
import com.artisan.un.databinding.ActivityMainBinding
import com.artisan.un.ui.common.dialog.CustomAlertDialog
import com.artisan.un.ui.home.fragment.*
import com.artisan.un.ui.home.myOrder.ActivityMyOrder
import com.artisan.un.ui.product.AddProductBasicActivity
import com.artisan.un.ui.userauth.ActivityLogin
import com.artisan.un.ui.userauth.viewModel.LoginViewModel
import com.artisan.un.utils.*
import com.artisan.un.utils.apis.UserInfo

class MainActivity : BaseActivity<ActivityMainBinding, LoginViewModel>(
    R.layout.activity_main,
    LoginViewModel::class
), DrawerListener, AppBarListener {
    private var menuType: MenuType? = null

    override fun onCreate() {
        viewDataBinding.listener = this
        viewDataBinding.addBarListener = this
        viewDataBinding.version = BuildConfig.VERSION_NAME

        menuType = if(intent.getStringExtra(MENU_TYPE) == MenuType.PROFILE.name) MenuType.PROFILE else MenuType.HOME

        observeData()
        validateLanguage()
        validateMenu(menuType)
    }

    override fun onResume() {
        super.onResume()
        viewDataBinding.profileImage = ApplicationData.user?.user?.profileImage
        viewDataBinding.userName = ApplicationData.user?.user?.name ?: getString(R.string.app_name)

        validateMenu(menuType)
    }

    private fun observeData() {
        mViewModel.userResponse.observe(this) {
            viewDataBinding.profileImage = it?.user?.profileImage
            viewDataBinding.executePendingBindings()
        }

        mViewModel.isProfileDeleted.observe(this) {
            mPref.clear()
            ApplicationData.user = null

            CustomAlertDialog(
                context = this,
                message = getString(R.string.profile_deleted),
                primaryKey = getString(R.string.ok),
                isCancelable = false,
                primaryKeyAction = {
                    navigateTo(ActivityLogin::class.java)
                    finishAffinity()
                }
            ).show()
        }
    }

    override fun onBackPressed() {
        if (menuType == MenuType.HOME) super.onBackPressed()
        else {
            val fragment = supportFragmentManager.findFragmentByTag("webView") as? FragmentWebView
            fragment?.let {
                fragment.onBackPressed {
                    if (it.canGoBack()) it.goBack()
                    else {
                        menuType = MenuType.HOME
                        validateMenu(menuType)
                    }
                }
            } ?: run {
                menuType = MenuType.HOME
                validateMenu(menuType)
            }
        }
    }

    override fun onBackClick() = onBackPressed()

    private fun validateMenu(menuType: MenuType?) {
        validateFragment(menuType ?: MenuType.HOME)
        validateTitle(menuType ?: MenuType.HOME)
    }

    override fun onLogoutClick() {
        CustomAlertDialog(
            context = this,
            title = getString(R.string.logout),
            message = getString(R.string.sure_log_out),
            primaryKey = getString(R.string.logout),
            secondaryKey = getString(R.string.cancel),
            primaryKeyAction = {
                mPref.clear()
                updateFCM(mPref)
                ApplicationData.user = null
                navigateTo(ActivityLogin::class.java)
                finishAffinity()
            }
        ).show()
    }

    override fun onMenuDeleteClick() {
        CustomAlertDialog(
            context = this,
            message = getString(R.string.delete_profile_message),
            secondaryKey = getString(R.string.cancel),
            primaryKey = getString(R.string.delete),
            primaryKeyAction = {
                mViewModel.deleteProfile()
            }
        ).show()
    }

    override fun onMenuIconClick() {
        viewDataBinding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onMenuItemClick(type: MenuType) {
        onMenuIconClick()

        when (type) {
            MenuType.LANGUAGE -> {
                mPref.appLanguage = when (mPref.appLanguage) {
                    AppLanguage.KANNADA.code -> AppLanguage.ENGLISH.code
                    else -> AppLanguage.KANNADA.code
                }

                startActivity(intent)
                finish()
                overridePendingTransition(0, 0)
            }
            MenuType.RATE_US -> redirectTo(BuildConfig.PLAY_STORE_URL + packageName)
            MenuType.HELP -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(BuildConfig.HELP_URL)
                startActivity(intent)
            }
            else -> {
                if (menuType != type) {
                    menuType = type
                    validateFragment(type)
                }
            }
        }
    }

    private fun validateLanguage() {
        viewDataBinding.language = when (mPref.appLanguage) {
            AppLanguage.KANNADA.code -> getString(R.string.lang_english)
            else -> getString(R.string.lang_kannada)
        }
        viewDataBinding.executePendingBindings()
    }

    private fun validateFragment(menuType: MenuType) {
        viewDataBinding.deleteable = false
        viewDataBinding.drawerLayout.closeDrawer(GravityCompat.START)

        val fragment = when (menuType) {
            MenuType.HOME -> HomeFragment()
            MenuType.PROFILE -> {
                viewDataBinding.deleteable = true
                ProfileFragment(mViewModel)
            }
            MenuType.PRODUCT -> ProductFragment()
            MenuType.MyOrder -> ActivityMyOrder()

            MenuType.DRAFT -> DraftFragment()
            MenuType.BLOG -> FragmentWebView(if (mPref.appLanguage == AppLanguage.ENGLISH.code) BuildConfig.BLOG_URL else BuildConfig.BLOG_KN_URL)
            MenuType.TERMS_AND_CONDITIONS -> FragmentWebView(BuildConfig.TERMS_AND_CONDITIONS_URL)
            MenuType.PRIVACY_POLICY -> FragmentWebView(BuildConfig.PRIVACY_POLICY_URL)
            else -> Fragment()
        }

        supportFragmentManager.beginTransaction().replace(
            viewDataBinding.fragmentContainer.id,
            fragment,
            if (fragment is FragmentWebView) "webView" else ""
        ).commit()

        validateTitle(menuType)
        validateFabVisibility(menuType)
        validateFabClickEvent(menuType)
    }

    private fun validateFabClickEvent(menuType: MenuType) {
        viewDataBinding.fabView.setOnClickListener {
            when (menuType) {
                MenuType.HOME -> addProduct()
                MenuType.PRODUCT -> addProduct()
                else -> {
                }
            }
        }
    }

    private fun validateFabVisibility(menuType: MenuType) {
        viewDataBinding.fabView.visibility = when (menuType) {
            MenuType.HOME -> View.VISIBLE
            MenuType.PRODUCT -> View.VISIBLE
            else -> View.GONE
        }
    }

    private fun validateTitle(menuType: MenuType, title: String? = null) {
        if(menuType == MenuType.HELP) return
        viewDataBinding.showAppLogo = menuType == MenuType.HOME

        title?.let {
            viewDataBinding.title = it
            return
        }

        viewDataBinding.title = when (menuType) {
            MenuType.PROFILE -> getString(R.string.profile)
            MenuType.PRODUCT -> getString(R.string.product)
            MenuType.DRAFT -> getString(R.string.drafts)
            MenuType.MyOrder -> getString(R.string.my_order)
            MenuType.BLOG -> getString(R.string.blog)
            MenuType.HELP -> getString(R.string.help)
            MenuType.PRIVACY_POLICY -> getString(R.string.privacy_policy)
            MenuType.TERMS_AND_CONDITIONS -> getString(R.string.terms_conditions)
            else -> ""
        }
    }

    private fun addProduct() {
        navigateTo(AddProductBasicActivity::class.java)
    }
}