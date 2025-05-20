package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.controllers.MotorPositionController
import org.firstinspires.ftc.teamcode.controllers.MotorSyncController
/**
 * Subsystem that handles the robot's slide mechanism
 */
class SlideSubsystem(private val hardwareMap: HardwareMap) : Subsystem {
    // Motors
    private lateinit var slide1: DcMotorEx
    private lateinit var slide2: DcMotorEx

    // Controller
    private lateinit var slideController: MotorPositionController

    // State
    private var recovering = false
    private var targetPosition = 0

    override fun initialize() {
        // Initialize motors
        slide1 = hardwareMap.get(DcMotorEx::class.java, "slide1")
        slide1.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        slide1.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        slide1.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        slide1.direction = DcMotorSimple.Direction.REVERSE

        slide2 = hardwareMap.get(DcMotorEx::class.java, "slide2")
        slide2.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        slide2.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        slide2.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        // Initialize controller with both motors
        val syncController = MotorSyncController(0.0, 0.0, 0.0)
        slideController = MotorPositionController(
            motor = slide1,
            syncMotor = slide2,
            syncController = syncController,
            kP = 0.015,
            kI = 0.0,
            kD = 0.0005,
            minPower = 0.5,
            ticksPerUnit = 384.5,
            targetPos = 0
        )
    }

    /**
     * Set the target position for the slides
     */
    fun setTarget(position: Int) {
        targetPosition = position
        slideController.setTarget(position)
    }

    /**
     * Get the current position of the slide
     */
    fun getCurrentPosition(): Int {
        return slide1.currentPosition
    }

    /**
     * Move slides up at full power
     */
    fun moveUp() {
        slide1.power = 1.0
        slide2.power = 1.0
    }

    /**
     * Move slides down at full power
     */
    fun moveDown() {
        slide1.power = -1.0
        slide2.power = -1.0
    }

    /**
     * Handle manual control using gamepad triggers
     */
    fun handleManualControl(gamepad: Gamepad, positionLimit: Int = 350) {
        if (gamepad.left_trigger > 0 && (slide1.currentPosition < positionLimit || recovering)) {
            val slidePower = gamepad.left_trigger.toDouble()
            slide1.power = slidePower
            slide2.power = slidePower
        } else if (gamepad.right_trigger > 0 && (slide1.currentPosition > 0 || recovering)) {
            val slidePower = gamepad.right_trigger.toDouble()
            slide1.power = -slidePower
            slide2.power = -slidePower
        } else {
            // Maintain position if not actively controlled
            setTarget(slide1.currentPosition)
            slideController.update()
        }
    }

    /**
     * Toggle recovery mode
     */
    fun toggleRecoveryMode() {
        recovering = !recovering

        if (recovering) {
            // Reset encoders in recovery mode
            slide1.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            slide1.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

            slide2.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            slide2.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        }
    }

    /**
     * Check if in recovery mode
     */
    fun isRecovering(): Boolean {
        return recovering
    }

    override fun update() {
        // Default update - simply let the controller work
        if (!recovering) {
            slideController.update()
        }
    }

    override fun reset() {
        setTarget(0)
    }

    override fun stop() {
        slide1.power = 0.0
        slide2.power = 0.0
    }
}