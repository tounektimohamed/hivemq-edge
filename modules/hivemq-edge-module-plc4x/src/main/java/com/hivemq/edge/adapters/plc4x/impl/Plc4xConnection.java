package com.hivemq.edge.adapters.plc4x.impl;

import com.hivemq.persistence.clientsession.ConnectResult;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcSubscriptionRequest;
import org.apache.plc4x.java.api.messages.PlcSubscriptionResponse;
import org.apache.plc4x.java.api.model.PlcSubscriptionHandle;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Plc4xConnection extends AbstractProtocolBridgeConnection implements IProtocolBridgeConnection {

    protected PlcDriverManager plcDriverManager;
    protected PlcConnection plcConnection;
    protected ScheduledFuture poller;


    public Plc4xConnection(PlcDriverManager plcDriverManager, ProtocolBridgeOptions options, IMqttsnRuntimeRegistry registry, ProtocolBridgeDescriptor descriptor)  {
        super(options, descriptor, registry);
        this.plcDriverManager = plcDriverManager;
    }

    protected String createConnectionString(ProtocolBridgeOptions options){
        //opcua:tcp://Simons-Laptop.broadband:53530/OPCUA/SimulationServer

        if(options.getResourcePath() != null && !options.getResourcePath().trim().equals("")){
            return String.format("%s://%s:%s/%s",
                    options.getProtocol().trim(),
                    options.getHostName().trim(),
                    options.getPort(),
                    options.getResourcePath().trim());
        } else {
            return String.format("%s://%s:%s",
                    options.getProtocol().trim(),
                    options.getHostName().trim(),
                    options.getPort());
        }

    }

    @Override
    protected synchronized ConnectResult connectExternal(IClientIdentifierContext context) throws ProtocolBridgeException {

        try {
            if(plcConnection == null || !plcConnection.isConnected()){
                this.context = context;
                String connectionString = createConnectionString(options);
                logger.info("connecting via plx4j to {}", connectionString);
                plcConnection = plcDriverManager.getConnection(connectionString);
                plcConnection.connect();
            }
            return new ConnectResult(Result.STATUS.SUCCESS);
        } catch(Exception e){
            throw new ProtocolBridgeException(e);
        }
    }

    @Override
    protected synchronized DisconnectResult disconnectExternal(IClientIdentifierContext context) throws ProtocolBridgeException {
        try {
            if(plcConnection != null && plcConnection.isConnected()){
                plcConnection.close();
            }
            return new DisconnectResult(Result.STATUS.SUCCESS);
        } catch(Exception e){
            throw new ProtocolBridgeException(e);
        }
    }

    @Override
    protected SubscribeResult subscribeExternal(final IClientIdentifierContext context, final String topicPath, final int grantedQoS)
            throws ProtocolBridgeException {

        try {
            if(plcConnection != null && !plcConnection.isConnected()){
                logger.error("cannot subscribe on bridge that is disconnected");
                throw new ProtocolBridgeException("unable to perform operation on disconnected bridge");
            }

            if (plcConnection.getMetadata().canSubscribe()) {

                final PlcSubscriptionRequest.Builder builder = plcConnection.subscriptionRequestBuilder();
                builder.addChangeOfStateField("value-1", topicPath);

                PlcSubscriptionRequest subscriptionRequest = builder.build();
                final PlcSubscriptionResponse subscriptionResponse =
                        subscriptionRequest.execute().get();

                for (String subscriptionName : subscriptionResponse.getFieldNames()) {
                    final PlcSubscriptionHandle subscriptionHandle =
                            subscriptionResponse.getSubscriptionHandle(subscriptionName);
                    subscriptionHandle.register(
                            event -> {
                                List<Pair<String, byte[]>> l = Plc4xUtils.getDataFromSubscriptionEvent(event);
                                processPlcFieldData(l);
                            }
                    );
                }
            }
            else if(plcConnection.getMetadata().canRead()){
                poller = ((IMqttsnGatewayRuntimeRegistry)registry).getProtocolBridgeService().schedulePolling(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            logger.info("sending read request to plc for {}", topicPath);
                            PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();
                            builder.addItem("value-1", topicPath);
                            PlcReadRequest readRequest = builder.build();
                            readRequest.execute().whenComplete((r, t) -> {
                                List<Pair<String, byte[]>> l = Plc4xUtils.getDataFromReadResponse(r);
                                processPlcFieldData(l);
                            });
                        } catch(Exception e){
                            logger.error("error polling plc connection", e);
                        }
                    }
                }, 5000, 1000, TimeUnit.MILLISECONDS);

            } else {
                logger.error("connection does not support subscribe");
                return new SubscribeResult(Result.STATUS.ERROR, "connection doesn't support subscriptions");
            }

            return new SubscribeResult(Result.STATUS.SUCCESS);

        } catch(Exception e){
            throw new ProtocolBridgeException(e);
        }
    }

    protected void processPlcFieldData(List<Pair<String, byte[]>> l){
        for (Pair<String, byte[]> p : l) {
            if (p.getRight() != null && p.getRight().length > 0) {
                try {
                    logger.info("received field {} from plc -> {}", p.getLeft(),
                            MqttsnWireUtils.toHex(p.getRight()));
                    receiveExternal(context, p.getLeft(), 1, p.getRight());
                } catch (Exception e) {
                    logger.error("error receiving bytes from plc -> {}", p.getLeft(), e);
                }
            }
        }
    }

    @Override
    protected UnsubscribeResult unsubscribeExternal(IClientIdentifierContext context, String topicPath) throws ProtocolBridgeException {
        return null;
    }

    @Override
    protected PublishResult publishExternal(IClientIdentifierContext context, String topicPath, int QoS, byte[] payload) throws ProtocolBridgeException {
        return null;
    }

    @Override
    protected void receiveExternal(IClientIdentifierContext context, String topicPath, int QoS, byte[] payload) throws ProtocolBridgeException {
        super.receive(context, new TopicPath(topicPath), QoS, payload);
    }

    @Override
    public boolean isConnected() throws ProtocolBridgeException {
        return plcConnection != null && plcConnection.isConnected();
    }

    @Override
    public synchronized void close() {
        try {
            if(poller != null){
                poller.cancel(true);
            }
        } catch(Exception e){
            logger.warn("error encountered cancelling polling job", e);
        } finally {
            try {
                if(plcConnection != null &&
                        plcConnection.isConnected()){
                    plcConnection.close();
                }
            } catch(Exception e){
                logger.warn("error encountered shutting down bridge to plc", e);
            }
        }
    }


    private static byte[] convert(Byte[] oBytes) {
        byte[] bytes = new byte[oBytes.length];
        for(int i = 0; i < oBytes.length; i++) {
            bytes[i] = oBytes[i];
        }
        return bytes;
    }
}
