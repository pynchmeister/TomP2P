package net.tomp2p.relay;

import java.util.Collection;
import java.util.Set;

import net.tomp2p.nat.PeerBuilderNAT;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.relay.android.MessageBufferConfiguration;

/**
 * Holds multiple relay types with their configuration
 * 
 * @author Nico Rutishauser
 *
 */
public class RelayConfig {

	private final RelayType type;
	private final String registrationId;
	private final MessageBufferConfiguration bufferConfiguration;

	// configurable
	private int peerMapUpdateInterval;
	private Collection<PeerAddress> gcmServers;

	/**
	 * Creates a TCP relay configuration
	 * 
	 * @return
	 */
	public static RelayConfig OpenTCP() {
		return new RelayConfig(RelayType.OPENTCP, 15, null, null, null);
	}

	/**
	 * Creates an Android relay configuration. The messages at the relayed peer are not buffered.
	 * 
	 * @param registrationId the Google Cloud Messaging registration ID. This can be obtained on an Android
	 *            device by providing the correct senderID. The registration ID is unique for each device for
	 *            each senderID.
	 * @return
	 */
	public static RelayConfig Android(String registrationId) {
		return Android(registrationId, null);
	}

	/**
	 * Creates an Android relay configuration having a buffer for received GCM messages.
	 * 
	 * @param registrationId the Google Cloud Messaging registration ID. This can be obtained on an Android
	 *            device by providing the correct senderID. The registration ID is unique for each device for
	 *            each senderID.
	 * @param bufferConfiguration to buffer received GCM messages. Having multiple relays, this can help to
	 *            reduce battery consumption.
	 * @return
	 */
	public static RelayConfig Android(String registrationId, MessageBufferConfiguration bufferConfiguration) {
		return new RelayConfig(RelayType.ANDROID, 60, registrationId, null, bufferConfiguration);
	}

	private RelayConfig(RelayType type, int peerMapUpdateInterval, String registrationId, Collection<PeerAddress> gcmServers,
			MessageBufferConfiguration bufferConfiguration) {
		this.type = type;
		this.peerMapUpdateInterval = peerMapUpdateInterval;
		this.registrationId = registrationId;
		this.gcmServers = gcmServers;
		this.bufferConfiguration = bufferConfiguration;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(type.toString());
		sb.append("[Interval:").append(peerMapUpdateInterval).append("s");
		if (bufferConfiguration != null) {
			sb.append(", GCM-Buffered");
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * @return the relay type
	 */
	public RelayType type() {
		return type;
	}

	/**
	 * Get the peer map update interval
	 */
	public int peerMapUpdateInterval() {
		return peerMapUpdateInterval;
	}

	/**
	 * Defines the time interval of sending the peer map of the unreachable peer
	 * to its relays. The routing requests are not relayed to the unreachable
	 * peer but handled by the relay peers. Therefore, the relay peers should
	 * always have an up-to-date peer map of the relayed peer
	 * 
	 * @param peerMapUpdateInterval the interval of updating the own peer map at the relay
	 * @return this instance
	 */
	public RelayConfig peerMapUpdateInterval(int peerMapUpdateInterval) {
		this.peerMapUpdateInterval = peerMapUpdateInterval;
		return this;
	}

	/**
	 * <strong>Only used for {@link RelayConfig#ANDROID}</strong><br>
	 * 
	 * @return the GCM registration ID. If this peer is an unreachable Android device, this value needs to be
	 *         provided.
	 */
	public String registrationId() {
		return registrationId;
	}

	/**
	 * <strong>Only used for {@link RelayConfig#ANDROID}</strong><br>
	 * 
	 * @return the configuration for the GCM message buffer at the mobile device.
	 */
	public MessageBufferConfiguration bufferConfiguration() {
		return bufferConfiguration;
	}

	/**
	 * <strong>Only used for {@link RelayConfig#ANDROID}</strong><br>
	 * 
	 * @return a collection of known GCM servers which are known to be able to send GCM messages. A GCM server can
	 *         be configured by setting {@link PeerBuilderNAT#gcmAuthenticationKey(String)}.
	 */
	public Collection<PeerAddress> gcmServers() {
		return gcmServers;
	}

	/**
	 * <strong>Only used for {@link RelayConfig#ANDROID}</strong><br>
	 *
	 * Defines well-known peers that have the ability to send messages over Google Cloud Messaging. If an
	 * empty list or null is provided, the relays try to send it by themselves or deny the relay connection.
	 * 
	 * @param gcmServers a set of peers that can send GCM messages
	 * @return this instance
	 */
	public RelayConfig gcmServers(Set<PeerAddress> gcmServers) {
		this.gcmServers = gcmServers;
		return this;
	}
}
