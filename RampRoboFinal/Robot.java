/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;
import edu.wpi.cscore.CameraServerJNI;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.GenericHID;
//import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.AnalogTrigger;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
//import com.ctre.phoenix.CANifierStatusFrame;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.PWMVictorSPX;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */

public class Robot extends TimedRobot {
  //Creating instances
  TalonSRX m_talon = new TalonSRX(3); //Talon SRX for shooter actuator
  XboxController xbox = new XboxController(0); //Xbox Controlelr
  DifferentialDrive m_drive = new DifferentialDrive(new Spark(0), new Spark(1)); //Driving Sparks
  DifferentialDrive m_redlines = new DifferentialDrive(new Spark(2), new Spark(3)); //Shooting Sparks
  VictorSP m_hatch = new VictorSP(4); //Hatch Victor SP motor controller
  double a = 0.75; //Values for turbo mode forward
  double b = 0.7; //Values for turbo mode turning
  double Mode = 1; //Mode for cargo or hatch
  Timer timer = new Timer(); //Timer
  TalonSRX ballPush = new TalonSRX(1); //Ball 
  PWMVictorSPX rampslide = new PWMVictorSPX(5);
  


  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    CameraServer.getInstance().startAutomaticCapture();  
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
    //Change Drive modes:

    //Cargo Mode:
    if (xbox.getStartButton()) {
      Mode = 1;
    }

    //Hatch Mode:
    if (xbox.getBackButton()) {
      Mode = -1;
    }

    //Turbo Mode:

    //If Left Stick is Pressed -> Turbo mode forward
    if (xbox.getStickButton(Hand.kLeft)) {
      a = 1;
    }
    //If left stick is not pressed (default values)
    else {
      a = 0.75;
    }

    //If Right Stick pressed -> Turbo mode turn controls
    if (xbox.getStickButton(Hand.kRight))  {
      b = 1;
    } 
    //If right stick is not pressed (default value)
    else {
      b = 0.7;
    }

    //Gathering input for driving around robot
    m_drive.arcadeDrive(a * (Mode * xbox.getY(Hand.kLeft)), b * (xbox.getX(Hand.kRight)));
    
    
    //Redline Shooters

    //If Right stick is pressed -> Shoot redlines
    if (xbox.getTriggerAxis(Hand.kRight) > 0)  {
      m_redlines.tankDrive(-1 * xbox.getTriggerAxis(Hand.kRight), 1 *xbox.getTriggerAxis(Hand.kRight));
      
      //Push the ball our when trigger if fully pressed:
      if (xbox.getTriggerAxis(Hand.kRight) == 1) {
        //Start timer
        timer.reset();
        timer.start();

        //Push the ball pusher out for 0.7 seconds
        while (timer.get() < 0.7) {ballPush.set(ControlMode.PercentOutput, 0.5);}

        //Reset timer
        timer.reset();
        timer.start();

        //Retract ball pusher for 0.7 sec
        while (timer.get() < 0.7) {ballPush.set(ControlMode.PercentOutput, -0.5); }

        //Stop ball pusher
        ballPush.set(ControlMode.PercentOutput, 0);
        
        
      }
    }
    //If Left Stick is pressed -> Reverse redlines
    if (xbox.getTriggerAxis(Hand.kLeft) > 0) {
      m_redlines.tankDrive(0.65 * xbox.getTriggerAxis(Hand.kLeft), -0.65 * xbox.getTriggerAxis(Hand.kLeft));
      //redlineSpark.set(-1 * xbox.getTriggerAxis(Hand.kLeft));
    }
    
    //Ramp reset:

    //Down Arrow on D-Pad:
    if (xbox.getPOV() == 180) {
      //Move ramp down:
      rampslide.set(0.5);
    }

    //Up Arrow on D-Pad:
    else if (xbox.getPOV() == 0) {
      //Move ramp up:
      rampslide.set(-0.5);
    }

    //Stationary Ramp
    else {
      rampslide.set(0);
    }


    //Actuator Control Code:

    //Actuator retracts
    if (xbox.getBButton()) {
      m_talon.set(ControlMode.PercentOutput, -0.5);
     // System.out.println(pot.get());
    }

    //Actuator Push
    else if (xbox.getXButton()) {
      m_talon.set(ControlMode.PercentOutput, 0.5);      
      //System.out.println(pot.get());
    }
    
    //If no buttons are being held for actuator
    else {
      //System.out.println(pot.get());
      m_talon.set(ControlMode.PercentOutput, 0);  
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
    
    //Hatch mechanism grab and release

    //Grab hatch
    if (xbox.getBumper(Hand.kLeft)) {
      m_hatch.set(-0.25);
    }

    //ReSSlease Hatch
    else if (xbox.getBumper(Hand.kRight)) {
      m_hatch.set(0.25);
    }

    //If no buttons are being held
    else {
      m_hatch.set(0);
    }

    //Lock the hatch mechanism when turning
    if (xbox.getX(Hand.kRight) >= 0.6 || xbox.getX(Hand.kRight) <= -0.6) {
      
        m_hatch.set(-.2);
     
    }
    else {
      m_hatch.set(0);
    }
    
    
    
  }

  @Override
  public void testInit() {
    
  }

  @Override
  public void testPeriodic() {

  }

}
