package org.firstinspires.ftc.teamcode.opmodes

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.hardware.RobotHardware
import org.firstinspires.ftc.teamcode.state.RobotState
import org.firstinspires.ftc.teamcode.state.StateManager
import org.firstinspires.ftc.teamcode.util.GamepadManager

/**
 * Base OpMode with shared functionality
 */
abstract class BaseOpMode : LinearOpMode() {
    // Hardware
    protected lateinit var robotHardware: RobotHardware

    // State manager
    protected lateinit var stateManager: StateManager

    // Gamepad managers
    protected lateinit var driveGamepadManager: GamepadManager
    protected lateinit var armGamepadManager: GamepadManager

    // Gamepad state
    protected val currDriveGamepad = Gamepad()
    protected val prevDriveGamepad = Gamepad()
    protected val currArmGamepad = Gamepad()
    protected val prevArmGamepad = Gamepad()

    // Telemetry manager for Panels


    /**
     * Initialize the OpMode
     */
    protected fun initOpMode() {
        // Configure telemetry
        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        telemetry.addData("Status", "Initializing")
        telemetry.update()

        // Initialize hardware
        robotHardware = RobotHardware(hardwareMap, telemetry)
        robotHardware.initialize()

        // Initialize state manager
        stateManager = StateManager(
            robotHardware.liftSubsystem,
            robotHardware.slideSubsystem,
            robotHardware.grabberSubsystem,
            telemetry
        )
        stateManager.initialize()

        // Initialize gamepad managers
        driveGamepadManager = GamepadManager(gamepad1)
        armGamepadManager = GamepadManager(if (gamepad2.gamepadId != -1) gamepad2 else gamepad1)

        // Configure gamepad references
        robotHardware.setGamepads(gamepad1, if (gamepad2.gamepadId != -1) gamepad2 else gamepad1)

        // Initialize panels telemetry


        telemetry.addData("Status", "Initialized")
        telemetry.update()
    }

    /**
     * Update gamepad state for tracking button presses
     */
    protected fun updateGamepadState() {
        // Save previous state
        prevDriveGamepad.copy(currDriveGamepad)
        prevArmGamepad.copy(currArmGamepad)

        // Update current state
        currDriveGamepad.copy(gamepad1)
        currArmGamepad.copy(if (gamepad2.gamepadId != -1) gamepad2 else gamepad1)


        // Update gamepad managers
        driveGamepadManager.update()
        armGamepadManager.update()
    }

    /**
     * Check for state changes based on gamepad input
     */
    protected fun checkStateTransitions() {
        val armGamepad = armGamepadManager.gamepad

        // State transitions
        if (armGamepad.dpad_down) {
            stateManager.setState(RobotState.FLOOR_GRAB)
            robotHardware.grabberSubsystem.setGrabbing(false)
        }

        if (armGamepad.left_bumper) {
            stateManager.setState(RobotState.SPECIMEN_DROP)
        }

        if (armGamepad.dpad_left) {
            stateManager.setState(RobotState.SPECIMEN_TRANSITION)
        }

        if (armGamepad.right_bumper) {
            stateManager.setState(RobotState.SPECIMEN_GRAB)
        }

        if (armGamepad.dpad_up) {
            stateManager.setState(RobotState.HIGH_BASKET)
        }
    }

    /**
     * Handle grabber control
     */
    protected fun handleGrabberControl() {
        if (currArmGamepad.triangle && !prevArmGamepad.triangle) {
            robotHardware.grabberSubsystem.openClaw()
        }

        if (currArmGamepad.cross && !prevArmGamepad.cross) {
            robotHardware.grabberSubsystem.closeClaw()
        }

        if (currArmGamepad.left_stick_button && !prevArmGamepad.left_stick_button) {
            robotHardware.grabberSubsystem.setGrabbing(true)
        } else if (currArmGamepad.right_stick_button && !prevArmGamepad.right_stick_button) {
            robotHardware.grabberSubsystem.setGrabbing(false)
        }
    }

    /**
     * Handle vision alignment and claw rotation
     */
    protected fun handleVisionAndRotation() {
        val currentState = stateManager.getCurrentState()
        val grabber = robotHardware.grabberSubsystem
        val vision = robotHardware.visionSubsystem

        // Handle limelight alignment toggle
        if (currArmGamepad.touchpad && !prevArmGamepad.touchpad) {
            vision.toggleAlignment()
        }

        // Pipeline switching
        if (currArmGamepad.dpad_right && !prevArmGamepad.dpad_right) {
            vision.switchToNextPipeline()
        }

        // Handle rotation based on vision or manual control
        if (currentState == RobotState.FLOOR_GRAB && !grabber.isGrabbing()) {
            if (vision.isAlignmentEnabled()) {
                val rotatePosition = vision.calculateRotatePosition()
                if (rotatePosition != null) {
                    grabber.setRotatePosition(rotatePosition)
                }
            } else {
                // Manual rotation control
                if (currArmGamepad.circle && !prevArmGamepad.circle) {
                    grabber.rotateClawRight()
                } else if (currArmGamepad.square && !prevArmGamepad.square) {
                    grabber.rotateClawLeft()
                }
            }
        }
    }

    /**
     * Handle recovery mode toggle
     */
    protected fun checkRecoveryToggle() {
        val driveGamepad = robotHardware.getDriveGamepad()

        if (driveGamepad != null && robotHardware.recoveryToggle.getState(arrayOf(
                driveGamepad.left_bumper,
                driveGamepad.right_bumper,
                driveGamepad.left_stick_button,
                driveGamepad.right_stick_button
            ))) {
            robotHardware.toggleRecoveryMode()
        }
    }

    /**
     * Handle speed limit toggle
     */
    protected fun checkSpeedLimitToggle() {
        val driveGamepad = robotHardware.getDriveGamepad()

        if (driveGamepad != null && robotHardware.speedLimitToggle.getState(arrayOf(driveGamepad.touchpad))) {
            robotHardware.driveSubsystem.toggleSpeedLimit()
        }
    }

    /**
     * Update indicator light based on current state
     */
    protected fun updateIndicatorLight() {
        robotHardware.visionSubsystem.updateIndicatorLight(robotHardware.isRecovering())
    }

    /**
     * Update telemetry with debug info
     */
    protected fun updateTelemetry() {
        // Base telemetry is handled by individual subsystems
        telemetry.update()

        // Update panels telemetry
        robotHardware.visionSubsystem.getStatus().let { status ->

        }


    }
}