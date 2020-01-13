
package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

//import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import static frc.robot.Constants.DriveConstants.CAN_ID_BALL_LAUNCH_LEFT;
import static frc.robot.Constants.DriveConstants.CAN_ID_BALL_LAUNCH_RIGHT;

public class BallLauncher extends SubsystemBase {

    WPI_VictorSPX _leftBallThrow = new WPI_VictorSPX(CAN_ID_BALL_LAUNCH_LEFT);
    WPI_VictorSPX _rightBallThrow = new WPI_VictorSPX(CAN_ID_BALL_LAUNCH_RIGHT);

    public void throwBall(double left_spd, double right_spd){
        _leftBallThrow.set(-left_spd);
        _rightBallThrow.set(right_spd);
    }
}