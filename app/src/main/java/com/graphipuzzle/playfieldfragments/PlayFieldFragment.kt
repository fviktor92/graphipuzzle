package com.graphipuzzle.playfieldfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import com.graphipuzzle.PlayField
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val PLAY_FIELD_FRAGMENT_ID = "playFieldFragmentId"

/**
 * A simple [Fragment] subclass.
 * Use the [PlayFieldFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlayFieldFragment : Fragment()
{
	private var playFieldFragmentId: Int = 0

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		arguments?.let {
			playFieldFragmentId = it.getInt(PLAY_FIELD_FRAGMENT_ID)
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View?
	{
		// Inflate the layout for this fragment
		return inflater.inflate(playFieldFragmentId, container, false)
	}

	companion object
	{
		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.
		 *
		 * @param playFieldFragmentId The id of the playfield fragment xml
		 * @return A new instance of fragment PlayFieldSmall.
		 */
		@JvmStatic
		fun newInstance(playField: PlayField, playFieldFragmentId: Int) =
			PlayFieldFragment().apply {
				arguments = Bundle().apply {
					putInt(PLAY_FIELD_FRAGMENT_ID, playFieldFragmentId)
				}
			}
	}
}