package org.firstinspires.ftc.teamcode.util

import com.qualcomm.robotcore.hardware.Gamepad

/**
 * Extension functions for improved Kotlin usability
 */

/**
 * Compare two gamepads to check if a button was just pressed
 *
 * @param previous The previous gamepad state
 * @param button Lambda that returns the button state for both gamepads
 * @return True if the button was just pressed
 */
fun Gamepad.justPressed(previous: Gamepad, button: (Gamepad) -> Boolean): Boolean {
    return button(this) && !button(previous)
}

/**
 * Compare two gamepads to check if a button was just released
 *
 * @param previous The previous gamepad state
 * @param button Lambda that returns the button state for both gamepads
 * @return True if the button was just released
 */
fun Gamepad.justReleased(previous: Gamepad, button: (Gamepad) -> Boolean): Boolean {
    return !button(this) && button(previous)
}

/**
 * Apply a deadzone to a joystick value
 *
 * @param value The joystick value
 * @param deadzone The deadzone value (default 0.05)
 * @return The value with deadzone applied
 */
fun applyDeadzone(value: Float, deadzone: Float = 0.05f): Float {
    return if (Math.abs(value) < deadzone) 0f else value
}

/**
 * Extension property for left stick magnitude
 */
val Gamepad.leftStickMagnitude: Float
    get() = Math.sqrt((left_stick_x * left_stick_x + left_stick_y * left_stick_y).toDouble()).toFloat()

/**
 * Extension property for right stick magnitude
 */
val Gamepad.rightStickMagnitude: Float
    get() = Math.sqrt((right_stick_x * right_stick_x + right_stick_y * right_stick_y).toDouble()).toFloat()