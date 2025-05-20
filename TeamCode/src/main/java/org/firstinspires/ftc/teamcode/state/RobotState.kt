package org.firstinspires.ftc.teamcode.state

/**
 * Defines all possible robot states.
 */
enum class RobotState {
    FLOOR_GRAB,
    SPECIMEN_GRAB,
    SPECIMEN_TRANSITION,
    SPECIMEN_DROP,
    HIGH_BASKET,
    SPEC_SCORE,
    NEUTRAL;

    companion object {
        // Default starting state
        val DEFAULT = FLOOR_GRAB
    }
}