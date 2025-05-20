package org.firstinspires.ftc.teamcode.controllers

import com.qualcomm.robotcore.hardware.DcMotorEx

/**
 * Controller for positioning motors with PID control
 */
class MotorPositionController(
    private val motor: DcMotorEx,
    private val syncMotor: DcMotorEx? = null,
    private val syncController: MotorSyncController? = null,
    var kP: Double,
    var kI: Double,
    var kD: Double,
    var minPower: Double,
    var ticksPerUnit: Double,
    var targetPos: Int
) {
    private var error = 0.0
    private var lastError = 0.0
    private var integral = 0.0

    /**
     * Set the target position
     */
    fun setTarget(position: Int) {
        targetPos = position
    }

    /**
     * Update the controller and apply motor power
     */
    fun update() {
        error = targetPos - motor.currentPosition.toDouble()
        integral += error
        val derivative = error - lastError
        lastError = error

        var power = kP * error + kI * integral + kD * derivative

        // Apply minimum power in the correct direction if we're not at the target
        if (error > 1 && power < minPower) {
            power = minPower
        } else if (error < -1 && power > -minPower) {
            power = -minPower
        }

        // Apply power to the main motor
        motor.power = power.coerceIn(-1.0, 1.0)

        // Apply power to the sync motor if present
        syncMotor?.let { syncMotor ->
            var syncPower = power

            // Apply sync correction if controller is provided
            syncController?.let {
                val correction = it.calculate(motor, syncMotor)
                syncPower += correction
            }

            syncMotor.power = syncPower.coerceIn(-1.0, 1.0)
        }
    }

    /**
     * Reset controller state
     */
    fun reset() {
        error = 0.0
        lastError = 0.0
        integral = 0.0
    }
}