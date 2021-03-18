package com.graphipuzzle.fragments

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.get
import androidx.core.view.setMargins
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.google.android.material.button.MaterialButton
import com.graphipuzzle.PlayField
import com.graphipuzzle.R
import com.graphipuzzle.data.TileData
import com.graphipuzzle.databinding.FragmentPlayFieldBinding
import com.graphipuzzle.util.SoundPoolUtil
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.abs


const val PLAY_FIELD = "playField"

open class PlayFieldFragment : Fragment(R.layout.fragment_play_field)
{
	private val COLUMN_VALUE_NUMBER_SEPARATOR: String = "<br>"
	private val ROW_VALUE_NUMBER_SEPARATOR: String = " "
	protected val ROW_TAG_PREFIX: String = "row_"
	protected val COLUMN_TAG_PREFIX: String = "column_"
	private val BORDER_TAG: String = "border"

	private var screenWidth: Int = 0
	protected lateinit var playField: PlayField
	protected lateinit var fragmentPlayFieldBinding: FragmentPlayFieldBinding

	// Variables used for swiping
	private var minHorizontalSwipeLength = 0
	private var minVerticalSwipeLength = 0
	private var downX: Float = 0.0f
	private var downY: Float = 0.0f
	private lateinit var touchedRowButtons: List<MaterialButton>
	private lateinit var touchedColumnButtons: List<MaterialButton>
	protected var firstTouchedButtonColor: Int = 0
	private var currentColumn = 0
	private var currentRow = 0

