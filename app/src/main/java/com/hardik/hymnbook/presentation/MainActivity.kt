package com.hardik.hymnbook.presentation

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.imageview.ShapeableImageView
import com.hardik.hymnbook.R
import com.hardik.hymnbook.adapter.IndexAdapter
import com.hardik.hymnbook.common.Constants.BASE_TAG
import com.hardik.hymnbook.common.FragmentSessionUtils
import com.hardik.hymnbook.common.Prefs
import com.hardik.hymnbook.common.Resource
import com.hardik.hymnbook.common.UIOrientationUtils
import com.hardik.hymnbook.common.fadeIn
import com.hardik.hymnbook.common.fadeOut
import com.hardik.hymnbook.databinding.ActivityMainBinding
import com.hardik.hymnbook.presentation.ui.HymnBookItemFragment
import com.hardik.hymnbook.presentation.ui.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = BASE_TAG + MainActivity::class.java.simpleName

    val hymnBookViewModel: HymnBookViewModel by viewModels()

    val UI_ORIENTATION_UTILS: UIOrientationUtils? = UIOrientationUtils.instance
    val fragmentSessionUtils = FragmentSessionUtils.getInstance()

    lateinit var mainActivity: MainActivity
    private lateinit var binding: ActivityMainBinding
    lateinit var progressBar: View

    private lateinit var prefs: SharedPreferences

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var drawer: LinearLayout
    lateinit var sivOpenDrawer: ShapeableImageView

    private lateinit var mainContent: ConstraintLayout // Adjust this based on your main content type
    lateinit var indexAdapter: IndexAdapter

    private var drawerOpened = false
    private val maxSlideOffset = 0.50f // Adjust as needed
    private val maxScaleDown = 0.65f // Adjust as needed

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        mainActivity = this

        prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        var isHistoryTrackerEnabled = prefs.getBoolean("history_tracker", false)
        Log.e(TAG, "onCreate: isSync: $isHistoryTrackerEnabled")

        val screenUi = prefs.getString("system_ui", "default_screen")
        val systemUiValues: Array<String> = resources.getStringArray(R.array.system_ui_values)

        UI_ORIENTATION_UTILS?.setOrientationUnlock(this@MainActivity) // Unlock orientation

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        Log.e(TAG, "onCreate: ${UI_ORIENTATION_UTILS?.hasNotch(context = this)}")
        /*if (UI_ORIENTATION_UTILS?.hasNotch(this) == true && UI_ORIENTATION_UTILS.isCameraInsideDisplay(this)) {
            Log.d(TAG, "DeviceInfo: This device has a notch.")
            Log.d(TAG, "DeviceInfo: The camera is inside the display (notch area).")
            // Get the layout parameters of the view
//            binding.includedDrawerLayout.navHostFragment.setPadding(0, resources.getDimension(R.dimen.padding_14).toInt(), 0, 0)
//            binding.includedDrawerLayout.navigationDrawer.setPadding(0, resources.getDimension(R.dimen.padding_8).toInt(), 0, 0)

        } else {
            Log.d(TAG, "DeviceInfo: This device does not have a notch.")
            Log.d(TAG, "DeviceInfo: The camera is not inside the display (notch area).")
        }*/

        setUpRecyclerView()

        drawerLayout = binding.includedDrawerLayout.drawerLayout
        mainContent = binding.includedDrawerLayout.mainContent
        drawer = binding.includedDrawerLayout.navigationDrawer
        sivOpenDrawer = binding.includedDrawerLayout.sivOpenDrawer
