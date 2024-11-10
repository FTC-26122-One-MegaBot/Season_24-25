package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Starterbot2025TeleOpBlocks12 (Blocks to Java)")
public class StarterBot2025TeleOpJava_V2 extends LinearOpMode {

    private DcMotor leftDrive;
    private DcMotor rightDrive;
    private Servo claw;
    private CRServo intake;
    private DcMotor wrist;
    private DcMotor arm;

    String currentState;
    boolean lastGrab;
    int targetArm;
    int targetWrist;
    String INTAKE;
    String LOW_BASKET;
    String INIT;
    boolean lastHook;
    String MANUAL;
    String WALL_GRAB;
    String WALL_UNHOOK;
    String HOVER_HIGH;
    String CLIP_HIGH;
    boolean clawOpen = false;

    /**
     * This function is executed when this Op Mode is selected.
     */
    @Override
    public void runOpMode() {
        leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");
        claw = hardwareMap.get(Servo.class, "claw");
        intake = hardwareMap.get(CRServo.class, "intake");
        wrist = hardwareMap.get(DcMotor.class, "wrist");
        arm = hardwareMap.get(DcMotor.class, "arm");

        // Put initialization blocks here.
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        leftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        MANUAL = "MANUAL";
        INTAKE = "INTAKE";
        WALL_GRAB = "WALL_GRAB";
        WALL_UNHOOK = "WALL_UNHOOK";
        HOVER_HIGH = "HOVER_HIGH";
        CLIP_HIGH = "CLIP_HIGH";
        LOW_BASKET = "LOW_BASKET";
        INIT = "INIT";
        currentState = INIT;
        lastHook = false;
        lastGrab = false;
        waitForStart();
        if (opModeIsActive()) {
            // Put run blocks here.
            while (opModeIsActive()) {
                GAMEPAD_INPUT_STATE();
                GAMEPAD_INPUT_TOGGLE();
                GAMEPAD_INPUT_MANUAL();
                GAMEPAD_INTAKE();
                STATE_MACHINE();
                SPLIT_STICK_ARCADE_DRIVE();
                Wrist();
                Arm();
                TELEMETRY();
            }
        }
    }

    /**
     * Describe this function...
     */
    private void GAMEPAD_INPUT_TOGGLE() {
        if (gamepad1.right_bumper) {
            claw.setPosition(0.3);
        } else {
            claw.setPosition(0.2);
        }
    }

    /**
     * Describe this function...
     */
    private void GAMEPAD_INPUT_STATE() {
        if (gamepad1.a) {
            currentState = INTAKE;
        } else if (gamepad1.b && !lastGrab) {
            if (currentState.equals(WALL_GRAB)) {
                currentState = WALL_UNHOOK;
            } else {
                currentState = WALL_GRAB;
            }
        } else if (gamepad1.y && !lastHook) {
            if (currentState.equals(HOVER_HIGH)) {
                currentState = CLIP_HIGH;
            } else {
                currentState = HOVER_HIGH;
            }
        } else if (gamepad1.x) {
            currentState = LOW_BASKET;
        } else if (gamepad1.left_bumper) {
            currentState = INIT;
        }
        lastGrab = gamepad1.b;
        lastHook = gamepad1.y;
    }

    /**
     * Describe this function...
     */
    private void GAMEPAD_INTAKE() {
        if (gamepad1.right_trigger > 0.1) {
            intake.setPower(1);
        } else if (gamepad1.left_trigger > 0.1) {
            intake.setPower(-1);
        } else {
            intake.setPower(0);
        }
    }

    /**
     * Describe this function...
     */
    private void Wrist() {
        wrist.setTargetPosition(targetWrist);
        wrist.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        wrist.setPower(1);
    }

    /**
     * Describe this function...
     */
    private void SPLIT_STICK_ARCADE_DRIVE() {
        leftDrive.setPower(Range.clip(-gamepad1.left_stick_y - gamepad1.right_stick_x, -1, 1));
        rightDrive.setPower(Range.clip(-gamepad1.left_stick_y + gamepad1.right_stick_x, -1, 1));
    }

    /**
     * Describe this function...
     */
    private void Arm() {
        arm.setTargetPosition(targetArm);
        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        arm.setPower(1);
    }

    /**
     * Describe this function...
     */
    private void GAMEPAD_INPUT_MANUAL() {
        if (gamepad1.dpad_up) {
            currentState = MANUAL;
            targetArm += 25;
        } else if (gamepad1.dpad_down) {
            currentState = MANUAL;
            targetArm += -25;
        } else if (gamepad1.dpad_left) {
            currentState = MANUAL;
            targetWrist += 10;
        } else if (gamepad1.dpad_right) {
            currentState = MANUAL;
            targetWrist += -10;
        }
    }

    /**
     * Describe this function...
     */
    private void STATE_MACHINE() {
        if (currentState.equals(INIT)) {
            targetArm = 450;
            targetWrist = 0;
        } else if (currentState.equals(INTAKE)) {
            targetArm = 480;
            targetWrist = 240;
        } else if (currentState.equals(WALL_GRAB)) {
            targetArm = 1100;
            targetWrist = 10;
        } else if (currentState.equals(WALL_UNHOOK)) {
            targetArm = 1700;
            targetWrist = 10;
        } else if (currentState.equals(HOVER_HIGH)) {
            targetArm = 2600;
            targetWrist = 10;
        } else if (currentState.equals(CLIP_HIGH)) {
            targetArm = 2100;
            targetWrist = 10;
        } else if (currentState.equals(LOW_BASKET)) {
            targetArm = 2500;
            targetWrist = 270;
        } else {
            currentState = MANUAL;
        }
    }

    /**
     * Describe this function...
     */
    private void TELEMETRY() {

        telemetry.addData("STATE:", currentState);
        telemetry.addData("clawOpen", clawOpen ? "Open" : "Closed");
        telemetry.addData("Arm Position", arm.getCurrentPosition());
        telemetry.addData("Arm Power", arm.getPower());
        telemetry.addData("Target arm", targetArm);
        telemetry.addData("Wrist Position", wrist.getCurrentPosition());
        telemetry.addData("Wrist Power", wrist.getPower());
        telemetry.addData("Target wrist", targetWrist);
        telemetry.addData("Claw position", claw.getPosition());
        telemetry.update();
    }
}