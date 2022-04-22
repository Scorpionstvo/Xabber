package com.xabber.presentation.application.activity

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LifecycleOwner
import com.xabber.R
import com.xabber.data.dto.ChatDto
import com.xabber.databinding.ActivityApplicationBinding
import com.xabber.presentation.application.contract.ApplicationNavigator
import com.xabber.presentation.application.contract.ApplicationToolbarChanger
import com.xabber.presentation.application.fragments.account.AccountFragment
import com.xabber.presentation.application.fragments.calls.CallsFragment
import com.xabber.presentation.application.fragments.chat.ChatFragment
import com.xabber.presentation.application.fragments.chat.NewGroupFragment
import com.xabber.presentation.application.fragments.chat.SpecialNotificationsFragment
import com.xabber.presentation.application.fragments.contacts.ContactsFragment
import com.xabber.presentation.application.fragments.contacts.EditContactFragment
import com.xabber.presentation.application.fragments.contacts.NewContactFragment
import com.xabber.presentation.application.fragments.discover.DiscoverFragment
import com.xabber.presentation.application.fragments.message.MessageFragment
import com.xabber.presentation.application.fragments.message.NewChatFragment
import com.xabber.presentation.application.fragments.settings.SettingsFragment
import com.xabber.presentation.onboarding.contract.ResultListener


class ApplicationActivity : AppCompatActivity(), ApplicationNavigator {

    private var binding: ActivityApplicationBinding? = null
    lateinit var userName: String
    private val activeFragment: Fragment
        get() = supportFragmentManager.findFragmentById(R.id.application_container)!!

    private val fragmentListner = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            updateUi()
        }
    }

    private fun updateUi() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)

            binding?.bottomNavBar?.isVisible = false
            binding?.shadow?.isVisible = false

        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)

            binding?.bottomNavBar?.isVisible = true
            binding?.shadow?.isVisible = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplicationBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        userName = intent.getStringExtra("key").toString()
        if (savedInstanceState == null) {
            launchFragment(ChatFragment.newInstance(""))
        }
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListner, false)
        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        binding!!.bottomNavBar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.chats -> {
                    if (activeFragment !is ChatFragment) {
                        launchFragment(ChatFragment.newInstance(""))
                    } else {
                        Toast.makeText(this, "Button press", Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.calls -> if (activeFragment !is CallsFragment) launchFragment(CallsFragment())
                R.id.contacts -> if (activeFragment !is ContactsFragment) launchFragment(
                    ContactsFragment()
                )
                R.id.discover -> if (activeFragment !is DiscoverFragment) launchFragment(
                    DiscoverFragment()
                )
                R.id.settings -> if (activeFragment !is SettingsFragment) launchFragment(
                    SettingsFragment()
                )
            }
            true
        }
    }

    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.appearance, R.animator.disappearance)
            .replace(R.id.application_container, fragment).commit()
    }

    private fun launchFragmentInStack(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.application_container, fragment).addToBackStack(null).commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun goBack() {
        onBackPressed()
    }


    override fun showMessage(chat: ChatDto) {
        launchFragmentInStack(MessageFragment())
    }

    override fun showAccount() {
        launchFragmentInStack(AccountFragment())
    }

    override fun showNewChat() {
        launchFragmentInStack(NewChatFragment())
    }

    override fun showNewContact() {
        launchFragmentInStack(NewContactFragment())
    }

    override fun showNewGroup(incognito: Boolean) {
        launchFragmentInStack(NewGroupFragment.newInstance(incognito))
    }

    override fun showSpecialNotificationSettings() {
        launchFragmentInStack(SpecialNotificationsFragment())
    }

    override fun showEditContact() {
        launchFragmentInStack(EditContactFragment())
    }

    override fun <T : Parcelable> showResult(result: T) {

    }

    override fun <T : Parcelable> giveResult(
        clazz: Class<T>,
        owner: LifecycleOwner,
        listener: ResultListener<T>
    ) {

    }

    override fun setTitle(titleResId: Int) {
        //      binding?.tvTitle?.setText(titleResId)
    }


}