//        progressBar = binding.includedProgressLayout.progressBar
        progressBar = binding.includedDrawerLayout.includedProgressLayout.progressBar


        // set up Screen System UI
        when (screenUi) {
            systemUiValues[0] -> {
                UI_ORIENTATION_UTILS?.setNormalScreenSystemUI(activity = this)// set Default screen
                drawer.setPadding(0, resources.getDimension(R.dimen.padding_4).toInt(), 0, 0)
            }

            systemUiValues[1] -> {
                UI_ORIENTATION_UTILS?.setFullScreenWithStatusBarSystemUI(activity = this) // set Fullscreen + status bar
                drawer.setPadding(0, resources.getDimension(R.dimen.padding_20).toInt(), 0, 0)
            }

            else -> {
                UI_ORIENTATION_UTILS?.setFullScreenSystemUI(activity = this) // set Fullscreen
                drawer.setPadding(0, resources.getDimension(R.dimen.padding_4).toInt(), 0, 0)
            }
        }

        drawerLayout.setScrimColor(resources.getColor(android.R.color.white, theme))
        // Enable the Up button for a more complete drawer experience
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Create a DrawerToggle and set it as the DrawerListener
        drawerToggle = object : ActionBarDrawerToggle(
            this, drawerLayout,
            R.string.drawer_open, R.string.drawer_close
        ) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)

                // Animate scaleX and scaleY based on slideOffset
                val scaleX = 1 - (slideOffset * maxScaleDown)
                val scaleY = 1 - (slideOffset * maxScaleDown)
                animateScale(mainContent, scaleX, scaleY)

                // Animate translationX based on slideOffset
                val offsetX = slideOffset * drawerView.width * maxSlideOffset
                val offsetX1 =
                    if (UI_ORIENTATION_UTILS?.isOrientationLandscape(this@MainActivity) == true) offsetX else offsetX * 1.2f
                animateTranslationX(mainContent, offsetX1)

                // Optionally, change background based on slideOffset
                if (slideOffset > 0) {
                    mainContent.background = ContextCompat.getDrawable(
                        this@MainActivity,
                        R.drawable.background_open
                    )
                    startRotationAnimationClockWise()
                } else {
                    mainContent.background = ContextCompat.getDrawable(
                        this@MainActivity,
                        R.drawable.background_closed
                    )
                    startRotationAnimationAntiClockWise()
                }
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                UI_ORIENTATION_UTILS?.setOrientationLock(this@MainActivity) // Lock orientation

                drawerOpened = true

                // Fade out animation for hiding
                sivOpenDrawer.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction {
                        sivOpenDrawer.visibility = View.GONE
                    }
                    .start()
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)

                UI_ORIENTATION_UTILS?.setOrientationUnlock(this@MainActivity) // Unlock orientation

                drawerOpened = false

                // Make the view visible before starting the animation
                sivOpenDrawer.visibility = View.VISIBLE

                // Fade in animation for showing
                sivOpenDrawer.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start()
            }
        }
        // Add drawerToggle as a drawer listener
        drawerLayout.addDrawerListener(drawerToggle)

        // Synchronize the state of the drawerToggle
        drawerToggle.syncState()


        // Set click listener on tvOpenDrawer to open the drawer
        sivOpenDrawer.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START) // Assuming you want to open from the start (left) side
        }

        // Search for drawer index on Adapter
        binding.includedDrawerLayout.searchView.apply {
            // Set query text listener
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    // Handle query submission here
                    if (!query.isNullOrBlank()) {
                        // Perform search or filtering based on the query
                        indexAdapter.filter.filter(query)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    // Handle query text changes here
                    indexAdapter.filter.filter(newText ?: "")
                    return true
                }
            })

            // Set close listener
            setOnCloseListener {
                // Handle close event here, reset any filters or clear search results
                indexAdapter.filter.filter("")
                false // Return true if you have consumed the event
            }
