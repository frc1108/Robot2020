
package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
//import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import static frc.robot.Constants.IntakeConstants.CAN_ID_Hopper_Intake;
import static frc.robot.Constants.IntakeConstants.CAN_ID_Launcher_Intake;

public class IntakeSubsystem extends SubsystemBase {

    WPI_VictorSPX _HopperIntake = new WPI_VictorSPX(CAN_ID_Hopper_Intake);
    WPI_VictorSPX _LauncherIntake = new WPI_VictorSPX(CAN_ID_Launcher_Intake);

    public void IntakeMotor(double intake_spd){
        _HopperIntake.set(intake_spd);
        _LauncherIntake.set(-1);
    }
}