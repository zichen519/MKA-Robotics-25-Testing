package org.firstinspires.ftc.teamcode.controllers

import com.qualcomm.robotcore.hardware.DcMotorEx

/**
 * Controller for synchronizing two motors
 */
class MotorSyncController(
    var kP: Double,
    var kI: Double,
    var kD: Double
) {
    private var error = 0.0
    private var lastError = 0.0
    private var integral = 0.0

    /**
     * Calculate correction needed to sync two motors
     */
    fun calculate(master: DcMotorEx, slave: DcMotorEx): Double {
        error = master.currentPosition.toDouble() - slave.currentPosition.toDouble()
        integral += error
        val derivative = error - lastError
        lastError = error

        return kP * error + kI * integral + kD * derivative
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