	// The play field is completed or not
	private var isComplete = false

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		arguments?.let {
			playField = Json.decodeFromString(it.getString(PLAY_FIELD)!!)
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View?
	{
		screenWidth = getScreenWidthInPixels()
		this.fragmentPlayFieldBinding =
			DataBindingUtil.inflate(inflater, R.layout.fragment_play_field, container, false)

		(activity as AppCompatActivity).supportActionBar?.setIcon(R.drawable.ic_baseline_arrow_back_24)

		loadPlayField()

		setHasOptionsMenu(true)

		return this.fragmentPlayFieldBinding.root
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
	{
		super.onCreateOptionsMenu(menu, inflater)
		inflater?.inflate(R.menu.navigation_bar_menu, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean
	{
		return when (item.itemId)
		{
			R.id.settingsFragment ->
			{
				NavigationUI.onNavDestinationSelected(item!!, this.findNavController())
				true
			}
			else ->
			{
				// Save the play field state when user navigates back
				storePlayFieldStateInSharedPreferences(this.playField)
				super.onOptionsItemSelected(item)
			}
		}
	}

	override fun onStop()
	{
		super.onStop()
		// Save the play field state when the fragment is stopped, only if it is not yet completed
		if (!this.isComplete)
		{
			storePlayFieldStateInSharedPreferences(this.playField)
		}
	}

	/**
	 * Saves the state of the play field in SharedPreferences with [PLAY_FIELD] key, to be able to continue from a previous state.
	 * @param playField The [PlayField] object to store. Passing null for removes any existing play field object assigned to [PLAY_FIELD] key.
	 */
	private fun storePlayFieldStateInSharedPreferences(playField: PlayField?)
	{
		val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())
		if (playField != null)
		{
			with(sharedPreferences.edit()) {
				putString(PLAY_FIELD, Json.encodeToString(playField))
				apply()
			}
		} else
		{
			with(sharedPreferences.edit()) {
				remove(PLAY_FIELD)
				apply()
			}
		}

	}

	protected fun loadPlayField()
	{
		// FIXME: Maybe this could be implemented prettier
		// Displaying the loading fragment
		GlobalScope.launch {
			withContext(Dispatchers.Main) {
				val bundle = bundleOf(LOADING_TEXT to "Loading play field...")
				childFragmentManager.commit {
					setReorderingAllowed(true)
					add(R.id.loading_fragment, LoadingFragment::class.java, bundle)
					addToBackStack(LEVEL_PACKS_FRAGMENT)
				}
				this@PlayFieldFragment.fragmentPlayFieldBinding.loadingFragment.visibility = View.VISIBLE
				// Disabling user interaction with the UI while loading
				this@PlayFieldFragment.requireActivity().window.setFlags(
					WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
					WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				)
				// Disabling back button
				this@PlayFieldFragment.fragmentPlayFieldBinding.loadingFragment.rootView.setOnKeyListener { v, keyCode, event ->
					if (keyCode == KeyEvent.KEYCODE_BACK)
					{
						return@setOnKeyListener true
					}
					return@setOnKeyListener false
				}
			}
		}

		// FIXME: Maybe this could be implemented prettier
		val deferredCreatedPlayFieldTableViews = GlobalScope.async { createPlayFieldTableViews() }

		val deferredColumnValuesTableRow = GlobalScope.async { createPlayFieldColumnValuesTableRow() }

		val deferredRowValuesTableRows = GlobalScope.async { createPlayFieldRowValuesTableRows() }

		val deferredSetTileColorSwitchOnTouchListener = GlobalScope.async { setTileColorSwitchOnTouchListener() }

		val deferredSetHelpButtonOnTouchListener = GlobalScope.async { setHelpButtonOnTouchListener() }

		val deferredSetTileCounterText = GlobalScope.async {
			this@PlayFieldFragment.fragmentPlayFieldBinding.coloredTilesCounterText.textSize = screenWidth * 0.015f
			createTileCounterText()
		}

		GlobalScope.launch {
			withContext(Dispatchers.Main) {
				this@PlayFieldFragment.fragmentPlayFieldBinding.coloredTilesCounterText.text = deferredSetTileCounterText.await()
				deferredSetHelpButtonOnTouchListener.await()
				deferredSetTileColorSwitchOnTouchListener.await()

				// Initialize Play Field Table
				deferredCreatedPlayFieldTableViews.await().forEach {
					this@PlayFieldFragment.fragmentPlayFieldBinding.playFieldTable.addView(it)
				}
				this@PlayFieldFragment.fragmentPlayFieldBinding.playFieldColumnValuesTable.addView(
					deferredColumnValuesTableRow.await()
				)
				deferredRowValuesTableRows.await().forEach {
					this@PlayFieldFragment.fragmentPlayFieldBinding.playFieldRowValuesTable.addView(it)
				}

				// Enabling Back button
				this@PlayFieldFragment.fragmentPlayFieldBinding.loadingFragment.rootView.setOnKeyListener { v, keyCode, event ->
					if (keyCode == KeyEvent.KEYCODE_BACK)
					{
						return@setOnKeyListener false
					}
					return@setOnKeyListener true
				}
				// Enabling user interaction with the UI when loading is finished
				this@PlayFieldFragment.requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
				this@PlayFieldFragment.fragmentPlayFieldBinding.loadingFragment.visibility = View.GONE
			}
		}
	}

	/**
	 * Creates the [TableRow] for the top [TableLayout] that is supposed to contain the column group values.
	 */
	private fun createPlayFieldColumnValuesTableRow(): TableRow
	{
		val fieldColumns = this.playField.getFieldColumns()
		val row = TableRow(this.context)
		row.layoutParams = TableLayout.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT)

		for (columnIndex in fieldColumns.indices)
		{
			val columnValueText = createColumnValueTextView(columnIndex)
			row.addView(columnValueText)
		}
		return row
	}

	/**
	 * Creates the [TableRows] for the left [TableLayout] that is supposed to contain the row group values.
	 */
	private fun createPlayFieldRowValuesTableRows(): MutableList<TableRow>
	{
		val fieldRows = this.playField.getFieldRows()
		val tableRows = mutableListOf<TableRow>()
		for (rowIndex in fieldRows.indices)
		{
			val row = TableRow(this.context)
			row.layoutParams = TableLayout.LayoutParams(
				0,
				TableLayout.LayoutParams.WRAP_CONTENT,
				1.0f
			)

			val rowValueText = createRowValueTextView(rowIndex)
			row.addView(rowValueText)
			tableRows.add(row)
		}
		return tableRows
	}

	/**
	 * Creates a TextView that contains the group of values in a column
	 * @return the created TextView with the values as text
	 */
	private fun createColumnValueTextView(columnIndex: Int): TextView
	{
		val columnValues: ArrayList<Int> = this.playField.getFieldColumns()[columnIndex]
		val tileStates: IntArray = this.playField.getTileStates().map { array -> array[columnIndex] }.toIntArray()
		val groupStates: IntArray = this.playField.getColumnGroupStates()[columnIndex]
		val columnValueText = TextView(this.context)
		columnValueText.layoutParams = TableRow.LayoutParams(
			TableRow.LayoutParams.WRAP_CONTENT,
			TableRow.LayoutParams.MATCH_PARENT, 1.0f
		)
		columnValueText.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
		columnValueText.textAlignment = TextView.TEXT_ALIGNMENT_GRAVITY
		columnValueText.setTextColor(Color.BLACK)
		columnValueText.textSize = screenWidth * 0.011f
		columnValueText.text = createColoredText(
			columnValues.map { it.toString() }.toTypedArray(),
			COLUMN_VALUE_NUMBER_SEPARATOR,
			groupStates,
			tileStates,
			columnValues
		)
		return columnValueText
	}

	/**
	 * Creates a TextView that contains the group of values in a row
	 * @return the created TextView with the values as text
	 */
	private fun createRowValueTextView(rowIndex: Int): TextView
	{
		val rowValues: ArrayList<Int> = this.playField.getFieldRows()[rowIndex]
		val groupStates: IntArray = this.playField.getRowGroupStates()[rowIndex]
		val tileStates: IntArray = this.playField.getTileStates()[rowIndex]
		val rowValueText = TextView(this.context)
		rowValueText.layoutParams = TableRow.LayoutParams(
			TableRow.LayoutParams.WRAP_CONTENT,
			TableRow.LayoutParams.MATCH_PARENT, 1.0f
		)
		rowValueText.gravity = Gravity.END or Gravity.CENTER_VERTICAL
		rowValueText.textAlignment = TextView.TEXT_ALIGNMENT_GRAVITY
		rowValueText.setTextColor(Color.BLACK)
		rowValueText.textSize = screenWidth * 0.011f
		rowValueText.text = createColoredText(
			rowValues.map { it.toString() }.toTypedArray(),
			ROW_VALUE_NUMBER_SEPARATOR,
			groupStates,
			tileStates,
			rowValues
		)
		return rowValueText
	}

	/**
	 * Creates the views for the center [TableLayout] that is supposed to contain the play field buttons.
	 */
	private fun createPlayFieldTableViews(): MutableList<View>
	{
		val fieldValues = this.playField.getTileDatas()
		val tableViews: MutableList<View> = mutableListOf<View>()
		// Add top border to table
		tableViews.add(createBorder(TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 4)))

		for (rowIndex in fieldValues.indices)
		{
			val row = createPlayFieldTableRow(rowIndex)
			// Add left side border to table row
			row.addView(createBorder(TableRow.LayoutParams(4, TableRow.LayoutParams.MATCH_PARENT)))

			val rowValues: ArrayList<TileData> = fieldValues[rowIndex]
			for (columnIndex in rowValues.indices)
			{
				val fieldButton = createPlayFieldButton(rowIndex, columnIndex)

				row.addView(fieldButton)

				// Add border after every 5th button
				if ((columnIndex + 1) % 5 == 0)
				{
					row.addView(createBorder(TableRow.LayoutParams(4, TableRow.LayoutParams.MATCH_PARENT)))
				}
			}

			tableViews.add(row)

			// Add border after every 5th row
			if ((rowIndex + 1) % 5 == 0)
			{
				tableViews.add(createBorder(TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 4)))
			}
		}

		return tableViews
	}

