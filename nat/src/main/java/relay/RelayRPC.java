package relay;

import net.tomp2p.connection2.ChannelCreator;
import net.tomp2p.connection2.ConnectionBean;
import net.tomp2p.connection2.ConnectionConfiguration;
import net.tomp2p.connection2.DefaultConnectionConfiguration;
import net.tomp2p.connection2.PeerBean;
import net.tomp2p.connection2.RequestHandler;
import net.tomp2p.futures.FuturePeerConnection;
import net.tomp2p.futures.FutureResponse;
import net.tomp2p.message.Message;
import net.tomp2p.message.Message.Type;
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.DispatchHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelayRPC extends DispatchHandler {

	public static final byte RELAY_COMMAND = 77;

	private static final Logger LOG = LoggerFactory.getLogger(RelayRPC.class);
	private ConnectionConfiguration config;

	public RelayRPC(PeerBean peerBean, ConnectionBean connectionBean) {
		super(peerBean, connectionBean, RELAY_COMMAND);
		config = new DefaultConnectionConfiguration();
	}

	@Override
	public Message handleResponse(Message message, boolean sign) throws Exception {

		if (!(message.getType() == Type.REQUEST_1 && message.getCommand() == RELAY_COMMAND)) {
			throw new IllegalArgumentException("Message content is wrong");
		}
		
		LOG.debug("received RPC message {}", message);
		
		// check if this peer is behind nat. If yes, deny request
		if(peerBean().serverPeerAddress().isRelay()) {
			return createResponseMessage(message, Type.DENIED);
		} else {
			return createResponseMessage(message, Type.OK);
		}
	}

	public FutureResponse setupRelay(PeerAddress other, final ChannelCreator channelCreator) {

		final Message message = createMessage(other, RELAY_COMMAND, Type.REQUEST_1);
		FutureResponse futureResponse = new FutureResponse(message);
		final RequestHandler<FutureResponse> requestHandler = new RequestHandler<FutureResponse>(futureResponse, peerBean(), connectionBean(), config);
		LOG.debug("send RPC message {}", message);
		requestHandler.sendTCP(channelCreator);

		return futureResponse;

	}
}
