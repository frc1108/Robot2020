/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import io.github.oblarg.oblog.annotations.Log;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;

import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.HopperSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.BallLauncher;
import frc.robot.subsystems.ClimberSubsystem;
import frc.robot.subsystems.LightsSubsystem;

import frc.robot.commands.ManualHopper;
import frc.robot.commands.SetSolidColor;
import frc.robot.commands.BasicCommandGroup;
import frc.robot.commands.SimpleAutoGroup;

import static frc.robot.Constants.OIConstants.*;

/**
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems
  @Log private final DriveSubsystem m_robotDrive = new DriveSubsystem();
  @Log private final HopperSubsystem m_hopper = new HopperSubsystem();
  @Log private final BallLauncher m_launcher = new BallLauncher();
  @Log private final ClimberSubsystem m_climber = new ClimberSubsystem();
  private final IntakeSubsystem m_intake = new IntakeSubsystem();
  private final FeederSubsystem m_feeder = new FeederSubsystem();
  private final LightsSubsystem m_lights = new LightsSubsystem();

  // A chooser for autonomous commands
  SendableChooser<Command> m_chooser = new SendableChooser<>();
  SendableChooser<Command> m_ledChooser = new SendableChooser<>();

  // Controller for driver and operator
  XboxController m_driverController = new XboxController(kDriverControllerPort);
  XboxController m_operatorController = new XboxController(kOperatorControllerPort);

  /**
   * The container for the robot.  Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    configureButtonBindings();

    // Drive default is split stick arcade drive (fwd left / rot right)
    m_robotDrive.setDefaultCommand(
        new RunCommand(()->m_robotDrive.arcadeDrive(m_driverController.getY(GenericHID.Hand.kLeft),
            m_driverController.getX(GenericHID.Hand.kRight)), m_robotDrive));

    // Hopper axle default
    m_hopper.setDefaultCommand(
        new ManualHopper(m_hopper, () -> m_operatorController.getY(GenericHID.Hand.kRight)));
      
    // Winch default 
    m_climber.setDefaultCommand(
        new RunCommand(()->m_climber.manualControl(m_operatorController.getY(GenericHID.Hand.kLeft)),m_climber));

    // Add commands to the autonomous command chooser
    m_chooser.setDefaultOption("Drive Off Line Auto", new SimpleAutoGroup(m_robotDrive));
    m_chooser.addOption("Regular Auto", new BasicCommandGroup(m_robotDrive, m_launcher, m_feeder, m_hopper));
    
    m_ledChooser.setDefaultOption("None", new SetSolidColor(m_lights,0,0,0));
    m_ledChooser.addOption("Red", new SetSolidColor(m_lights,255,0,0));
    m_ledChooser.addOption("Blue", new SetSolidColor(m_lights,0,0,255));
    
    Shuffleboard.getTab("Setup").add(m_chooser);
    Shuffleboard.getTab("Setup").add(m_ledChooser);
  }

  /**
   * Use this method to define your button->command mappings.  Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a
   * {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    // Reverse feeder motor for time with operator button Y
    new JoystickButton(m_operatorController, XboxController.Button.kY.value)
        .toggleWhenActive(new StartEndCommand(
                              ()->m_feeder.slowOutFeeder(),
                              ()->m_feeder.stop(),m_feeder)
                              .withTimeout(0.2));

   // Run feeder motor for time with operator button B
    new JoystickButton(m_operatorController, XboxController.Button.kB.value)
        .toggleWhenActive(new WaitCommand(0.8)
                              .andThen(new StartEndCommand(
                                           ()->m_feeder.fastInFeeder(),
                                           ()->m_feeder.stop(),m_feeder)
                                           .withTimeout(5)));

   
    // Run feeder motor for time with operator button X
    new JoystickButton(m_operatorController, XboxController.Button.kX.value)
        .toggleWhenActive(new StartEndCommand(
                              ()->m_feeder.slowInFeeder(),
                              ()->m_feeder.stop(),m_feeder)
                              .withTimeout(0.6));

    new JoystickButton(m_operatorController, XboxController.Button.kBumperRight.value)
      .whileHeld(new RunCommand(() -> m_intake.startIntake(), m_intake));

    new JoystickButton(m_operatorController, XboxController.Button.kBumperLeft.value)
      .whileHeld(new RunCommand(() -> m_intake.slowOutIntake(), m_intake));
    
    new POVButton(m_operatorController, 180)  // Xbox down arrow
      .whenPressed(new RunCommand(() -> m_hopper.down(), m_hopper).withTimeout(2)
      .withInterrupt(m_hopper::isLowSwitchSet));

    new POVButton(m_operatorController, 0)  // Xbox up arrow
      .whenPressed(new RunCommand(() -> m_hopper.up(), m_hopper).withTimeout(2)
      .withInterrupt(m_hopper::isHighSwitchSet));

    new JoystickButton(m_operatorController, XboxController.Button.kB.value)
      .whenPressed(new RunCommand(()-> m_launcher.startPIDLauncher(), m_launcher).withTimeout(6));
     
    new JoystickButton(m_operatorController, XboxController.Button.kStart.value)
      .whenPressed(new RunCommand(()-> m_climber.setSpeedMax(),m_climber).withTimeout(0.1));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
    public Command getAutonomousCommand() {
    return m_chooser.getSelected();
  }  

   public Command getLightInitCommand() {
    return m_ledChooser.getSelected();
  } 
}
