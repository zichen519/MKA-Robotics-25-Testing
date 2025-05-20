package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.ServoImplEx
import com.qualcomm.robotcore.util.ElapsedTime

/**
 * Subsystem that handles the robot's grabber mechanism, including all servos
 */
class GrabberSubsystem(private val hardwareMap: HardwareMap) : Subsystem {
    // Servos
    private lateinit var grab: Servo
    private lateinit var rotate: Servo
    private lateinit var elbow1: ServoImplEx
    private lateinit var elbow2: ServoImplEx
    private lateinit var wrist: Servo

    // State
    private var grabbing = false
    private var clawClosed = false
    private var grabState = 0
    private var grabDone = false

    // Rotate positions
    private var rotateIndex = 0
    private val rotatePositions = doubleArrayOf(0.23, 0.39, 0.52, 0.65)

    // Timer
    private val timer = ElapsedTime()

    override fun initialize() {
        // Initialize servos
        grab = hardwareMap.get(Servo::class.java, "grab")
        rotate = hardwareMap.get(Servo::class.java, "rotate")

        elbow1 = hardwareMap.get(ServoImplEx::class.java, "elbow1")
        elbow1.direction = Servo.Direction.REVERSE

        elbow2 = hardwareMap.get(ServoImplEx::class.java, "elbow2")
        wrist = hardwareMap.get(Servo::class.java, "wrist")

        timer.reset()
    }

    /**
     * Set the positions of all grabber servos
     */
    fun setPositions(
        grabPosition: Double? = null,
        rotatePosition: Double? = null,
        elbow1Position: Double? = null,
        elbow2Position: Double? = null,
        wristPosition: Double? = null
    ) {
        grabPosition?.let { grab.position = it }
        rotatePosition?.let { rotate.position = it }
        elbow1Position?.let { elbow1.position = it }
        elbow2Position?.let { elbow2.position = it }
        wristPosition?.let { wrist.position = it }
    }

    /**
     * Opens the claw
     */
    fun openClaw() {
        clawClosed = false
        grab.position = 0.95
    }

    /**
     * Closes the claw
     */
    fun closeClaw() {
        clawClosed = true
        grab.position = 0.73
    }

    /**
     * Rotate the claw to the right
     */
    fun rotateClawRight() {
        if (rotateIndex < rotatePositions.size - 1) {
            rotateIndex++
            rotate.position = rotatePositions[rotateIndex]
        }
    }

    /**
     * Rotate the claw to the left
     */
    fun rotateClawLeft() {
        if (rotateIndex > 0) {
            rotateIndex--
            rotate.position = rotatePositions[rotateIndex]
        }
    }

    /**
     * Get the current rotate position index
     */
    fun getRotateIndex(): Int {
        return rotateIndex
    }

    /**
     * Set the rotate position directly
     */
    fun setRotatePosition(position: Double) {
        rotate.position = position
    }

    /**
     * Set the grabbing state
     */
    fun setGrabbing(isGrabbing: Boolean) {
        this.grabbing = isGrabbing
        if (isGrabbing) {
            grabDone = false
            grabState = 0
        }
    }

    /**
     * Is the claw closed?
     */
    fun isClawClosed(): Boolean {
        return clawClosed
    }

    /**
     * Is the grabber currently in grabbing sequence?
     */
    fun isGrabbing(): Boolean {
        return grabbing
    }

    /**
     * Is the grabbing sequence complete?
     */
    fun isGrabDone(): Boolean {
        return grabDone
    }

    /**
     * Update the floor grab sequence
     * Returns true when done
     */
    fun updateFloorGrab(): Boolean {
        if (!grabbing) {
            // Default hover position
            elbow1.position = 0.6
            elbow2.position = 0.6
            wrist.position = 1.0
            openClaw()
            return true
        }

        if (grabDone) {
            return true
        }

        // State machine for grabbing sequence
        when (grabState) {
            0 -> {
                // Position arm for grab
                elbow1.position = 0.48
                elbow2.position = 0.48
                wrist.position = 1.0
                timer.reset()
                grabState++
            }
            1 -> {
                if (timer.seconds() > 0.2) {
                    // Close the claw
                    closeClaw()
                    timer.reset()
                    grabState++
                }
            }
            2 -> {
                if (timer.seconds() > 0.2) {
                    // Lift slightly
                    elbow1.position = 0.6
                    elbow2.position = 0.6
                    wrist.position = 1.0
                    timer.reset()
                    grabState++
                }
            }
            3 -> {
                if (timer.seconds() > 0.2) {
                    // Finish sequence
                    timer.reset()
                    grabState = 0
                    grabDone = true
                }
            }
        }

        return grabDone
    }

    /**
     * Configure for floor grab position
     */
    fun configureForFloorGrab() {
        elbow1.position = 0.6 // hover
        elbow2.position = 0.6
        wrist.position = 1.0
        openClaw()
    }

    /**
     * Configure for specimen grab position
     */
    fun configureForSpecimenGrab() {
        elbow1.position = 0.99
        elbow2.position = 0.99
        wrist.position = 0.73
        rotate.position = 0.23
    }

    /**
     * Configure for specimen transition
     */
    fun configureForSpecimenTransition() {
        rotate.position = 0.8
        elbow1.position = 0.3
        elbow2.position = 0.3
        wrist.position = 0.4
    }

    /**
     * Configure for specimen drop
     */
    fun configureForSpecimenDrop() {
        elbow1.position = 0.12
        elbow2.position = 0.12
    }

    /**
     * Configure for high basket
     */
    fun configureForHighBasket() {
        elbow1.position = 0.5
        elbow2.position = 0.5
        wrist.position = 0.2
        rotate.position = 0.52
    }

    override fun update() {
        // Default update is empty - state is managed by the caller
    }

    override fun reset() {
        // Reset to a safe position
        setPositions(
            grabPosition = 0.95,
            rotatePosition = 0.23,
            elbow1Position = 0.0,
            elbow2Position = 0.0,
            wristPosition = 1.0
        )
        grabbing = false
        clawClosed = false
        grabDone = false
        grabState = 0
    }

    override fun stop() {
        // No motors to stop
    }
}