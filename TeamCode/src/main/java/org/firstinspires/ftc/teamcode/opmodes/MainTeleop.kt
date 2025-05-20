package org.firstinspires.ftc.teamcode.opmodes

import com.acmerobotics.dashboard.config.Config

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.state.RobotState

/**
 * Main TeleOp mode for the robot
 */
@Config

@TeleOp(name = "MAIN", group = "Linear Opmode")
class MainTeleOp : BaseOpMode() {
    // Configuration variables
    companion object {
        @JvmField var offset = 0.15
        @JvmField var limelightAlign = true
    }

    override fun runOpMode() {
        // Initialize the OpMode
        initOpMode()

        // Wait for the driver to press PLAY
        waitForStart()

        // Start the limelight vision system
        robotHardware.visionSubsystem.start()

        // Main control loop
        while (opModeIsActive()) {
            // Update gamepad state tracking
            updateGamepadState()

            // Drive control
            robotHardware.driveSubsystem.drive()

            // Check for state transitions from gamepad input
            checkStateTransitions()

            // Handle grabber buttons
            handleGrabberControl()

            // Handle vision alignment and rotation
            handleVisionAndRotation()

            // Update the current robot state
            stateManager.update()

            // Handle slide manual control based on current state
            handleSlideControl()

            // Check for recovery mode toggle
            checkRecoveryToggle()

            // Check for speed limit toggle
            checkSpeedLimitToggle()

            // Handle lift manual control in recovery mode
            handleLiftRecoveryControl()

            // Update indicator light
            updateIndicatorLight()

            // Update lift and slide controllers if not in recovery mode
            if (!robotHardware.isRecovering()) {
                robotHardware.liftSubsystem.update()
            }

            // Update telemetry
            updateTelemetry()
        }
    }

    /**
     * Handle manual slide control with triggers
     */
    private fun handleSlideControl() {
        val currentState = stateManager.getCurrentState()
        val armGamepad = robotHardware.getArmGamepad() ?: return
        val slideSubsystem = robotHardware.slideSubsystem

        // Different position limits based on state
        val positionLimit = if (currentState == RobotState.FLOOR_GRAB) 350 else 2300

        slideSubsystem.handleManualControl(armGamepad, positionLimit)
    }

    /**
     * Handle lift control in recovery mode
     */
    private fun handleLiftRecoveryControl() {
        if (robotHardware.isRecovering()) {
            val driveGamepad = robotHardware.getDriveGamepad() ?: return
            robotHardware.liftSubsystem.handleManualControl(driveGamepad)
        }
    }
}