package org.firstinspires.ftc.teamcode.util

import com.qualcomm.robotcore.hardware.Gamepad

/**
 * Helper class for handling gamepad input and button state changes
 */
class GamepadManager(val gamepad: Gamepad) {
    private var previousGamepad = Gamepad()

    /**
     * Update the previous gamepad state with current state
     */
    fun update() {
        previousGamepad.copy(gamepad)
    }

    /**
     * Check if a button was just pressed
     */
    fun justPressed(button: (Gamepad) -> Boolean): Boolean {
        return button(gamepad) && !button(previousGamepad)
    }

    /**
     * Check if a button was just released
     */
    fun justReleased(button: (Gamepad) -> Boolean): Boolean {
        return !button(gamepad) && button(previousGamepad)
    }

    /**
     * Check if a button is currently held
     */
    fun isPressed(button: (Gamepad) -> Boolean): Boolean {
        return button(gamepad)
    }

    /**
     * Reset the gamepad manager state
     */
    fun reset() {
        previousGamepad = Gamepad()
    }
}