package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * Subsystem that handles the robot's drivetrain
 */
class DriveSubsystem(private val hardwareMap: HardwareMap) : Subsystem {
    // Motors
    private lateinit var leftFront: DcMotorEx
    private lateinit var rightFront: DcMotorEx
    private lateinit var leftRear: DcMotorEx
    private lateinit var rightRear: DcMotorEx

    // Drive settings
    private var driveGamepad: Gamepad? = null
    private var isSpeedLimited = true

    override fun initialize() {
        // Initialize motors
        leftFront = hardwareMap.get(DcMotorEx::class.java, "leftFront")
        rightFront = hardwareMap.get(DcMotorEx::class.java, "rightFront")
        leftRear = hardwareMap.get(DcMotorEx::class.java, "leftRear")
        rightRear = hardwareMap.get(DcMotorEx::class.java, "rightRear")

        // Set motor directions
        leftFront.setDirection(DcMotorSimple.Direction.FORWARD)
        rightFront.setDirection(DcMotorSimple.Direction.REVERSE)
        leftRear.setDirection(DcMotorSimple.Direction.FORWARD)
        rightRear.setDirection(DcMotorSimple.Direction.REVERSE)

        // Reset encoders
        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE)
    }

    /**
     * Set the gamepad used for controlling the drive subsystem
     */
    fun setGamepad(gamepad: Gamepad) {
        this.driveGamepad = gamepad
    }

    /**
     * Toggle speed limiting on/off
     */
    fun toggleSpeedLimit() {
        isSpeedLimited = !isSpeedLimited
    }

    /**
     * Move the robot based on gamepad inputs
     */
    fun drive() {
        driveGamepad?.let { gamepad ->
            val modifier = 1.0
            val leftPower = 1.0 * modifier
            val rightPower = if (isSpeedLimited) 0.517 else 1.0
            val reverseStick = true

            val stickX = if (!reverseStick) gamepad.left_stick_x else gamepad.right_stick_x
            val stickY = if (!reverseStick) -gamepad.left_stick_y else -gamepad.right_stick_y
            val rightStickX = if (!reverseStick) gamepad.right_stick_x else gamepad.left_stick_x
            val rightStickY = if (!reverseStick) gamepad.right_stick_y else gamepad.left_stick_y

            val r = leftPower * hypot(stickX, stickY)
            val robotAngle = atan2(stickY, stickX) + 3 * Math.PI / 4
            val rightX = rightPower * rightStickX
            val rightY = rightPower * rightStickY

            val v1 = r * cos(robotAngle) - rightX + rightY
            val v2 = r * sin(robotAngle) + rightX + rightY
            val v3 = r * sin(robotAngle) - rightX + rightY
            val v4 = r * cos(robotAngle) + rightX + rightY

            setMotorPowers(v1, v2, v3, v4)
        }
    }

    /**
     * Set all motor powers directly
     */
    fun setMotorPowers(v1: Double, v2: Double, v3: Double, v4: Double) {
        leftFront.power = v1
        rightFront.power = v2
        leftRear.power = v3
        rightRear.power = v4
    }

    /**
     * Set zero power behavior for all motors
     */
    private fun setZeroPowerBehavior(behavior: DcMotor.ZeroPowerBehavior) {
        leftFront.zeroPowerBehavior = behavior
        rightFront.zeroPowerBehavior = behavior
        leftRear.zeroPowerBehavior = behavior
        rightRear.zeroPowerBehavior = behavior
    }

    override fun update() {
        // Default update calls drive if gamepad is connected
        driveGamepad?.let { drive() }
    }

    override fun reset() {
        setMotorPowers(0.0, 0.0, 0.0, 0.0)
    }

    override fun stop() {
        setMotorPowers(0.0, 0.0, 0.0, 0.0)
    }
}