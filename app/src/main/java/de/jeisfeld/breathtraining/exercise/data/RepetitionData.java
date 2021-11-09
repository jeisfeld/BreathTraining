package de.jeisfeld.breathtraining.exercise.data;

import java.io.Serializable;

import androidx.annotation.NonNull;

/**
 * Structure for holding information about current repetition.
 */
public class RepetitionData implements Serializable {
	/**
	 * The default serial versio UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Total number of repetitions.
	 */
	private final int mTotalRepetitions;
	/**
	 * Current repetition number.
	 */
	private final int mCurrentRepetition;
	/**
	 * Number of repetitions of current part.
	 */
	private final int mTotalPartRepetitions;
	/**
	 * Current repetition number from part.
	 */
	private final int mCurrentPartRepetition;
	/**
	 * Number of current part.
	 */
	private final int mCurrentPartNumber;

	/**
	 * Default Constructor.
	 */
	public RepetitionData() {
		this(0);
	}

	/**
	 * Constructor.
	 *
	 * @param currentRepetition The current repetition number
	 */
	public RepetitionData(final int currentRepetition) {
		this(currentRepetition, 0);
	}

	/**
	 * Constructor.
	 *
	 * @param currentRepetition The current repetition number
	 * @param totalRepetitions The total number of repetitions
	 */
	public RepetitionData(final int currentRepetition, final int totalRepetitions) {
		this(currentRepetition, totalRepetitions, currentRepetition, totalRepetitions, 1);
	}

	/**
	 * Constructor.
	 *
	 * @param currentRepetition     The current repetition number
	 * @param totalRepetitions      The total number of repetitions
	 * @param currentPartRepetition The repetition number of the current part
	 * @param totalPartRepetitions  The total number of repetitions of the current part
	 * @param currentPartNumber     The name of the current part
	 */
	public RepetitionData(final int currentRepetition, final int totalRepetitions, final int currentPartRepetition,
						  final int totalPartRepetitions, final int currentPartNumber) {
		mCurrentRepetition = currentRepetition;
		mTotalRepetitions = totalRepetitions;
		mCurrentPartRepetition = currentPartRepetition;
		mTotalPartRepetitions = totalPartRepetitions;
		mCurrentPartNumber = currentPartNumber;
	}

	/**
	 * Get the current repetition.
	 *
	 * @return The current repetition.
	 */
	public int getCurrentRepetition() {
		return mCurrentRepetition;
	}

	/**
	 * Get the total repetitions.
	 *
	 * @return The total repetitions.
	 */
	public int getTotalRepetitions() {
		return mTotalRepetitions;
	}

	/**
	 * Get repedition data with updated repetition number.
	 *
	 * @param currentRepetition The updated repetition number
	 * @return The updated repetition data
	 */
	public RepetitionData updateRepetition(final int currentRepetition) {
		return new RepetitionData(currentRepetition, mTotalRepetitions, mCurrentPartRepetition, mTotalPartRepetitions, mCurrentPartNumber);
	}

	@NonNull
	@Override
	public final String toString() {
		if (mCurrentRepetition == 0) {
			return "";
		}
		else if (mTotalPartRepetitions == mTotalRepetitions) {
			return mCurrentRepetition + "/" + mTotalRepetitions;
		}
		else {
			return mCurrentRepetition + "/" + mTotalRepetitions
					+ " (" + mCurrentPartNumber + ": " + mCurrentPartRepetition + "/" + mTotalPartRepetitions + ")";
		}
	}

}
