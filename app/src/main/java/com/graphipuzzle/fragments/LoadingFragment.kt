package com.graphipuzzle.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.graphipuzzle.R
import com.graphipuzzle.databinding.FragmentLoadingBinding

const val LOADING_TEXT = "loadingText"

/**
 * A simple [Fragment] subclass.
 * Use the [LoadingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoadingFragment : Fragment()
{
	private var loadingText: String? = null

	private lateinit var fragmentLoadingBinding: FragmentLoadingBinding

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		arguments?.let {
			loadingText = it.getString(LOADING_TEXT)
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View?
	{
		this.fragmentLoadingBinding =
			DataBindingUtil.inflate(inflater, R.layout.fragment_loading, container, false)

		this.fragmentLoadingBinding.progressIndicatorText.text = this.loadingText

		// Inflate the layout for this fragment
		return this.fragmentLoadingBinding.root
	}

	companion object
	{
		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.
		 *
		 * @param loadingText The text that should be displayed with the progress indicator
		 * @return A new instance of fragment LoadingFragment.
		 */
		@JvmStatic
		fun newInstance(loadingText: String) =
			LoadingFragment().apply {
				arguments = Bundle().apply {
					putString(LOADING_TEXT, loadingText)
				}
			}
	}
}