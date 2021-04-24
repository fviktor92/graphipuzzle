package com.graphipuzzle

import java.util.*

typealias BitSets = List<MutableList<BitSet>>

class PlayFieldSolver
{
	fun isSolveable(rowGroups: Array<IntArray>, columnGroups: Array<IntArray>): Boolean
	{
		val rows = getCandidates(rowGroups)
		val cols = getCandidates(columnGroups)

		do
		{
			val numChanged = reduceMutual(cols, rows)
			if (numChanged == -1)
			{
				return false
			}
		} while (numChanged > 0)

		for (row in rows)
		{
			for (i in 0 until cols.size)
			{
				print(if (row[0][i]) "# " else ". ")
			}
			println()
		}
		println()

		return true
	}

	// collect all possible solutions for the given clues
	private fun getCandidates(datas: Array<IntArray>): BitSets
	{
		val length = datas.size
		val result = mutableListOf<MutableList<BitSet>>()
		for (groups in datas)
		{
			val list = mutableListOf<BitSet>()
			val groupsSum = groups.sum()
			val prep = groups.map { "1".repeat(it) }

			for (r in genSequence(prep, length - groupsSum + 1))
			{
				val bits = r.substring(1).toCharArray()
				val bitset = BitSet(bits.size)
				for (i in bits.indices) bitset[i] = bits[i] == '1'
				list.add(bitset)
			}
			result.add(list)
		}
		return result
	}

	private fun genSequence(ones: List<String>, numZeros: Int): List<String>
	{
		if (ones.isEmpty()) return listOf("0".repeat(numZeros))
		val result = mutableListOf<String>()
		for (x in 1 until numZeros - ones.size + 2)
		{
			val skipOne = ones.drop(1)
			for (tail in genSequence(skipOne, numZeros - x))
			{
				result.add("0".repeat(x) + ones[0] + tail)
			}
		}
		return result
	}

	/* If all the candidates for a row have a value in common for a certain cell,
		then it's the only possible outcome, and all the candidates from the
		corresponding column need to have that value for that cell too. The ones
		that don't, are removed. The same for all columns. It goes back and forth,
		until no more candidates can be removed or a list is empty (failure).
	*/
	private fun reduceMutual(cols: BitSets, rows: BitSets): Int
	{
		val countRemoved1 = reduce(cols, rows)
		if (countRemoved1 == -1) return -1
		val countRemoved2 = reduce(rows, cols)
		if (countRemoved2 == -1) return -1
		return countRemoved1 + countRemoved2
	}

	private fun reduce(a: BitSets, b: BitSets): Int
	{
		var countRemoved = 0
		for (i in a.indices)
		{
			val commonOn = BitSet()
			commonOn[0] = b.size
			val commonOff = BitSet()

			// determine which values all candidates of a[i] have in common
			for (candidate in a[i])
			{
				commonOn.and(candidate)
				commonOff.or(candidate)
			}

			// remove from b[j] all candidates that don't share the forced values
			for (j in b.indices)
			{
				if (b[j].removeIf { cnd ->
						(commonOn[j] && !cnd[i]) ||
								(!commonOff[j] && cnd[i])
					}) countRemoved++
				if (b[j].isEmpty()) return -1
			}
		}
		return countRemoved
	}
}