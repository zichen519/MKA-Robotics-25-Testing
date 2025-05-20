package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.hardware.limelightvision.Limelight3A
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.ServoImplEx
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.subsystems.*
import org.firstinspires.ftc.teamcode.controllers.ToggleButton;

/**
 * Handles initialization and access to robot hardware
 */
class RobotHardware(
    private val hardwareMap: HardwareMap,
    private val telemetry: Telemetry
) {
    // Subsystems
    val driveSubsystem = DriveSubsystem(hardwareMap)
    val liftSubsystem = LiftSubsystem(hardwareMap, telemetry)
    val slideSubsystem = SlideSubsystem(hardwareMap)
    val grabberSubsystem = GrabberSubsystem(hardwareMap)
    val visionSubsystem = VisionSubsystem(hardwareMap, telemetry)

    // Gamepads
    private var driveGamepad: Gamepad? = null
    private var armGamepad: Gamepad? = null

    // Button handlers
    val recoveryToggle = ToggleButton()
    val speedLimitToggle = ToggleButton()

    /**
     * Initialize all subsystems
     */
    fun initialize() {
        // Initialize all subsystems
        driveSubsystem.initialize()
        liftSubsystem.initialize()
        slideSubsystem.initialize()
        grabberSubsystem.initialize()
        visionSubsystem.initialize()

        telemetry.addData("Status", "Initialized")
        telemetry.update()
    }

    /**
     * Set the gamepads used for control
     */
    fun setGamepads(drive: Gamepad, arm: Gamepad) {
        driveGamepad = drive
        armGamepad = arm
        driveSubsystem.setGamepad(drive)
    }

    /**
     * Get the drive gamepad
     */
    fun getDriveGamepad(): Gamepad? {
        return driveGamepad
    }

    /**
     * Get the arm gamepad
     */
    fun getArmGamepad(): Gamepad? {
        return armGamepad
    }

    /**
     * Toggle recovery mode for all relevant subsystems
     */
    fun toggleRecoveryMode() {
        val isRecovering = !liftSubsystem.isRecovering()
        liftSubsystem.toggleRecoveryMode()
        slideSubsystem.toggleRecoveryMode()

        telemetry.addData("Recovery Mode", isRecovering)
        telemetry.update()
    }

    /**
     * Check if in recovery mode
     */
    fun isRecovering(): Boolean {
        return liftSubsystem.isRecovering()
    }
}