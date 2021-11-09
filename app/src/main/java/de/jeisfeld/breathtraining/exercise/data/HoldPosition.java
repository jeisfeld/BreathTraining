package de.jeisfeld.breathtraining.exercise.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Enumeration for the hold positions.
 */
public enum HoldPosition {
	/**
	 * Hold only at the end.
	 */
	ONLY_END,
	/**
	 * One intermittent hold.
	 */
	ONE_INTERMITTENT,
	/**
	 * Two intermittent holds.
	 */
	TWO_INTERMITTENT,
	/**
	 * Variable intermittend holds.
	 */
	VARIABLE;

	/**
	 * A random stream of doubles.
	 */
	private static final transient Iterator<Double> DOUBLE_ITERATOR = new Random().doubles().iterator();

	/**
	 * Apply the hold variation to a duration.
	 *
	 * @param duration      The duration.
	 * @param holdVariation The hold variation to be applied.
	 * @return The duration with hold variation.
	 */
	private static long applyHoldVariation(final long duration, final double holdVariation) {
		return (long) (duration * (1 + (DOUBLE_ITERATOR.next() * 2 - 1) * holdVariation));
	}

	/**
	 * Split a breath into parts, including hold.
	 *
	 * @param originalStep  The original breath.
	 * @param holdDuration  The hold duration.
	 * @param holdVariation The hold variation.
	 * @return The split step.
	 */
	public List<ExerciseStep> applyHold(final ExerciseStep originalStep, final long holdDuration, final double holdVariation) {
		List<ExerciseStep> result = new ArrayList<>();
		long stepDuration;
		long breakDuration;
		RepetitionData repetition = originalStep.getRepetition();
		StepType originalType = originalStep.getStepType();
		StepType continueType = originalType.getContinueType();

		switch (this) {
		case ONLY_END:
			result.add(originalStep);
			result.add(new ExerciseStep(StepType.HOLD, applyHoldVariation(holdDuration, holdVariation), repetition));
			return result;
		case ONE_INTERMITTENT:
			stepDuration = originalStep.getDuration() / 2;
			breakDuration = holdDuration / 2;
			result.add(new ExerciseStep(originalType, stepDuration, originalStep.getSoundDuration(), repetition));
			result.add(new ExerciseStep(StepType.HOLD, applyHoldVariation(breakDuration, holdVariation), repetition));
			result.add(new ExerciseStep(continueType, stepDuration, originalStep.getSoundDuration(),
					repetition));
			result.add(new ExerciseStep(StepType.HOLD, applyHoldVariation(breakDuration, holdVariation), repetition));
			return result;
		case TWO_INTERMITTENT:
			stepDuration = originalStep.getDuration() / 3; // MAGIC_NUMBER
			breakDuration = holdDuration / 3; // MAGIC_NUMBER
			result.add(new ExerciseStep(originalType, stepDuration, originalStep.getSoundDuration(), repetition));
			result.add(new ExerciseStep(StepType.HOLD, applyHoldVariation(breakDuration, holdVariation), repetition));
			result.add(new ExerciseStep(continueType, stepDuration, originalStep.getSoundDuration(),
					repetition));
			result.add(new ExerciseStep(StepType.HOLD, applyHoldVariation(breakDuration, holdVariation), repetition));
			result.add(new ExerciseStep(continueType, stepDuration, originalStep.getSoundDuration(),
					repetition));
			result.add(new ExerciseStep(StepType.HOLD, applyHoldVariation(breakDuration, holdVariation), repetition));
			return result;
		case VARIABLE:
			long totalHoldDuration = applyHoldVariation(holdDuration, holdVariation);
			if (originalStep.getDuration() <= 2000 || totalHoldDuration <= 2000) { // MAGIC_NUMBER
				// at least 1s between holds
				result.add(originalStep);
				return result;
			}

			// determine number of intermediate holds - for both hold and breath, number of parts should be smaller than average duration in s.
			int maxIntermediateHoldCount1 = (int) Math.sqrt(originalStep.getDuration() / 1000.0 - 2); // MAGIC_NUMBER
			int maxIntermediateHoldCount2 = (int) Math.sqrt(totalHoldDuration / 1000.0 - 2); // MAGIC_NUMBER
			int intermediateHoldCount = (int) (DOUBLE_ITERATOR.next() * (Math.min(maxIntermediateHoldCount1, maxIntermediateHoldCount2) + 1));

			List<Long> breathDurations = splitInParts(originalStep.getDuration(), intermediateHoldCount);
			List<Long> holdDurations = splitInParts(totalHoldDuration, intermediateHoldCount);

			for (int i = 0; i <= intermediateHoldCount; i++) {
				result.add(
						new ExerciseStep(i == 0 ? originalType : continueType, breathDurations.get(i), originalStep.getSoundDuration(), repetition));
				result.add(new ExerciseStep(StepType.HOLD, holdDurations.get(i), repetition));
			}
			return result;
		default:
			result.add(originalStep);
			return result;
		}
	}

	/**
	 * Split a number of milliseconds into parts of length at least 1 second.
	 *
	 * @param duration The full duration in milliseconds.
	 * @param numberOfParts The number of parts.
	 * @return The split.
	 */
	private static List<Long> splitInParts(final long duration, final int numberOfParts) {
		List<Double> intermediateSplitPositions = new ArrayList<>();
		intermediateSplitPositions.add(1.0);
		intermediateSplitPositions.add(0.0);
		for (int i = 0; i < numberOfParts; i++) {
			intermediateSplitPositions.add(DOUBLE_ITERATOR.next());
		}
		intermediateSplitPositions.sort(Double::compare);
		List<Long> splitDurations = new ArrayList<>();
		for (int i = 0; i <= numberOfParts; i++) {
			splitDurations.add(Math.round(1000 + (intermediateSplitPositions.get(i + 1) - intermediateSplitPositions.get(i)) // MAGIC_NUMBER
					* (duration - numberOfParts * 1000 - 1000))); // MAGIC_NUMBER
		}
		return splitDurations;
	}

}
