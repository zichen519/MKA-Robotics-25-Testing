package org.firstinspires.ftc.teamcode.subsystems

import com.qualcomm.hardware.limelightvision.LLResult
import com.qualcomm.hardware.limelightvision.LLStatus
import com.qualcomm.hardware.limelightvision.Limelight3A
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.Telemetry

/**
 * Subsystem that handles the Limelight vision system
 */
class VisionSubsystem(private val hardwareMap: HardwareMap, private val telemetry: Telemetry? = null) : Subsystem {
    // Limelight
    private lateinit var limelight: Limelight3A

    // Light indicator
    private lateinit var lightServo: Servo

    // Pipeline configuration
    private val pipelines = intArrayOf(0, 1, 2)
    private var pipelineIndex = 0

    // Alignment settings
    private var offset = 0.15
    private var limelightAlign = true

    override fun initialize() {
        // Initialize Limelight
        limelight = hardwareMap.get(Limelight3A::class.java, "limelight")
        limelight.setPollRateHz(10)
        limelight.pipelineSwitch(0)

        // Initialize indicator light
        lightServo = hardwareMap.get(Servo::class.java, "light")
    }

    /**
     * Start the limelight
     */
    fun start() {
        limelight.start()
    }

    /**
     * Stop the limelight
     */
    fun stopVision() {
        limelight.stop()
    }

    /**
     * Get the latest result from the limelight
     */
    fun getLatestResult(): LLResult? {
        return limelight.latestResult
    }

    /**
     * Get the status of the limelight
     */
    fun getStatus(): LLStatus {
        return limelight.status
    }

    /**
     * Toggle alignment mode
     */
    fun toggleAlignment() {
        limelightAlign = !limelightAlign
    }

    /**
     * Check if alignment is enabled
     */
    fun isAlignmentEnabled(): Boolean {
        return limelightAlign
    }

    /**
     * Switch to the next pipeline
     */
    fun switchToNextPipeline() {
        pipelineIndex = (pipelineIndex + 1) % pipelines.size
        limelight.pipelineSwitch(pipelines[pipelineIndex])
    }

    /**
     * Set indicator light color based on pipeline and mode
     */
    fun updateIndicatorLight(isRecovering: Boolean) {
        when {
            isRecovering -> {
                lightServo.position = 0.5
                telemetry?.addData("COLOR", "GREEN (Recovering)")
            }
            !limelightAlign -> {
                lightServo.position = 1.0
            }
            else -> {
                val status = limelight.status
                when (status.pipelineIndex) {
                    0 -> {
                        lightServo.position = 0.28
                        telemetry?.addData("COLOR", "RED")
                    }
                    1 -> {
                        lightServo.position = 0.388
                        telemetry?.addData("COLOR", "YELLOW")
                    }
                    2 -> {
                        lightServo.position = 0.611
                        telemetry?.addData("COLOR", "BLUE")
                    }
                }
            }
        }
    }

    /**
     * Calculate rotation position based on vision data
     */
    fun calculateRotatePosition(): Double? {
        if (!limelightAlign) return null

        val result = limelight.latestResult ?: return null
        if (!result.isValid) return null

        val outputs = result.pythonOutput
        return (outputs[5] / 255) + offset
    }

    /**
     * Update the offset value
     */
    fun updateOffset(value: Double) {
        offset += value
    }

    /**
     * Log telemetry
     */
    fun logTelemetry() {
        val status = limelight.status

        telemetry?.addData("Name", "%s", status.name)
        telemetry?.addData("LL", "Temp: %.1fC, CPU: %.1f%%, FPS: %d", status.temp, status.cpu, status.fps.toInt())
        telemetry?.addData("Pipeline", "Index: %d, Type: %s", status.pipelineIndex, status.pipelineType)
        telemetry?.addData("limelight align", limelightAlign)
    }

    override fun update() {
        // Default update - just log status
        logTelemetry()
    }

    override fun reset() {
        pipelineIndex = 0
        limelight.pipelineSwitch(pipelines[pipelineIndex])
    }

    override fun stop() {
        limelight.stop()
    }
}