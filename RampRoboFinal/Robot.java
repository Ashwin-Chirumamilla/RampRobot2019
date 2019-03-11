/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.PWMVictorSPX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.Pixy;
import edu.wpi.first.wpilibj.Encoder;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.PIDController;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
**/
public class Robot extends TimedRobot {
  
    DifferentialDrive drive = new DifferentialDrive(new Spark(0), new Spark(1));
    DifferentialDrive shooter = new DifferentialDrive(new Spark(2), new Spark (3));
    VictorSP hatch = new VictorSP(4);
    XboxController xbox = new XboxController(0);
    double sideSpeed = 0.7;
    double forwardSpeed = 0.7;
    double driveMode = 1;
    TalonSRX ballPush = new TalonSRX(3);
    TalonSRX cargoAngle = new TalonSRX(1);
    Timer timer = new Timer();
    PWMVictorSPX rampslide = new PWMVictorSPX(5);
    double a = 0.75; //Values for turbo mode forward
    double b = 0.7; //Values for turbo mode turning
    double Mode = 1; //Mode for cargo or hatch
    Pixy p2 = new Pixy();
    PixyPacket p2pkt;
    AHRS ahrs = new AHRS(Port.kUSB); 
    PIDController pid;
    double rotateToAngleRate;
  
    /* The following PID Controller coefficients will need to be tuned */
    /* to match the dynamics of your drive system.  Note that the      */
    /* SmartDashboard in Test mode has support for helping you tune    */
    /* controllers by displaying a form where you can enter new P, I,  */
    /* and D constants and test the mechanism.                         */
  
    static final double kP = 0.03;
    static final double kI = 0.00;
    static final double kD = 0.00;
    static final double kF = 0.00;
  
/* This tuning parameter indicates how close to "on target" the    */
/* PID Controller will attempt to get.                             */

  static final double kToleranceDegrees = 2.0f;
    
    

/*
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
*/
  @Override
  public void robotInit() {
    try {
      /* Communicate w/navX-MXP via the MXP SPI Bus.                                     */
      /* Alternatively:  I2C.Port.kMXP, SerialPort.Port.kMXP or SerialPort.Port.kUSB     */
      /* See http://navx-mxp.kauailabs.com/guidance/selecting-an-interface/ for details. */
      ahrs = new AHRS(Port.kUSB); 
  } catch (RuntimeException ex ) {
      System.out.println("Error instantiating navX-MXP:  " + ex.getMessage());
  }
  pid  = new PIDController(0.03, 0, 0, ahrs, pid);

  }

  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
  }

  @Override
  public void teleopPeriodic() {
    if (xbox.getPOV() == 90){
    try {
      p2pkt = p2.readPacket(1);
    }
    catch (PixyException e) {
      System.out.println("Pixy Packet error: " + e.getMessage());
    }
  }
  if (xbox.getPOV() == 270) {
    p2.getversion();  
  } 
    if (xbox.getStartButton()) {
      driveMode = 1;
    }

    //Hatch Mode:
    if (xbox.getBackButton()) {
      driveMode = -1;
    }

    //Turbo Mode:

    //If Left Stick is Pressed -> Turbo mode forward
    if (xbox.getStickButton(Hand.kLeft)) {
      forwardSpeed = 1;
    }
    //If left stick is not pressed (default values)
    else {
      forwardSpeed = 0.75;
    }

    //If Right Stick pressed -> Turbo mode turn controls
    if (xbox.getStickButton(Hand.kRight))  {
      sideSpeed = 1;
    } 
    //If right stick is not pressed (default value)
    else {
      sideSpeed = 0.5;
    }
    
    drive.arcadeDrive(forwardSpeed * (driveMode * xbox.getY(Hand.kLeft)),sideSpeed * xbox.getX(Hand.kRight));

    if (xbox.getTriggerAxis(Hand.kRight) > 0)  {
    shooter.tankDrive(-1 * xbox.getTriggerAxis(Hand.kRight), -1 *xbox.getTriggerAxis(Hand.kRight));
      
    }
    //If Left Trigger is pressed -> Reverse redlines
    if (xbox.getTriggerAxis(Hand.kLeft) > 0) {
      shooter.tankDrive(0.65 * xbox.getTriggerAxis(Hand.kLeft), 0.65 * xbox.getTriggerAxis(Hand.kLeft));
    }

    //Down Arrow on D-Pad:
    if (xbox.getPOV() > 160 && xbox.getPOV() < 200) {
      //Move ramp down:
      rampslide.set(-0.5);
    }

    //Up Arrow on D-Pad:
    else if (xbox.getPOV() == 0) {
      //Move ramp up:

      rampslide.set(0.5);
    }

    //Stationary Ramp
    else {
      rampslide.set(0);
    }

    //Actuator retracts
    if (xbox.getBButton()) {
      cargoAngle.set(ControlMode.PercentOutput, -0.5);
     // System.out.println(pot.get());
    }

    //Actuator Push
    else if (xbox.getXButton()) {
      cargoAngle.set(ControlMode.PercentOutput, 0.5);      
      //System.out.println(pot.get());
    }
    
    //If no buttons are being held for actuator
    else {
      //System.out.println(pot.get());
      cargoAngle.set(ControlMode.PercentOutput, 0);  
    }

    if (xbox.getAButton()) {
      ballPush.set(ControlMode.PercentOutput, 0.5);
    }
    else if (xbox.getYButton()) {
      ballPush.set(ControlMode.PercentOutput, -0.5);
    }
    else {
      ballPush.set(ControlMode.PercentOutput, 0);
    }

    //Grab hatch
    if (xbox.getBumper(Hand.kLeft)) {
      hatch.set(-0.25);
    }
    //Release Hatch
    else if (xbox.getBumper(Hand.kRight)) {
      hatch.set(0.25);
    }
    else {
      hatch.set(0);
    }
/*
    //Lock the hatch mechanism when turning
    if (xbox.getX(Hand.kRight) >= 0.6 || xbox.getX(Hand.kRight) <= -0.6) {
      j
      hatch.set(-.2);
   
  }
  else {
    hatch.set(0);
  }*/
  if (xbox.getPOV() == 90) {
  cargoAngle.set(ControlMode.Position, 150);
  }




  }
  

  @Override
  public void testInit() {
  }

  @Override
  public void testPeriodic() {
  }

}
