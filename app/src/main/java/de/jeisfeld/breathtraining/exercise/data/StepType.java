package de.jeisfeld.breathtraining.exercise.data;

import de.jeisfeld.breathtraining.R;

/**
 * The type of an exercise step.
 */
public enum StepType {
	/**
	 * Inhale.
	 */
	INHALE(R.string.text_inhale),
	/**
	 * Exhale.
	 */
	EXHALE(R.string.text_exhale),
	/**
	 * Hold.
	 */
	HOLD(R.string.text_hold),
	/**
	 * Relax.
	 */
	RELAX(R.string.text_relax),
	/**
	 * Continue inhale after hold.
	 */
	CONTINUE_INHALE(R.string.text_inhale),
	/**
	 * Continue exhale after hold.
	 */
	CONTINUE_EXHALE(R.string.text_exhale);

	/**
	 * The text resource for displaying the stepType.
	 */
	private final int mTextResource;

	/**
	 * Constructor.
	 *
	 * @param textResource The text resource for displaying the stepType.
	 */
	StepType(final int textResource) {
		mTextResource = textResource;
	}

	/**
	 * Get the String resource for display.
	 *
	 * @return The string resource.
	 */
	public int getDisplayResource() {
		return mTextResource;
	}

	/**
	 * Get information if this is hold step.
	 *
	 * @return true if hold step.
	 */
	public boolean isHold() {
		return this == HOLD;
	}

	/**
	 * Get information if this is continue step.
	 *
	 * @return true if continue step.
	 */
	public boolean isContinue() {
		return this == CONTINUE_INHALE || this == CONTINUE_EXHALE;
	}

	/**
	 * Get the continue type of a certain step type.
	 *
	 * @return The continue type.
	 */
	public StepType getContinueType() {
		return this == INHALE ? CONTINUE_INHALE : this == EXHALE ? CONTINUE_EXHALE : null;
	}

}
