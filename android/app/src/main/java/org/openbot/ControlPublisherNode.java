package org.openbot;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import java.util.Timer;
import java.util.TimerTask;

import geometry_msgs.Vector3;

public class ControlPublisherNode extends AbstractNodeMain implements NodeMain {
    public String publisherNodeName ="OpenBot/control_publisher";

    //parameter for Turtlebot3
    //public float myRobotSpeedMultiplier = 0.1f; // assume speed is in meter/second turtlebot3 testing
    //public float myRobotAxleDiameter = 0.287f; // Turtlebot3 distance between two wheels in meter
    //public String topicName = "cmd_vel";

    //parameter for Ohmnilabs Telepresence Robot
    public float myRobotSpeedMultiplier = 0.3f;
    public float myRobotAxleDiameter = 0.35f;
    public String topicName = "tb_cmd_vel";

    private Publisher<geometry_msgs.Twist> publisher;

    //naming the node
    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(publisherNodeName);
    }

    @Override
    public void onStart(ConnectedNode connectedNode){
        publisher = connectedNode.newPublisher(topicName, geometry_msgs.Twist._TYPE);
        /*Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                publish();
            }
        }, 100, 100);*/
    }
    public void publish (CameraActivity.ControlSignal vehicleControl){
        float left_wheel_signal = vehicleControl.getLeft();
        float right_wheel_signal = vehicleControl.getRight();

        geometry_msgs.Twist currentControlMessage = publisher.newMessage();
        // set linear velocity
        Vector3 linearVector = currentControlMessage.getLinear();
        float linearSpeed = (left_wheel_signal +right_wheel_signal)*myRobotSpeedMultiplier/2;
        linearVector.setX(linearSpeed);
        // set angular velocity
        Vector3 angularVector = currentControlMessage.getAngular();
        float angularSpeed = (right_wheel_signal-left_wheel_signal)*myRobotSpeedMultiplier/myRobotAxleDiameter;
        angularVector.setZ(angularSpeed);

        publisher.publish(currentControlMessage);
    }
}

