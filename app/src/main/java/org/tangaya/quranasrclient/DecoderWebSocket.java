package org.tangaya.quranasrclient;

import android.util.Log;

import com.neovisionaries.ws.client.ThreadType;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import com.neovisionaries.ws.client.WebSocketState;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class DecoderWebSocket implements DecoderWSRepository {
    // Server code for success decoding
    public static final int STATUS_SUCCESS = 0;
    // Server code for close connection because client silent
    public static final int STATUS_NO_SPEECH = 1;
    // Server code for invalid request id
    public static final int STATUS_ABORTED = 2;
    // Server code for internal error
    public static final int STATUS_NOT_ALLOWED = 5;
    // Server code for worker not available
    public static final int STATUS_NOT_AVAILABLE = 9;

    private String mEndpoint;
    private String mArgument;
    private WebSocket mWebSocket;
    private String mUserId;
    private String mTranscriptionId;

    public DecoderWebSocket(String transcriptionId, String endpoint) {

        mTranscriptionId = transcriptionId;
        mEndpoint = endpoint;
        mArgument = getArguments();
    }

    public DecoderWebSocket(String endpoint) {

        mEndpoint = endpoint;
        mArgument = "";
    }

    @Override
    public void connect(final Callback callback) {

        try {
            mWebSocket = new WebSocketFactory()
                    .setConnectionTimeout(5000)
                    .createSocket(mEndpoint + mArgument);
        }
        catch (IOException e) {
            Timber.e(e.getMessage());
            callback.onError(e.getMessage());
            return;
        }

        try {
            mWebSocket.addListener(new Listener(callback));
            mWebSocket.connect();
        }
        catch (WebSocketException e) {
            callback.onError(e.getMessage());
        }
    }

    private String getArguments() {

        try {
            String contentType =  URLEncoder.encode("audio/x-raw, layout=(string)interleaved, rate=(int)16000, format=(string)S16LE, channels=(int)1", "UTF-8");
            StringBuilder builder = new StringBuilder();
            builder.append("?content-type=");
            builder.append(contentType);
            builder.append("&user-id=");
            builder.append(mUserId);
            builder.append("&transcription-id=");
            builder.append(mTranscriptionId);

            return builder.toString();
        }
        catch(UnsupportedEncodingException e) {
            Timber.e(e.getMessage());
            return "";
        }
    }

    @Override
    public void disconnect() {

        mWebSocket.disconnect();
    }

    @Override
    public void sendBinary(byte[] binary) {

        mWebSocket.sendBinary(binary);
    }

    @Override
    public void sendText(String text) {

        mWebSocket.sendText(text);
    }

    private static class Listener implements WebSocketListener {

        Callback mCallback;

        Listener(Callback callback) {
            mCallback = callback;
        }

        @Override
        public void onStateChanged(WebSocket websocket,
                                   WebSocketState newState) throws Exception {

            Timber.d("State: " + newState.toString());
            Log.d("TWS", newState.toString());
        }

        @Override
        public void onConnected(WebSocket websocket,
                                Map<String, List<String>> headers) throws Exception {

            mCallback.onConnected(headers);
        }

        @Override
        public void onConnectError(WebSocket websocket,
                                   WebSocketException cause) throws Exception {

            Timber.d(cause.getMessage());
        }

        @Override
        public void onDisconnected(WebSocket websocket,
                                   WebSocketFrame serverCloseFrame,
                                   WebSocketFrame clientCloseFrame,
                                   boolean closedByServer) throws Exception {

            mCallback.onDisconnected();
        }

        @Override
        public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

            Timber.d("onFrame: " + frame.toString());
            Log.d("TWS", frame.toString());
        }

        @Override
        public void onContinuationFrame(WebSocket websocket,
                                        WebSocketFrame frame) throws Exception {

            Timber.d("onContinuationFrame");
            Log.d("TWS", "onContinuationFrame" + frame.toString());
        }

        @Override
        public void onTextFrame(WebSocket websocket,
                                WebSocketFrame frame) throws Exception {

            Timber.d("onTextFrame");
            Log.d("TWS", "onTextFrame" + frame.toString());
        }

        @Override
        public void onBinaryFrame(WebSocket websocket,
                                  WebSocketFrame frame) throws Exception {

            Timber.d("onBinaryFrame");
        }

        @Override
        public void onCloseFrame(WebSocket websocket,
                                 WebSocketFrame frame) throws Exception {

            Timber.d("onCloseFrame");
        }

        @Override
        public void onPingFrame(WebSocket websocket,
                                WebSocketFrame frame) throws Exception {

            Timber.d("onPingFrame");
        }

        @Override
        public void onPongFrame(WebSocket websocket,
                                WebSocketFrame frame) throws Exception {

            Timber.d("onPongFrame");
        }

        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {

            mCallback.onTextMessage(text);
            Log.d("TWS", "onTextMessage:");
            Log.d("TWS", "onTextMessage: " + text);
        }

        @Override
        public void onBinaryMessage(WebSocket websocket,
                                    byte[] binary) throws Exception {

            Timber.d("onBinaryMessage");
            Log.d("TWS", "onBinaryMessage Zzzzzzz... ");
        }

        @Override
        public void onSendingFrame(WebSocket websocket,
                                   WebSocketFrame frame) throws Exception {

            Timber.d("onSendingFrame");
        }

        @Override
        public void onFrameSent(WebSocket websocket,
                                WebSocketFrame frame) throws Exception {

            Timber.d("onFrameSent");
        }

        @Override
        public void onFrameUnsent(WebSocket websocket,
                                  WebSocketFrame frame) throws Exception {

            Timber.d("onFrameUnSent");
        }

        @Override
        public void onThreadCreated(WebSocket websocket,
                                    ThreadType threadType, Thread thread) throws Exception {

            Timber.d("onThreadCreated");
        }

        @Override
        public void onThreadStarted(WebSocket websocket,
                                    ThreadType threadType, Thread thread) throws Exception {

            Timber.d("onThreadStarted");
        }

        @Override
        public void onThreadStopping(WebSocket websocket, ThreadType threadType,
                                     Thread thread) throws Exception {

            Timber.d("onThreadStopping");
        }

        @Override
        public void onError(WebSocket websocket,
                            WebSocketException cause) throws Exception {

            mCallback.onError(cause.getMessage());
        }

        @Override
        public void onFrameError(WebSocket websocket,
                                 WebSocketException cause, WebSocketFrame frame) throws Exception {

            Timber.d("onFrameError");
        }

        @Override
        public void onMessageError(WebSocket websocket,
                                   WebSocketException cause, List<WebSocketFrame> frames) throws Exception {

            Timber.d("onMessageError");
        }

        @Override
        public void onMessageDecompressionError(WebSocket websocket,
                                                WebSocketException cause,
                                                byte[] compressed) throws Exception {

            Timber.d("onMessageDecompressionError");
        }

        @Override
        public void onTextMessageError(WebSocket websocket,
                                       WebSocketException cause, byte[] data) throws Exception {

            Timber.d("onTextMessageError");
        }

        @Override
        public void onSendError(WebSocket websocket,
                                WebSocketException cause,
                                WebSocketFrame frame) throws Exception {

            mCallback.onSendError(cause.getMessage());
        }

        @Override
        public void onUnexpectedError(WebSocket websocket,
                                      WebSocketException cause) throws Exception {

            Timber.d("onUnexpectedError");
        }

        @Override
        public void handleCallbackError(WebSocket websocket,
                                        Throwable cause) throws Exception {

            Timber.d("handleCallbackError");
        }

        @Override
        public void onSendingHandshake(WebSocket websocket,
                                       String requestLine,
                                       List<String[]> headers) throws Exception {

            Timber.d("onSendingHandshake");
        }
    }
}