//            this.findViewById<EditText>(androidx.appcompat.R.id.search_src_text).background = null
        }

        // Initialize OnBackPressedCallback
        val onBackPressedCallback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle back press event
                    Log.i(TAG, "handleOnBackPressed: ")
                    fragmentSessionUtils.handleBackPressed(mainActivity)
                }
            }
        /// Add the callback to the back press dispatcher
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        // get book Index list | set data on recyclerview`s adapter here
        lifecycleScope.launch {
            // Observe the state flow and update UI accordingly
            mainActivity.hymnBookViewModel.hymnbookIndex.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        // Handle success case, update UI with data
                        val hymnBookIndexList = resource.data
                        Log.d(TAG, "onCreate: Success:  $hymnBookIndexList")
                        // Update your UI here
                        mainActivity.indexAdapter.setOriginalList(hymnBookIndexList)
//                        indexAdapter.differ.submitList(hymnBookIndexList)
                        progressBar.fadeOut()

                        // load the default Fragment with data
                        if (savedInstanceState == null) {
                            //region get last open file
                            //endregion
                            val savedFile = Prefs.getString("hymnBookFileName", hymnBookIndexList[hymnBookIndexList.size-1].file)

                            savedFile?.let { switchToHymnBookItemFragment(file = it) }//load list item from the index list

                        } else {
                            Log.e(TAG, "onCreate: " + savedInstanceState)
                        }
                    }

                    is Resource.Error -> {
                        // Handle error case, show error message
                        val errorMessage = resource.message
                        Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                        Log.d(TAG, "onCreate: Error: $errorMessage")
                        // Show error message to user
                        progressBar.fadeOut()
                    }

                    is Resource.Loading -> {
                        // Handle loading state, show progress bar or loading indicator
                        Log.d(TAG, "onCreate: Loading: ")
//                        Toast.makeText(this@MainActivity, "Index Loading", Toast.LENGTH_LONG).show()
                        progressBar.fadeIn()
                    }
                }
            }
        }

        // on item click load data on 'HymnBookItemFragment' and show data
        indexAdapter.setOnItemClickListener {
            if (prefs.getBoolean("drawer_attachment", true)) {// from the settings
                drawerLayout.closeDrawer(GravityCompat.START) // Assuming you want to close to the start (left) side
            }
            isHistoryTrackerEnabled = prefs.getBoolean("history_tracker", false)// from the settings

            //region Store the string in SharedPreferences using your custom Prefs object
            //endregion
            Prefs.putString("hymnBookFileName", it.file)

            fragmentSessionUtils.switchFragment(
                supportFragmentManager,
                HymnBookItemFragment.newInstance(param1FileName = it.file), //  HymnBookItemFragment(),
                isHistoryTrackerEnabled,// if you want to track history fragments than set 'ture'
            )
            supportFragmentManager.popBackStack(
                SettingsFragment::class.java.simpleName,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }

        binding.includedDrawerLayout.llSettings.setOnClickListener {
            if (prefs.getBoolean("drawer_attachment", true)) {// from the settings
                drawerLayout.closeDrawer(GravityCompat.START) // Assuming you want to close to the start (left) side
            }
            fragmentSessionUtils.switchFragment(
                supportFragmentManager,
                SettingsFragment(),
                true,// if you want to track history fragments than set 'ture'
            )
        }

    }

    private fun setUpRecyclerView() {
        indexAdapter = IndexAdapter()
        binding.includedDrawerLayout.recyclerview.apply {
            adapter = indexAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        Log.d(TAG, "onPostCreate: ")
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged: ")
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    // Function to animate scaleX and scaleY
    private fun animateScale(view: View, scaleX: Float, scaleY: Float) {
        Log.d(TAG, "animateScale: ")
        view.animate()
            .scaleX(scaleX)
            .scaleY(scaleY)
            .setDuration(0) // Set duration as needed
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    // Function to animate translationX
    private fun animateTranslationX(view: View, translationX: Float) {
        Log.d(TAG, "animateTranslationX: ")
        view.animate()
            .translationX(
                translationX
//                if(UI_ORIENTATION_UTILS?.isOrientationPortrait(this) == true) translationX * 1.2f else translationX
            )//translationX*1.2f
            .setDuration(0) // Set duration as needed
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    private var rotationAnimator: ObjectAnimator? = null

    // Drawer icon animation function
    private fun startRotationAnimationClockWise() {
        Log.d(TAG, "startRotationAnimationClockWise: ")
        rotationAnimator?.resume()
        rotationAnimator = ObjectAnimator.ofFloat(sivOpenDrawer, View.ROTATION, 0f, 90f)
        rotationAnimator?.apply {
            duration = 200 // Adjust duration as needed
//            repeatCount = ObjectAnimator.INFINITE
            repeatCount = 0
            interpolator = android.view.animation.LinearInterpolator()
            start()
        }
    }

    // Drawer icon animation function
    private fun startRotationAnimationAntiClockWise() {
        Log.d(TAG, "startRotationAnimationAntiClockWise: ")
        rotationAnimator?.resume()
        rotationAnimator = ObjectAnimator.ofFloat(sivOpenDrawer, View.ROTATION, 90f, 0f)
        rotationAnimator?.apply {
            duration = 200 // Adjust duration as needed
            //repeatCount = ObjectAnimator.INFINITE
            repeatCount = 0
            interpolator = android.view.animation.LinearInterpolator()
            start()
        }
    }

    private val hideHandler = Handler(Looper.myLooper()!!)

    // default loading data from the file
    private fun switchToHymnBookItemFragment(file: String) {
        Log.d(TAG, "switchToHymnBookItemFragment: ")

        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        if (currentFragment !is HymnBookItemFragment) { fragmentSessionUtils.switchFragment(
            supportFragmentManager,
            HymnBookItemFragment.newInstance(param1FileName = file),//HymnBookItemFragment(),
            false,
        )
        }
        hideHandler.run {
            postDelayed(Runnable {
                mainActivity.sivOpenDrawer.visibility = View.VISIBLE
            }, AUTO_DELAY_MILLIS.toLong())
        }
    }
}
private const val AUTO_DELAY_MILLIS = 3000
