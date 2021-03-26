package com.eateasily.exoplayersample;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.util.ErrorMessageProvider;

public class PlayerErrorMessageProvider implements ErrorMessageProvider<ExoPlaybackException> {

    private Context mContext;

    public PlayerErrorMessageProvider(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    @NonNull
    public Pair<Integer, String> getErrorMessage(@NonNull ExoPlaybackException e) {
        String errorString = mContext.getString(R.string.error_generic);
        if (e.type == ExoPlaybackException.TYPE_RENDERER) {
            Exception cause = e.getRendererException();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                // Special case for decoder initialization failures.
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                        (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.codecInfo == null) {
                    if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        errorString = mContext.getString(R.string.error_querying_decoders);
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        errorString =
                                mContext.getString(
                                        R.string.error_no_secure_decoder, decoderInitializationException.mimeType);
                    } else {
                        errorString =
                                mContext.getString(R.string.error_no_decoder, decoderInitializationException.mimeType);
                    }
                } else {
                    errorString =
                            mContext.getString(
                                    R.string.error_instantiating_decoder,
                                    decoderInitializationException.codecInfo.name);
                }
            }
        }
        return Pair.create(0, errorString);
    }
}