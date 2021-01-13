package com.graphipuzzle.playfieldfragments

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
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

	private lateinit var playField: PlayField
	private lateinit var fragmentPlayFieldBinding: FragmentPlayFieldBinding

	private lateinit var gestureDetector: GestureDetector
	private lateinit var gestureListener: View.OnTouchListener

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
		fragmentPlayFieldBinding =
			DataBindingUtil.inflate(inflater, R.layout.fragment_play_field, container, false)

			initializePlayFieldColumnValuesTable()
			initializePlayFieldRowValuesTable()
			initializePlayFieldTable()
			setHelpButtonOnTouchListener()
			setCompleteButtonOnTouchListener()

			val minSwipeLength = fragmentPlayFieldBinding.playFieldTable.findViewWithTag<TableRow>(
				ROW_TAG_PREFIX + 0).findViewWithTag<MaterialButton>(COLUMN_TAG_PREFIX + 0).height

			gestureDetector = GestureDetector(this@PlayFieldFragment.context, MyGestureDetector(minSwipeLength))
			gestureListener = View.OnTouchListener { v, event ->
				gestureDetector.onTouchEvent(event)
			}


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
		columnValueText.textSize = 10.0f
		columnValueText.text = columnValues.joinToString(separator = COLUMN_VALUE_NUMBER_SEPARATOR)
		return columnValueText
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
		rowValueText.textSize = 10.0f
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

	private fun createPlayFieldButton(rowIndex: Int, columnIndex: Int): MaterialButton
	{
		val fieldButton =
			MaterialButton(this.requireContext(), null, R.attr.materialButtonOutlinedStyle)
		val layoutParams = TableRow.LayoutParams(
			TableRow.LayoutParams.WRAP_CONTENT,
			TableRow.LayoutParams.WRAP_CONTENT, 1.0f
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
			gestureListener.onTouch(v, event)
			when (event?.action)
			{
				MotionEvent.ACTION_DOWN ->
				{
					colorTileBlackOrGray(v, rowIndex, columnIndex)
				}

				MotionEvent.ACTION_UP ->
				{
					colorColumnTextView(columnIndex)
					colorRowTextView(rowIndex)
				}
			}
			v?.onTouchEvent(event) ?: true
		}

		return fieldButton
	}

	private fun colorTileBlackOrGray(v: View, rowIndex: Int, columnIndex: Int)
	{
		if (fragmentPlayFieldBinding.tileColorSwitch.isChecked)
		{
			v?.backgroundTintList =
				ColorStateList.valueOf(
					ContextCompat.getColor(
						requireContext(),
						R.color.black
					)
				)
			playField.setTileState(1, rowIndex, columnIndex)
		} else
		{
			v?.backgroundTintList =
				ColorStateList.valueOf(
					ContextCompat.getColor(
						requireContext(),
						R.color.gray
					)
				)
			playField.setTileState(0, rowIndex, columnIndex)
		}
	}

	private fun colorColumnTextView(columnIndex: Int)
	{
		val columnGroupStates: IntArray = this.playField.getColumnGroupStates()[columnIndex]
		val tableRow = this.fragmentPlayFieldBinding.playFieldColumnValuesTable[0] as TableRow
		val textView = tableRow[columnIndex] as TextView
		var columnValues: Array<String> =
			textView.text.split(Regex(COLUMN_VALUE_NUMBER_SEPARATOR)).toTypedArray()

		for (i in columnValues.indices)
		{
			val columnGroupState = columnGroupStates[i]
			if (columnValues.size < columnGroupStates.toList()
					.count { groupState -> groupState != 0 }
			)
			{
				var newText = columnValues.joinToString(COLUMN_VALUE_NUMBER_SEPARATOR)
				textView.text = newText
				break
			} else if (columnGroupState == columnValues[i].toInt())
			{
				var recoloredGroup = "<font color=#D3D3D3>" + columnValues[i] + "</font>"
				columnValues[i] = recoloredGroup
				var newText = columnValues.joinToString("<br>")
				textView.text = Html.fromHtml(newText)
			} else if (columnGroupState != columnValues[i].toInt())
			{
				var recoloredGroup = "<font color=#000000>" + columnValues[i] + "</font>"
				columnValues[i] = recoloredGroup
				var newText = columnValues.joinToString("<br>")
				textView.text = Html.fromHtml(newText)
			}
		}
	}

	private fun colorRowTextView(rowIndex: Int)
	{
		val rowGroupStates: IntArray = this.playField.getRowGroupStates()[rowIndex]
		val tableRow = this.fragmentPlayFieldBinding.playFieldRowValuesTable[rowIndex] as TableRow
		val textView = tableRow[0] as TextView
		var rowValues: Array<String> =
			textView.text.split(Regex(ROW_VALUE_NUMBER_SEPARATOR)).toTypedArray()

		for (i in rowValues.indices)
		{
			val rowGroupState = rowGroupStates[i]
			if (rowValues.size < rowGroupStates.toList().count { groupState -> groupState != 0 })
			{
				var newText = rowValues.joinToString(ROW_VALUE_NUMBER_SEPARATOR)
				textView.text = newText
				break
			} else if (rowGroupState == rowValues[i].toInt())
			{
				var recoloredGroup = "<font color=#D3D3D3>" + rowValues[i] + "</font>"
				rowValues[i] = recoloredGroup
				var newText = rowValues.joinToString(ROW_VALUE_NUMBER_SEPARATOR)
				textView.text = Html.fromHtml(newText)
			} else if (rowGroupState != rowValues[i].toInt())
			{
				var recoloredGroup = "<font color=#000000>" + rowValues[i] + "</font>"
				rowValues[i] = recoloredGroup
				var newText = rowValues.joinToString(ROW_VALUE_NUMBER_SEPARATOR)
				textView.text = Html.fromHtml(newText)
			}
		}
	}

	private fun addBorderInRow(row: TableRow)
	{
		val border = View(this.context)
		val borderLayoutParams =
			TableRow.LayoutParams(2, TableRow.LayoutParams.MATCH_PARENT)
		border.layoutParams = borderLayoutParams
		border.setBackgroundColor(Color.GRAY)
		row.addView(border)
	}

	private fun addBorderInTable(tableLayout: TableLayout)
	{
		val border = View(this.context)
		val borderLayoutParams =
			TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2)
		border.layoutParams = borderLayoutParams
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
				}
				colorAnimation.start()
			}
		}
	}

	inner class MyGestureDetector(var minSwipeLength: Int) : GestureDetector.SimpleOnGestureListener()
	{
		override fun onFling(
			e1: MotionEvent?,
			e2: MotionEvent?,
			velocityX: Float,
			velocityY: Float
		): Boolean
		{
			val deltaX = e1!!.x - e2!!.x
			val deltaY = e1!!.y - e2!!.y

			// Swipe horizontal
			if (abs(deltaX) > abs(deltaY))
			{
				if (abs(deltaX) >= 95)
				{
					// Right Swipe
					if (deltaX < 0)
					{
						Toast.makeText(
							this@PlayFieldFragment.context,
							"Right Swipe $minSwipeLength",
							Toast.LENGTH_SHORT
						).show()
					}
					// Left Swipe
					if (deltaX > 0)
					{
						Toast.makeText(
							this@PlayFieldFragment.context,
							"Left Swipe",
							Toast.LENGTH_SHORT
						).show()
					}
				}
			}
			// Swipe vertical
			else
			{
				if (abs(deltaY) >= 95)
				{
					// Down swipe
					if (deltaY < 0)
					{
						Toast.makeText(
							this@PlayFieldFragment.context,
							"Down Swipe",
							Toast.LENGTH_SHORT
						).show()
					}
					// Up swipe
					if (deltaY > 0)
					{
						Toast.makeText(
							this@PlayFieldFragment.context,
							"Up Swipe",
							Toast.LENGTH_SHORT
						).show()
					}
				}
			}

			return false
		}

		override fun onDown(e: MotionEvent?): Boolean
		{
			return true
		}
	}
}