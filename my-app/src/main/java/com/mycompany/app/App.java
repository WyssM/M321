package com.mycompany.app;

import org.eclipse.paho.client.mqttv3.*;

public class App {

    private static boolean running = true;

    public static void main(String[] args) {
        String broker = "tcp://broker:1883";
        String clientId;
        String topic;
        String feedbackTopic;

        // Überprüfen, ob genügend Argumente übergeben wurden
        if (args.length >= 2) {
            clientId = "javaClient" + args[0];
            topic = args[0];
            feedbackTopic = args[1];
        } else {
            throw new IllegalArgumentException("Bitte geben Sie PUB und SUB Topics als Kommandozeilenargumente an.");
        }

        double counter = 0.0;

        try {
            MqttClient client = new MqttClient(broker, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            client.connect(options);

            // Abonnieren des Feedback-Topics
            client.subscribe(feedbackTopic);

            // Setzen des Callbacks, der auf eingehende Nachrichten reagiert
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // Ausgabe der empfangenen Nachricht
                    System.out.println("Empfangene Nachricht: " + new String(message.getPayload()));

                    // Wenn die Nachricht "stop" ist, stoppen wir die Hauptschleife
                    if ("stop".equals(new String(message.getPayload()))) {
                        running = false;
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });

            while (running) {
                double sinValue = Math.sin(counter);
                System.out.println("Sinuswert: " + sinValue);
                MqttMessage message = new MqttMessage(Double.toString(sinValue).getBytes());
                client.publish(topic, message);
                counter += 0.1;
                Thread.sleep(1000);
            }
        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}