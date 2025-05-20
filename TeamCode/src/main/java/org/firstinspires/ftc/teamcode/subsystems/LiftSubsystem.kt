package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.controllers.MotorPositionController

/**
 * Subsystem that handles the robot's lift mechanism
 */
class LiftSubsystem(private val hardwareMap: HardwareMap, private val telemetry: Telemetry? = null) : Subsystem {
    // Motor
    private lateinit var lift: DcMotorEx

    // Controller
    private lateinit var liftController: MotorPositionController

    // State
    private var recovering = false
    private var targetPosition = 0

    override fun initialize() {
        // Initialize motor
        lift = hardwareMap.get(DcMotorEx::class.java, "lift")
        lift.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        lift.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        lift.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        // Initialize controller
        liftController = MotorPositionController(
            motor = lift,
            syncMotor = null,
            kP = 0.004,
            kI = 0.0,
            kD = 0.0004,
            minPower = 0.1,
            ticksPerUnit = 1425.1,
            targetPos = 0
        )
    }

    /**
     * Set the target position for the lift
     */
    fun setTarget(position: Int) {
        if (!recovering) {
            targetPosition = position
            liftController.setTarget(position)
        }
    }

    /**
     * Get the current position of the lift
     */
    fun getCurrentPosition(): Int {
        return lift.currentPosition
    }

    /**
     * Handle direct control of the lift using gamepad triggers
     */
    fun handleManualControl(gamepad: Gamepad) {
        if (recovering) {
            if (gamepad.left_trigger > 0) {
                lift.power = gamepad.left_trigger.toDouble()
            } else if (gamepad.right_trigger > 0) {
                lift.power = -gamepad.right_trigger.toDouble()
            } else {
                lift.power = 0.0
            }
        }
    }

    /**
     * Toggle recovery mode
     */
    fun toggleRecoveryMode() {
        recovering = !recovering

        if (recovering) {
            // Reset the encoder in recovery mode
            lift.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            lift.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        }
    }

    /**
     * Check if the lift is in recovery mode
     */
    fun isRecovering(): Boolean {
        return recovering
    }

    override fun update() {
        if (!recovering) {
            liftController.update()

            telemetry?.addData("Lift Position", lift.currentPosition)
            telemetry?.addData("Lift Target", targetPosition)
        }
    }

    override fun reset() {
        setTarget(0)
    }

    override fun stop() {
        lift.power = 0.0
    }
}