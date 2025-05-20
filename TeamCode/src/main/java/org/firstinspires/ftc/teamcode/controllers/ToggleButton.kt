package org.firstinspires.ftc.teamcode.controllers

/**
 * Button toggle implementation that handles multiple button combinations
 */
class ToggleButton {
    private var lastState = false

    /**
     * Get toggle state based on button combination
     *
     * @param buttons Array of button states that should trigger the toggle when all true
     * @return true if toggle should activate, false otherwise
     */
    fun getState(buttons: Array<Boolean>): Boolean {
        // Check if all buttons are pressed
        val currentState = buttons.all { it }

        // Detect rising edge (buttons were not all pressed before, but are now)
        val result = currentState && !lastState

        // Update last state
        lastState = currentState

        return result
    }

    /**
     * Reset the toggle state
     */
    fun reset() {
        lastState = false
    }
}