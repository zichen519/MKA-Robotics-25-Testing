package org.firstinspires.ftc.teamcode.subsystems

/**
 * Subsystem interface defining the core functionality all subsystems must implement
 */
interface Subsystem {
    /**
     * Initialize the subsystem's hardware
     */
    fun initialize()

    /**
     * Update the subsystem's state (called every loop cycle)
     */
    fun update()

    /**
     * Reset the subsystem to default state
     */
    fun reset()

    /**
     * Stop all motors and actuators in the subsystem
     */
    fun stop()
}