	/**
	 * Creates an empty TableRow that is supposed to contain the Buttons in the play field table.
	 * @return the created empty TableRow
	 */
	private fun createPlayFieldTableRow(rowIndex: Int): TableRow
	{
		val row = TableRow(this.context)
		row.tag = "$ROW_TAG_PREFIX$rowIndex"
		row.layoutParams = TableLayout.LayoutParams(
			TableLayout.LayoutParams.WRAP_CONTENT,
			TableLayout.LayoutParams.WRAP_CONTENT,
			1.0f
		)
		return row
	}

	/**
	 * Creates a play field button with an on touch listener set.
	 * @return a MaterialButton
	 */
	private fun createPlayFieldButton(rowIndex: Int, columnIndex: Int): MaterialButton
	{
		val tileState = this.playField.getTileState(rowIndex, columnIndex)
		val fieldButton =
			MaterialButton(this.requireContext(), null, R.attr.materialButtonOutlinedStyle)
		val layoutParams = TableRow.LayoutParams(
			TableRow.LayoutParams.WRAP_CONTENT,
			TableRow.LayoutParams.MATCH_PARENT, 1.0f
		)
		layoutParams.setMargins(0)
		fieldButton.tag = "$COLUMN_TAG_PREFIX$columnIndex"
		fieldButton.layoutParams = layoutParams
		fieldButton.setPadding(0, 0, 0, 0)
		fieldButton.insetTop = 0
		fieldButton.insetBottom = 0
		fieldButton.textAlignment = Button.TEXT_ALIGNMENT_CENTER
		fieldButton.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.dark_gray))
		fieldButton.strokeWidth = 1

		when (tileState)
		{
			0 ->
			{
				fieldButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
			}
			1 ->
			{
				fieldButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black))
			}
			2 ->
			{
				fieldButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray))
			}
		}

		fieldButton.cornerRadius = 0

		fieldButton.setOnTouchListener { v, event ->
			fieldButtonOnTouchEvent(v, event, columnIndex, rowIndex)
			v?.onTouchEvent(event) ?: true
		}

		return fieldButton
	}

	/**
	 * Handles the events that happen on an on touch event. Sets the color of the button and the state of the play field tile value.
	 * Sets the row and column value colors according to the tile coloring.
	 * <b>Validates that the playfield is complete if the painted and paintable tiles count is equal</b>
	 * @param v The view the touch event has been dispatched to.
	 * @param event The MotionEvent object containing full information about the event.
	 * @param columnIndex The index of the column where the button is touched.
	 * @param rowIndex The index of the column where the button is touched.
	 */
	private fun fieldButtonOnTouchEvent(
		v: View,
		event: MotionEvent,
		columnIndex: Int,
		rowIndex: Int
	)
	{
		when (event.action)
		{
			MotionEvent.ACTION_DOWN ->
			{
				// Reset the values on a new touch
				this.currentColumn = columnIndex
				this.currentRow = rowIndex
				this.downX = event.x
				this.downY = event.y
				this.touchedRowButtons =
					this.fragmentPlayFieldBinding.playFieldTable.findViewWithTag<TableRow>(
						ROW_TAG_PREFIX + rowIndex
					).children.toList().filterIsInstance<MaterialButton>()
				this.touchedColumnButtons =
					this.fragmentPlayFieldBinding.playFieldTable.children.toList()
						.filterIsInstance<TableRow>()
						.map { row -> row.findViewWithTag(COLUMN_TAG_PREFIX + columnIndex) }
				this.firstTouchedButtonColor = v.backgroundTintList!!.getColorForState(
					intArrayOf(android.R.attr.state_enabled),
					0
				)

				// The minimal swipe length should be equal to the size of a tile
				this.minHorizontalSwipeLength = v.width
				this.minVerticalSwipeLength = v.height

				setFieldButtonColor(v, rowIndex, columnIndex)
				performOnTileTouchActions(rowIndex, columnIndex)
			}

			// Swipe is initiated
			MotionEvent.ACTION_MOVE ->
			{
				val moveX = event.x
				val moveY = event.y
				val deltaX = this.downX - moveX
				val deltaY = this.downY - moveY

				// Swipe horizontal
				if (abs(deltaX) > abs(deltaY) && this.currentRow == rowIndex)
				{
					if (abs(deltaX) >= this.minHorizontalSwipeLength)
					{
						this.minHorizontalSwipeLength += v.width
						// Right Swipe
						if (deltaX < 0)
						{
							this.currentColumn += 1
							if (this.currentColumn < this.touchedRowButtons.size)
							{
								setFieldButtonColor(
									this.touchedRowButtons[this.currentColumn],
									rowIndex,
									this.currentColumn
								)
								performOnTileTouchActions(rowIndex, this.currentColumn)
							}
						}
						// Left Swipe
						if (deltaX > 0)
						{
							this.currentColumn -= 1
							if (this.currentColumn >= 0)
							{
								setFieldButtonColor(
									this.touchedRowButtons[this.currentColumn],
									rowIndex,
									this.currentColumn
								)
								performOnTileTouchActions(rowIndex, this.currentColumn)
							}
						}
					}
				}
				// Swipe vertical
				else
				{
					if (abs(deltaY) >= this.minVerticalSwipeLength && this.currentColumn == columnIndex)
					{
						this.minVerticalSwipeLength += v.height
						// Down swipe
						if (deltaY < 0)
						{
							this.currentRow += 1
							if (this.currentRow < this.touchedColumnButtons.size)
							{
								setFieldButtonColor(
									this.touchedColumnButtons[this.currentRow],
									this.currentRow,
									columnIndex
								)
								performOnTileTouchActions(this.currentRow, columnIndex)
							}
						}
						// Up swipe
						if (deltaY > 0)
						{
							this.currentRow -= 1
							if (this.currentRow >= 0)
							{
								setFieldButtonColor(
									this.touchedColumnButtons[this.currentRow],
									this.currentRow,
									columnIndex
								)
								performOnTileTouchActions(this.currentRow, columnIndex)
							}
						}
					}
				}
			}
		}
	}

	protected open fun performOnTileTouchActions(rowIndex: Int, columnIndex: Int)
	{
		// Play a click sound
		SoundPoolUtil.getInstance(requireContext()).playSound(R.raw.tile_paint_sound)
		// Check if any group is completed
		colorColumnTextView(columnIndex)
		colorRowTextView(rowIndex)
		// Increment the tile counter
		this.fragmentPlayFieldBinding.coloredTilesCounterText.text = createTileCounterText()
		// Check if playfield is complete
		validatePlayFieldIsComplete()
	}

	/**
	 * Sets the color to the field button to either BLACK or GRAY, depending on the state of the color switch.
	 * If a tile is already in the desired state/color, it resets to WHITE and 0.
	 * @param v The view the touch event has been dispatched to.
	 * @param columnIndex The index of the column where the button is touched.
	 * @param rowIndex The index of the column where the button is touched.
	 */
	protected open fun setFieldButtonColor(v: View, rowIndex: Int, columnIndex: Int)
	{
		val actualFieldButtonColor = v.backgroundTintList!!.getColorForState(
			intArrayOf(android.R.attr.state_enabled),
			0
		)
		val tileColorSwitchCheckedColor = fragmentPlayFieldBinding.tileColorSwitch.thumbTintList!!.getColorForState(
			intArrayOf(android.R.attr.state_checked), Color.BLACK
		)
		val tileColorSwitchUncheckedColor = fragmentPlayFieldBinding.tileColorSwitch.thumbTintList!!.getColorForState(
			intArrayOf(-android.R.attr.state_checked), Color.GRAY
		)

		if (this.fragmentPlayFieldBinding.tileColorSwitch.isChecked)
		{
			if ((this.firstTouchedButtonColor == Color.WHITE || firstTouchedButtonColor == Color.TRANSPARENT) && actualFieldButtonColor != Color.GRAY)
			{
				v.backgroundTintList = ColorStateList.valueOf(tileColorSwitchCheckedColor)
				this.playField.setTileState(1, rowIndex, columnIndex)
			} else if (this.firstTouchedButtonColor == Color.GRAY)
			{
				v.backgroundTintList = ColorStateList.valueOf(tileColorSwitchCheckedColor)
				this.playField.setTileState(1, rowIndex, columnIndex)
			} else if (this.firstTouchedButtonColor == Color.BLACK && actualFieldButtonColor == Color.BLACK)
			{
				v.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
				this.playField.setTileState(0, rowIndex, columnIndex)
			}
		} else
		{
			if ((this.firstTouchedButtonColor == Color.WHITE || firstTouchedButtonColor == Color.TRANSPARENT) && actualFieldButtonColor != Color.BLACK)
			{
				v.backgroundTintList = ColorStateList.valueOf(tileColorSwitchUncheckedColor)
				this.playField.setTileState(2, rowIndex, columnIndex)
			} else if (this.firstTouchedButtonColor == Color.BLACK)
			{
				v.backgroundTintList = ColorStateList.valueOf(tileColorSwitchUncheckedColor)
				this.playField.setTileState(2, rowIndex, columnIndex)
			} else if (this.firstTouchedButtonColor == Color.GRAY && actualFieldButtonColor == Color.GRAY)
			{
				v.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
				this.playField.setTileState(0, rowIndex, columnIndex)
			}
		}
	}

	/**
	 * Compares the state of the play field with the column values and colors the group value characters if necessary.
	 * @param columnIndex The index of the column where the TextView's character have to be colored
	 */
	private fun colorColumnTextView(columnIndex: Int)
	{
		val columnTileStates: IntArray =
			this.playField.getTileStates().map { array -> array[columnIndex] }.toIntArray()
		val columnGroupStates: IntArray = this.playField.getColumnGroupStates()[columnIndex]
		val fieldColumn: ArrayList<Int> = this.playField.getFieldColumns()[columnIndex]
		val tableRow = this.fragmentPlayFieldBinding.playFieldColumnValuesTable[0] as TableRow
		val textView = tableRow[columnIndex] as TextView
		val actualText: Array<String> = textView.text.split(Regex("\n")).toTypedArray()
		textView.text = createColoredText(
			actualText,
			COLUMN_VALUE_NUMBER_SEPARATOR,
			columnGroupStates,
			columnTileStates,
			fieldColumn
		)
	}

	/**
	 * Compares the state of the play field with the row values and colors the group value characters if necessary.
	 * @param rowIndex The index of the row where the TextView's character have to be colored
	 */
	private fun colorRowTextView(rowIndex: Int)
	{
		val rowTileStates: IntArray = this.playField.getTileStates()[rowIndex]
		val rowGroupStates: IntArray = this.playField.getRowGroupStates()[rowIndex]
		val fieldRow: ArrayList<Int> = this.playField.getFieldRows()[rowIndex]
		val tableRow = this.fragmentPlayFieldBinding.playFieldRowValuesTable[rowIndex] as TableRow
		val textView = tableRow[0] as TextView
		val actualText: Array<String> = textView.text.split(Regex(ROW_VALUE_NUMBER_SEPARATOR)).toTypedArray()
		textView.text = createColoredText(actualText, ROW_VALUE_NUMBER_SEPARATOR, rowGroupStates, rowTileStates, fieldRow)

	}

	/**
	 * Creates a colored text from the given text, based on the given groups states (either row or column group states)
	 * @param textArray The text that has to be colored
	 * @param separator The separator in the text. Should be either [ROW_VALUE_NUMBER_SEPARATOR] or [COLUMN_VALUE_NUMBER_SEPARATOR]
	 * @param groupStates The actual states of groups in a given row or column.
	 * @param tileStates The actual states of tiles in a given row or column.
	 * @param fieldValues The expected values in the given row or column
	 * @return A text that is colored
	 */
	private fun createColoredText(
		textArray: Array<String>,
		separator: String,
		groupStates: IntArray,
		tileStates: IntArray,
		fieldValues: ArrayList<Int>
	): CharSequence
	{
		var newText = ""

		for (i in textArray.indices)
		{
			val groupState = groupStates[i]
			// If the number of black painted tiles is more than the expected
			if (tileStates.count { tile -> tile == 1 } > fieldValues.sum())
			{
				newText = textArray.joinToString(separator)
				break
			} else if (fieldValues.size > 0 && groupState == fieldValues[i])
			{
				var recoloredGroup = "<font color=#D3D3D3>" + textArray[i] + "</font>"
				textArray[i] = recoloredGroup
				newText = textArray.joinToString(separator)
			} else if (fieldValues.size > 0 && groupState != fieldValues[i])
			{
				var recoloredGroup = "<font color=#000000>" + textArray[i] + "</font>"
				textArray[i] = recoloredGroup
				newText = textArray.joinToString(separator)
			}
		}
		return Html.fromHtml(newText)
	}

	/**
	 * Creates a simple View that is functioning as a thicker border in a TableLayout.
	 * @param borderLayoutParams The layout params of the border
	 */
	private fun createBorder(borderLayoutParams: LinearLayout.LayoutParams): View
	{
		val border = View(this.context)
		border.layoutParams = borderLayoutParams
		border.tag = BORDER_TAG
		border.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_gray))
		return border
	}

	/**
	 * Validates that the playfield is complete, if the number of painted tiles is equal to the required
	 * @return if the playfield is complete or not.
	 */
	private fun validatePlayFieldIsComplete(): Boolean
	{
		this.isComplete = false
		if (this.playField.getPaintableTilesCount() == this.playField.getPaintedTilesCount())
		{
			this.isComplete = this.playField.validate()
			if (this.isComplete)
			{
				// Display the playfield level name
				Toast.makeText(
					this.context?.applicationContext,
					this.playField.getName(),
					Toast.LENGTH_SHORT
				).show()

				// Hide the table borders
				val tableBorders =
					this.fragmentPlayFieldBinding.playFieldTable.children.filter { view -> view.tag == BORDER_TAG }
				val rowBorders =
					this.fragmentPlayFieldBinding.playFieldTable.children.filterIsInstance<TableRow>()
						.flatMap { view -> view.children }
						.filter { view -> view.tag == BORDER_TAG }
				(tableBorders + rowBorders).forEach { view -> view.visibility = View.GONE }

				// Color the tiles
				colorPlayFieldTiles()

				// Hide the game UI elements and disable playfield table interactivity
				this.fragmentPlayFieldBinding.coloredTilesCounterText.visibility = View.INVISIBLE
				this.fragmentPlayFieldBinding.tileColorSwitch.visibility = View.INVISIBLE
				this.fragmentPlayFieldBinding.helpButton.visibility = View.INVISIBLE
				this.fragmentPlayFieldBinding.nextLevelButton.visibility = View.VISIBLE
				this.fragmentPlayFieldBinding.nextLevelButton.setOnClickListener { view: View ->
					SoundPoolUtil.getInstance(requireContext()).playSound(R.raw.button_sound)
					view.findNavController().navigate(R.id.action_playFieldFragment_to_levelsFragment)
				}

				setEnabledDisabledRecursively(this.fragmentPlayFieldBinding.playFieldTable, false)

				// Remove existing play field state from shared preferences to prevent continuing the already completed level
				storePlayFieldStateInSharedPreferences(null)
			} else
			{
				Toast.makeText(
					this.context?.applicationContext,
					"Not yet complete! Keep trying!",
					Toast.LENGTH_SHORT
				).show()
			}
		}

		return this.isComplete
	}

	private fun createTileCounterText(): String
	{
		return this.playField.getPaintedTilesCount()
			.toString() + " / " + this.playField.getPaintableTilesCount()
	}

	private fun setHelpButtonOnTouchListener()
	{
		this.fragmentPlayFieldBinding.helpButton.setOnClickListener {
			val paintableIndices = this.playField.help()
			if (paintableIndices != Pair(-1, -1))
			{
				this.fragmentPlayFieldBinding.playFieldTable.findViewWithTag<TableRow>(
					ROW_TAG_PREFIX + paintableIndices.first
				).findViewWithTag<MaterialButton>(COLUMN_TAG_PREFIX + paintableIndices.second).backgroundTintList =
					ColorStateList.valueOf(
						ContextCompat.getColor(
							this.requireContext(),
							R.color.black
						)
					)
				this.playField.setTileState(1, paintableIndices.first, paintableIndices.second)
				performOnTileTouchActions(paintableIndices.first, paintableIndices.second)
			}
		}
	}

	private fun setTileColorSwitchOnTouchListener()
	{
		this.fragmentPlayFieldBinding.tileColorSwitch.setOnClickListener {
			SoundPoolUtil.getInstance(requireContext()).playSound(R.raw.switch_sound)
		}
	}

	protected open fun colorPlayFieldTiles()
	{
		for (row in 0 until this.playField.getFieldSize())
		{
			val tableRow = this.fragmentPlayFieldBinding.playFieldTable
				.findViewWithTag<TableRow>(ROW_TAG_PREFIX + row)
			for (col in 0 until this.playField.getFieldSize())
			{
				val materialButton =
					tableRow.findViewWithTag<MaterialButton>(COLUMN_TAG_PREFIX + col)
				var originalColor = materialButton.backgroundTintList!!.getColorForState(
					intArrayOf(android.R.attr.state_enabled),
					0
				)

				var newColor: Int = Color.parseColor(this.playField.getTileDatas()[row][col].hexColorCode)

				val colorAnimation =
					ValueAnimator.ofObject(ArgbEvaluator(), originalColor, newColor)
				colorAnimation.duration = 1000L

				colorAnimation.addUpdateListener { animator ->
					materialButton.backgroundTintList = ColorStateList.valueOf(animator.animatedValue as Int)
					materialButton.strokeColor = ColorStateList.valueOf(animator.animatedValue as Int)
				}
				colorAnimation.start()
			}
		}
	}

	private fun getScreenWidthInPixels(): Int
	{
		val displayMetrics = DisplayMetrics()
		activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
		return displayMetrics.widthPixels
	}

	private fun setEnabledDisabledRecursively(viewGroup: ViewGroup, enabled: Boolean)
	{
		val childCount = viewGroup.childCount
		for (i in 0 until childCount)
		{
			val child = viewGroup.getChildAt(i)
			child.isEnabled = enabled
			if (child is ViewGroup)
			{
				setEnabledDisabledRecursively(child, enabled)
			}
		}
	}
}