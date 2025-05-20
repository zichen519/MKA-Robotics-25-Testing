package org.firstinspires.ftc.teamcode.state

import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.subsystems.GrabberSubsystem
import org.firstinspires.ftc.teamcode.subsystems.LiftSubsystem
import org.firstinspires.ftc.teamcode.subsystems.SlideSubsystem

/**
 * Manages the robot's state machine and transitions
 */
class StateManager(
    private val liftSubsystem: LiftSubsystem,
    private val slideSubsystem: SlideSubsystem,
    private val grabberSubsystem: GrabberSubsystem,
    private val telemetry: Telemetry? = null
) {
    // Current state
    private var currentState: RobotState = RobotState.FLOOR_GRAB

    // Substate handling
    private var subState = 0
    private var subStateDone = false

    // Timer for state transitions
    private val timer = ElapsedTime()

    /**
     * Initialize the state manager
     */
    fun initialize() {
        currentState = RobotState.FLOOR_GRAB
        subState = 0
        subStateDone = false
        timer.reset()
    }

    /**
     * Set the robot state
     */
    fun setState(state: RobotState) {
        // Only reset substates if we're changing state
        if (state != currentState) {
            subState = 0
            subStateDone = false
            timer.reset()
            currentState = state
        }
    }

    /**
     * Get the current robot state
     */
    fun getCurrentState(): RobotState {
        return currentState
    }

    /**
     * Update the state machine
     */
    fun update() {
        when (currentState) {
            RobotState.NEUTRAL -> updateNeutralState()
            RobotState.FLOOR_GRAB -> updateFloorGrabState()
            RobotState.SPECIMEN_GRAB -> updateSpecimenGrabState()
            RobotState.SPECIMEN_TRANSITION -> updateSpecimenTransitionState()
            RobotState.SPECIMEN_DROP -> updateSpecimenDropState()
            RobotState.HIGH_BASKET -> updateHighBasketState()
            RobotState.SPEC_SCORE -> updateSpecScoreState()
        }

        telemetry?.addData("State", currentState)
        telemetry?.addData("subState", subState)
        telemetry?.addData("time", timer.seconds())
    }

    /**
     * Updates the neutral state
     */
    private fun updateNeutralState() {
        if (!subStateDone) {
            when (subState) {
                0 -> {
                    // Reset slide
                    slideSubsystem.setTarget(0)
                    timer.reset()
                    subState++
                }
                1 -> {
                    // Move lift
                    if (timer.seconds() > 1) {
                        liftSubsystem.setTarget(-50)
                        timer.reset()
                        subState++
                    }
                }
                2 -> {
                    // Move elbow
                    if (timer.seconds() > 1) {
                        grabberSubsystem.setPositions(
                            elbow1Position = 0.0,
                            elbow2Position = 0.0
                        )
                        timer.reset()
                        subState++
                    }
                }
                3 -> {
                    // Move wrist
                    if (timer.seconds() > 1) {
                        grabberSubsystem.setPositions(
                            wristPosition = 1.0,
                            rotatePosition = 0.0
                        )
                        subState = 0
                        subStateDone = true
                    }
                }
            }
        }
    }

    /**
     * Updates the floor grab state
     */
    private fun updateFloorGrabState() {
        if (slideSubsystem.getCurrentPosition() < 200) {
            liftSubsystem.setTarget(-1660)
            slideSubsystem.setTarget(0)
        }

        if (grabberSubsystem.isGrabbing()) {
            val isDone = grabberSubsystem.updateFloorGrab()
            if (isDone) {
                slideSubsystem.setTarget(25)
            }
        } else {
            grabberSubsystem.configureForFloorGrab()
        }

        if (!subStateDone) {
            timer.reset()
            subState = 0
            subStateDone = true
        }
    }

    /**
     * Updates the specimen grab state
     */
    private fun updateSpecimenGrabState() {
        if (!subStateDone) {
            when (subState) {
                0 -> {
                    // Reset slide and lift
                    slideSubsystem.setTarget(0)
                    liftSubsystem.setTarget(-50)
                    timer.reset()
                    subState++
                }
                1 -> {
                    // Position servos
                    if (timer.seconds() > 0.2) {
                        grabberSubsystem.configureForSpecimenGrab()
                        subState = 0
                        subStateDone = true
                    }
                }
            }
        }
    }

    /**
     * Updates the specimen transition state
     */
    private fun updateSpecimenTransitionState() {
        if (!subStateDone) {
            when (subState) {
                0 -> {
                    slideSubsystem.setTarget(0)
                    liftSubsystem.setTarget(-50)
                    grabberSubsystem.setPositions(wristPosition = 1.0)
                    timer.reset()
                    subState++
                }
                1 -> {
                    if (timer.seconds() > 0.4) {
                        grabberSubsystem.configureForSpecimenTransition()
                        subState = 0
                        subStateDone = true
                    }
                }
            }
        }
    }

    /**
     * Updates the specimen drop state
     */
    private fun updateSpecimenDropState() {
        if (!subStateDone) {
            grabberSubsystem.configureForSpecimenDrop()
            timer.reset()
            subState = 0
            subStateDone = true
        }
    }

    /**
     * Updates the high basket state
     */
    private fun updateHighBasketState() {
        if (!subStateDone && slideSubsystem.getCurrentPosition() < 200) {
            // All in one step
            liftSubsystem.setTarget(-50)
            grabberSubsystem.configureForHighBasket()
            timer.reset()
            subState = 0
            subStateDone = true
        }
    }

    /**
     * Updates the spec score state
     */
    private fun updateSpecScoreState() {
        // Implementation for SPEC_SCORE state
        // This was commented out in the original code
    }
}