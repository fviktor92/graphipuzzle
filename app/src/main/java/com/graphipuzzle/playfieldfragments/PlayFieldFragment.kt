package com.graphipuzzle.playfieldfragments

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.get
import androidx.core.view.setMargins
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.graphipuzzle.PlayField
import com.graphipuzzle.R
import com.graphipuzzle.data.TileData
import com.graphipuzzle.databinding.FragmentPlayFieldBinding
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.abs

const val PLAY_FIELD = "playField"

/**
 * A simple [Fragment] subclass.
 * Use the [PlayFieldFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlayFieldFragment : Fragment(R.layout.fragment_play_field)
{
	private val COLUMN_VALUE_NUMBER_SEPARATOR = System.lineSeparator()
	private val ROW_VALUE_NUMBER_SEPARATOR = " "
	private val ROW_TAG_PREFIX = "row_"
	private val COLUMN_TAG_PREFIX = "column_"
	private val BORDER_TAG = "border"

	private var screenWidth: Int = 0
	private lateinit var playField: PlayField
	private lateinit var fragmentPlayFieldBinding: FragmentPlayFieldBinding

	// Variables used for swiping
	private var minHorizontalSwipeLength = 0
	private var minVerticalSwipeLength = 0
	private var downX: Float = 0.0f
	private var downY: Float = 0.0f
	private lateinit var touchedRowButtons: List<MaterialButton>
	private lateinit var touchedColumnButtons: List<MaterialButton>
	private var firstTouchedButtonColor: Int = 0
	private var currentColumn = 0
	private var currentRow = 0

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
		fragmentPlayFieldBinding =
			DataBindingUtil.inflate(inflater, R.layout.fragment_play_field, container, false)

		initializePlayFieldColumnValuesTable()
		initializePlayFieldRowValuesTable()
		initializePlayFieldTable()
		setHelpButtonOnTouchListener()
		setCompleteButtonOnTouchListener()

		// Inflate the layout for this fragment
		return fragmentPlayFieldBinding.root
	}

	companion object
	{
		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.
		 *
		 * @param playField The [PlayField] from which the fragment is initialized
		 * @return A new instance of fragment PlayFieldSmall.
		 */
		@JvmStatic
		fun newInstance(playField: PlayField) =
			PlayFieldFragment().apply {
				arguments = Bundle().apply {
					putString(PLAY_FIELD, Json.encodeToString(playField))
				}
			}
	}

	/**
	 * Initializes the top [TableLayout] that is supposed to contain the column group values.
	 */
	private fun initializePlayFieldColumnValuesTable()
	{
		val fieldColumns = this.playField.getFieldColumns()
		val row = TableRow(this.context)
		row.layoutParams = TableLayout.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT)

		for (columnIndex in fieldColumns.indices)
		{
			val columnValues: ArrayList<Int> = fieldColumns[columnIndex]
			val columnValueText = createColumnValueTextView(columnValues)
			row.addView(columnValueText)
		}

		this.fragmentPlayFieldBinding.playFieldColumnValuesTable.addView(row)
	}

	/**
	 * Initializes the left [TableLayout] that is supposed to contain the row group values.
	 */
	private fun initializePlayFieldRowValuesTable()
	{
		val fieldRows = this.playField.getFieldRows()

		for (rowIndex in fieldRows.indices)
		{
			val rowValues: ArrayList<Int> = fieldRows[rowIndex]
			val row = TableRow(this.context)
			row.layoutParams = TableLayout.LayoutParams(
				0,
				TableLayout.LayoutParams.WRAP_CONTENT,
				1.0f
			)

			val rowValueText = createRowValueTextView(rowValues)
			row.addView(rowValueText)
			this.fragmentPlayFieldBinding.playFieldRowValuesTable.addView(row)
		}
	}
	
	/**
	 * Creates a TextView that contains the group of values in a column
	 * @return the created TextView with the values as text
	 */
	private fun createColumnValueTextView(columnValues: ArrayList<Int>): TextView
	{
		val columnValueText = TextView(this.context)
		columnValueText.layoutParams = TableRow.LayoutParams(
			TableRow.LayoutParams.WRAP_CONTENT,
			TableRow.LayoutParams.MATCH_PARENT, 1.0f
		)
		columnValueText.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
		columnValueText.textAlignment = TextView.TEXT_ALIGNMENT_GRAVITY
		columnValueText.setTextColor(Color.BLACK)
		columnValueText.textSize = screenWidth * 0.011f
		columnValueText.text = columnValues.joinToString(separator = COLUMN_VALUE_NUMBER_SEPARATOR)
		return columnValueText
	}

	/**
	 * Creates a TextView that contains the group of values in a row
	 * @return the created TextView with the values as text
	 */
	private fun createRowValueTextView(rowValues: ArrayList<Int>): TextView
	{
		val rowValueText = TextView(this.context)
		rowValueText.layoutParams = TableRow.LayoutParams(
			TableRow.LayoutParams.WRAP_CONTENT,
			TableRow.LayoutParams.MATCH_PARENT, 1.0f
		)
		rowValueText.gravity = Gravity.END or Gravity.CENTER_VERTICAL
		rowValueText.textAlignment = TextView.TEXT_ALIGNMENT_GRAVITY
		rowValueText.setTextColor(Color.BLACK)
		rowValueText.textSize = screenWidth * 0.011f
		rowValueText.text = rowValues.joinToString(separator = ROW_VALUE_NUMBER_SEPARATOR)
		return rowValueText
	}

	/**
	 * Initializes tha center [TableLayout] that is supposed to contain the play field buttons.
	 */
	private fun initializePlayFieldTable()
	{
		val playFieldTable = this.fragmentPlayFieldBinding.playFieldTable
		val fieldValues = this.playField.getTileValues()

		addBorderInTable(playFieldTable)

		for (rowIndex in fieldValues.indices)
		{
			val row = createPlayFieldTableRow(rowIndex)
			playFieldTable.addView(row)
			addBorderInRow(row)

			if ((rowIndex + 1) % 5 == 0)
			{
				addBorderInTable(playFieldTable)
			}

			val rowValues: ArrayList<TileData> = fieldValues[rowIndex]
			for (columnIndex in rowValues.indices)
			{
				val fieldButton = createPlayFieldButton(rowIndex, columnIndex)

				row.addView(fieldButton)

				if ((columnIndex + 1) % 5 == 0)
				{
					addBorderInRow(row)
				}
			}
		}
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
		fieldButton.setBackgroundColor(Color.WHITE)
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

				// Color the first touched tile
				setFieldButtonColor(v, rowIndex, columnIndex)
				// Check if any group is completed
				colorColumnTextView(columnIndex)
				colorRowTextView(rowIndex)
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
								colorColumnTextView(this.currentColumn)
								colorRowTextView(rowIndex)
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
								colorColumnTextView(this.currentColumn)
								colorRowTextView(rowIndex)
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
								colorColumnTextView(columnIndex)
								colorRowTextView(this.currentRow)
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
								colorColumnTextView(columnIndex)
								colorRowTextView(this.currentRow)
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Sets the color to the field button to either BLACK or GRAY, depending on the state of the color switch.
	 * If a tile is already in the desired state/color, it resets to WHITE and 0.
	 * @param v The view the touch event has been dispatched to.
	 * @param columnIndex The index of the column where the button is touched.
	 * @param rowIndex The index of the column where the button is touched.
	 */
	private fun setFieldButtonColor(v: View, rowIndex: Int, columnIndex: Int)
	{
		if (this.fragmentPlayFieldBinding.tileColorSwitch.isChecked)
		{
			if (this.firstTouchedButtonColor != Color.BLACK)
			{
				v.backgroundTintList =
					ColorStateList.valueOf(
						ContextCompat.getColor(
							requireContext(),
							R.color.black
						)
					)
				this.playField.setTileState(1, rowIndex, columnIndex)
			} else
			{
				v.backgroundTintList =
					ColorStateList.valueOf(
						ContextCompat.getColor(
							requireContext(),
							R.color.white
						)
					)
				this.playField.setTileState(0, rowIndex, columnIndex)
			}
		} else
		{
			if (this.firstTouchedButtonColor == Color.BLACK || this.firstTouchedButtonColor == Color.WHITE || firstTouchedButtonColor == 0)
			{
				v.backgroundTintList =
					ColorStateList.valueOf(
						ContextCompat.getColor(
							requireContext(),
							R.color.gray
						)
					)
			} else
			{
				v.backgroundTintList =
					ColorStateList.valueOf(
						ContextCompat.getColor(
							requireContext(),
							R.color.white
						)
					)
			}
			this.playField.setTileState(0, rowIndex, columnIndex)
		}
	}

	/**
	 * FIXME: The coloring algorithm could be improved.
	 * Compares the state of the play field with the column values and colors the group value characters if necessary.
	 * @param columnIndex The index of the column where the TextView's character have to be colored
	 */
	private fun colorColumnTextView(columnIndex: Int)
	{
		val columnGroupStates: IntArray = this.playField.getColumnGroupStates()[columnIndex]
		val tableRow = this.fragmentPlayFieldBinding.playFieldColumnValuesTable[0] as TableRow
		val textView = tableRow[columnIndex] as TextView
		val fieldColumn: ArrayList<Int> = this.playField.getFieldColumns()[columnIndex]
		var columnValueTexts: Array<String> =
			textView.text.split(Regex(COLUMN_VALUE_NUMBER_SEPARATOR)).toTypedArray()

		for (i in columnValueTexts.indices)
		{
			val columnGroupState = columnGroupStates[i]
			// If there are more painted groups than the actual number of groups, reset the text to original
			if (fieldColumn.size < columnGroupStates.toList()
					.count { groupState -> groupState != 0 }
			)
			{
				var newText = columnValueTexts.joinToString(COLUMN_VALUE_NUMBER_SEPARATOR)
				textView.text = newText
				break
			} else if (columnGroupState == fieldColumn[i])
			{
				var recoloredGroup = "<font color=#D3D3D3>" + columnValueTexts[i] + "</font>"
				columnValueTexts[i] = recoloredGroup
				var newText = columnValueTexts.joinToString("<br>")
				textView.text = Html.fromHtml(newText)
			} else if (columnGroupState != fieldColumn[i])
			{
				var recoloredGroup = "<font color=#000000>" + columnValueTexts[i] + "</font>"
				columnValueTexts[i] = recoloredGroup
				var newText = columnValueTexts.joinToString("<br>")
				textView.text = Html.fromHtml(newText)
			}
		}
	}

	/**
	 * FIXME: The coloring algorithm could be improved.
	 * Compares the state of the play field with the row values and colors the group value characters if necessary.
	 * @param rowIndex The index of the row where the TextView's character have to be colored
	 */
	private fun colorRowTextView(rowIndex: Int)
	{
		val rowGroupStates: IntArray = this.playField.getRowGroupStates()[rowIndex]
		val tableRow = this.fragmentPlayFieldBinding.playFieldRowValuesTable[rowIndex] as TableRow
		val textView = tableRow[0] as TextView
		val fieldRow: ArrayList<Int> = this.playField.getFieldRows()[rowIndex]
		var rowValueTexts: Array<String> =
			textView.text.split(Regex(ROW_VALUE_NUMBER_SEPARATOR)).toTypedArray()

		for (i in rowValueTexts.indices)
		{
			val rowGroupState = rowGroupStates[i]
			// If there are more painted groups than the actual number of groups, reset the text to original
			if (fieldRow.size < rowGroupStates.toList().count { groupState -> groupState != 0 })
			{
				var newText = rowValueTexts.joinToString(ROW_VALUE_NUMBER_SEPARATOR)
				textView.text = newText
				break
			} else if (rowGroupState == fieldRow[i])
			{
				var recoloredGroup = "<font color=#D3D3D3>" + rowValueTexts[i] + "</font>"
				rowValueTexts[i] = recoloredGroup
				var newText = rowValueTexts.joinToString(ROW_VALUE_NUMBER_SEPARATOR)
				textView.text = Html.fromHtml(newText)
			} else if (rowGroupState != fieldRow[i])
			{
				var recoloredGroup = "<font color=#000000>" + rowValueTexts[i] + "</font>"
				rowValueTexts[i] = recoloredGroup
				var newText = rowValueTexts.joinToString(ROW_VALUE_NUMBER_SEPARATOR)
				textView.text = Html.fromHtml(newText)
			}
		}
	}

	/**
	 * Adds a simple View that is functioning as a thicker border in a TableRow.
	 * @param row the TableRow that receives the border view.
	 */
	private fun addBorderInRow(row: TableRow)
	{
		val border = View(this.context)
		val borderLayoutParams =
			TableRow.LayoutParams(2, TableRow.LayoutParams.MATCH_PARENT)
		border.layoutParams = borderLayoutParams
		border.tag = BORDER_TAG
		border.setBackgroundColor(Color.GRAY)
		row.addView(border)
	}

	/**
	 * Adds a simple View that is functioning as a thicker border in a TableRow.
	 * @param tableLayout the TableLayout that receives the border view.
	 */
	private fun addBorderInTable(tableLayout: TableLayout)
	{
		val border = View(this.context)
		val borderLayoutParams =
			TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2)
		border.layoutParams = borderLayoutParams
		border.tag = BORDER_TAG
		border.setBackgroundColor(Color.GRAY)
		tableLayout.addView(border)
	}

	private fun setCompleteButtonOnTouchListener()
	{
		this.fragmentPlayFieldBinding.completeButton.setOnTouchListener { v, event ->

			if (event.action == MotionEvent.ACTION_DOWN)
			{
				if (this.playField.validate())
				{
					Toast.makeText(
						this.context?.applicationContext,
						"Congrats!",
						Toast.LENGTH_SHORT
					).show()
					val tableBorders =
						this.fragmentPlayFieldBinding.playFieldTable.children.filter { view -> view.tag == BORDER_TAG }
					val rowBorders =
						this.fragmentPlayFieldBinding.playFieldTable.children.filterIsInstance<TableRow>()
							.flatMap { view -> view.children }
							.filter { view -> view.tag == BORDER_TAG }
					(tableBorders + rowBorders).forEach { view -> view.visibility = View.GONE }
					colorPlayFieldTiles()
				} else
				{
					Toast.makeText(
						this.context?.applicationContext,
						"Not yet complete! Keep trying!",
						Toast.LENGTH_SHORT
					).show()
				}
			}
			v?.onTouchEvent(event) ?: true
		}
	}

	private fun setHelpButtonOnTouchListener()
	{
		this.fragmentPlayFieldBinding.helpButton.setOnTouchListener { v, event ->
			val paintableIndices = this.playField.help()
			if (event.action == MotionEvent.ACTION_DOWN)
			{
				if (paintableIndices != Pair(-1, -1))
				{
					this.fragmentPlayFieldBinding.playFieldTable.findViewWithTag<TableRow>(
						ROW_TAG_PREFIX + paintableIndices.first
					).findViewWithTag<MaterialButton>(COLUMN_TAG_PREFIX + paintableIndices.second)
						.backgroundTintList =
						ColorStateList.valueOf(
							ContextCompat.getColor(
								this.requireContext(),
								R.color.black
							)
						)
					this.playField.setTileState(1, paintableIndices.first, paintableIndices.second)
					colorColumnTextView(paintableIndices.second)
					colorRowTextView(paintableIndices.first)
				}
			}

			v?.onTouchEvent(event) ?: true
		}
	}

	private fun colorPlayFieldTiles()
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

				val newColor =
					Color.parseColor(this.playField.getTileValues()[row][col].hexColorCode)
				val colorAnimation =
					ValueAnimator.ofObject(ArgbEvaluator(), originalColor, newColor)
				colorAnimation.duration = 1000L

				colorAnimation.addUpdateListener { animator ->
					materialButton
						.setBackgroundColor(animator.animatedValue as Int)
					materialButton.strokeColor =
						ColorStateList.valueOf(animator.animatedValue as Int)
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
}