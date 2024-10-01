package com.hardik.hymnbook.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hardik.hymnbook.R
import com.hardik.hymnbook.common.Constants.BASE_TAG
import com.hardik.hymnbook.common.Resource
import com.hardik.hymnbook.common.fadeIn
import com.hardik.hymnbook.common.fadeOut
import com.hardik.hymnbook.databinding.FragmentHymnBookItemBinding
import com.hardik.hymnbook.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [HymnBookItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class HymnBookItemFragment() : Fragment() {
    private val TAG = BASE_TAG + HymnBookItemFragment::class.java.simpleName
    private var param1FileName: String? = null

    private var _binding: FragmentHymnBookItemBinding?= null
    private val binding get() = _binding!!

    private lateinit var mainActivity: MainActivity

    private val hymnBookItemViewModel: HymnBookItemViewModel by viewModels()

    private lateinit var textView: TextView
    private lateinit var scrollView: ScrollView

    private lateinit var progressBar: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        arguments?.let {
            param1FileName = it.getString(ARG_PARAM1)
        }
        Log.d(TAG, "onCreate: $param1FileName")
        mainActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: ")
//        return inflater.inflate(R.layout.fragment_hymn_book_item, container, false)
        _binding = FragmentHymnBookItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")

        progressBar = mainActivity.progressBar

        textView = view.findViewById(R.id.tv_data)
        scrollView = view.findViewById(R.id.scrollView)

        val scaleGestureDetector = ScaleGestureDetector(requireContext(), ScaleListener())

        // Set up touch listener for ScrollView
        textView.setOnTouchListener { _, event ->
            // Handle touch events for scaling
            scaleGestureDetector.onTouchEvent(event)

            true
        }
//        hymnBookItemViewModel.getBookItems("श्री गीत गोविन्द.json")
        param1FileName?.also {
            hymnBookItemViewModel.getBookItems(it)
            Log.e(TAG, "onViewCreated: $it")
        }

        // get book data | set data on textview here
        lifecycleScope.launch {
            hymnBookItemViewModel.bookItem.collect { resources ->
                when (resources) {
                    is Resource.Success -> {
                        // Handle success case, update UI with data
                        val bookItem = resources.data
                        Log.d(TAG, "getBookItemData: Success: \n$bookItem")
                        textView.text =
                            "\n\n${bookItem.data} \n\n\n"
                        progressBar.fadeOut()
                    }

                    is Resource.Error -> {
                        // Handle error case, show error message
                        val errorMessage = resources.message
                        Log.d(TAG, "getBookItemData: Error: $errorMessage")
                        progressBar.fadeOut()
                    }

                    is Resource.Loading -> {
                        // Handle loading state, show progress bar or loading indicator
                        Log.d(TAG, "getBookItemData: Loading...")
                        progressBar.fadeIn()
//                        progressBar.setConstraintsForIncludedLayout(progressBar.id)
                    }
                }
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1FileName: String) =
            HymnBookItemFragment().apply {
                Log.d(TAG, "newInstance: $param1FileName")
                arguments = Bundle().apply { putString(ARG_PARAM1, param1FileName) }
            }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        private var baseTextSize = 18f
        private var zoomedOutTextSize = baseTextSize / 1.5f
        private var zoomedInTextSize = baseTextSize * 12.0f

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            // Calculate the scale factor from the detector
            val scaleFactor = detector.scaleFactor

            // Calculate new text size based on the current scale factor
            val currentTextSize =
                textView.textSize / textView.context.resources.displayMetrics.scaledDensity
            val newSize = currentTextSize * scaleFactor

            // Limit the text size within a reasonable range
            val minTextSize = zoomedOutTextSize
            val maxTextSize = zoomedInTextSize
            val clampedSize = newSize.coerceIn(minTextSize, maxTextSize)

            // Set the new text size for the TextView
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, clampedSize)

            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            // Final adjustments if needed
            // For example, you might want to round the final textSize to avoid very small changes
            val finalTextSize =
                textView.textSize / textView.context.resources.displayMetrics.scaledDensity
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, finalTextSize)
        }
    }

    /*
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f
    private var baseTextSize = 18f
    private var zoomedOutTextSize = baseTextSize / 3.0f
    private var zoomedInTextSize = baseTextSize * 10.0f  // Increase text size when zoomed in
    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        scaleFactor *= detector.scaleFactor

        // Limit the scale factor
        scaleFactor = scaleFactor.coerceIn(1.0f, 10.0f)

        // Adjust text size based on the scale factor
        val newSize = when {
            scaleFactor < 1.0f -> zoomedOutTextSize * scaleFactor
            scaleFactor > 1.0f -> zoomedInTextSize * scaleFactor
            else -> baseTextSize
        }

        textView.textSize = newSize

        return true
    }
    }
    */
}