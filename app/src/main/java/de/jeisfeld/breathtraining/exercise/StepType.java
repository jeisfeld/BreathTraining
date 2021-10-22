package de.jeisfeld.breathtraining.exercise;

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
	RELAX(R.string.text_relax);

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

}
