package dev.chat.fork.messenger.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.UnitModelLoader;
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableEncoder;
import com.bumptech.glide.load.resource.gif.ByteBufferGifDecoder;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.gif.StreamGifDecoder;

import org.signal.apng.ApngDecoder;
import org.signal.blurhash.BlurHash;
import org.signal.core.util.crypto.AttachmentSecret;
import org.signal.core.util.crypto.AttachmentSecretProvider;
import org.signal.glide.blurhash.BlurHashModelLoader;
import org.signal.glide.blurhash.BlurHashResourceDecoder;
import org.signal.glide.common.io.InputStreamFactory;
import org.signal.glide.decryptableuri.DecryptableUri;
import org.signal.glide.decryptableuri.DecryptableUriStreamLoader;
import dev.chat.fork.messenger.badges.load.BadgeLoader;
import dev.chat.fork.messenger.badges.load.GiftBadgeModel;
import dev.chat.fork.messenger.badges.models.Badge;
import dev.chat.fork.messenger.contacts.avatars.ContactPhoto;
import dev.chat.fork.messenger.contacts.avatars.ContactPhotoLoader;
import dev.chat.fork.messenger.crypto.AppAttachmentSecretStore;
import dev.chat.fork.messenger.giph.model.ChunkedImageUrl;
import dev.chat.fork.messenger.glide.cache.ApngDrawableTranscoder;
import dev.chat.fork.messenger.glide.cache.ApngInputStreamFactoryResourceDecoder;
import dev.chat.fork.messenger.glide.cache.EncryptedApngCacheDecoder;
import dev.chat.fork.messenger.glide.cache.EncryptedApngResourceEncoder;
import dev.chat.fork.messenger.glide.cache.EncryptedBitmapResourceEncoder;
import dev.chat.fork.messenger.glide.cache.EncryptedCacheDecoder;
import dev.chat.fork.messenger.glide.cache.EncryptedCacheEncoder;
import dev.chat.fork.messenger.glide.cache.EncryptedGifDrawableResourceEncoder;
import dev.chat.fork.messenger.glide.cache.InputStreamFactoryBitmapDecoder;
import dev.chat.fork.messenger.glide.cache.StreamBitmapDecoder;
import dev.chat.fork.messenger.glide.cache.StreamFactoryGifDecoder;
import dev.chat.fork.messenger.glide.cache.WebpSanDecoder;
import dev.chat.fork.messenger.mms.RegisterGlideComponents;
import dev.chat.fork.messenger.mms.SignalGlideModule;
import dev.chat.fork.messenger.stickers.StickerRemoteUri;
import dev.chat.fork.messenger.stickers.StickerRemoteUriLoader;
import dev.chat.fork.messenger.stories.StoryTextPostModel;
import dev.chat.fork.messenger.util.ConversationShortcutPhoto;

import java.io.File;
import java.io.InputStream;

/**
 * The core logic for {@link SignalGlideModule}. This is a separate class because it uses
 * dependencies defined in the main Gradle module.
 */
public class SignalGlideComponents implements RegisterGlideComponents {

  @Override
  public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
    AttachmentSecret attachmentSecret = AttachmentSecretProvider.getInstance(context, AppAttachmentSecretStore.INSTANCE).getOrCreateAttachmentSecret();
    byte[]           secret           = attachmentSecret.getModernKey();

    registry.prepend(File.class, File.class, UnitModelLoader.Factory.getInstance());

    registry.prepend(InputStream.class, Bitmap.class, new WebpSanDecoder());

    registry.prepend(InputStream.class, new EncryptedCacheEncoder(secret, glide.getArrayPool()));

    registry.prepend(File.class, Bitmap.class, new EncryptedCacheDecoder<>(secret, new StreamBitmapDecoder(context, glide, registry)));

    StreamGifDecoder        streamGifDecoder        = new StreamGifDecoder(registry.getImageHeaderParsers(), new ByteBufferGifDecoder(context, registry.getImageHeaderParsers(), glide.getBitmapPool(), glide.getArrayPool()), glide.getArrayPool());
    StreamFactoryGifDecoder streamFactoryGifDecoder = new StreamFactoryGifDecoder(streamGifDecoder);
    registry.prepend(InputStream.class, GifDrawable.class, streamGifDecoder);
    registry.prepend(InputStreamFactory.class, GifDrawable.class, streamFactoryGifDecoder);
    registry.prepend(GifDrawable.class, new EncryptedGifDrawableResourceEncoder(secret));
    registry.prepend(File.class, GifDrawable.class, new EncryptedCacheDecoder<>(secret, streamGifDecoder));

    EncryptedBitmapResourceEncoder encryptedBitmapResourceEncoder = new EncryptedBitmapResourceEncoder(secret);
    registry.prepend(Bitmap.class, new EncryptedBitmapResourceEncoder(secret));
    registry.prepend(BitmapDrawable.class, new BitmapDrawableEncoder(glide.getBitmapPool(), encryptedBitmapResourceEncoder));

    registry.prepend(InputStreamFactory.class, ApngDecoder.class, new ApngInputStreamFactoryResourceDecoder());
    registry.prepend(ApngDecoder.class, new EncryptedApngResourceEncoder(secret));
    registry.prepend(File.class, ApngDecoder.class, new EncryptedApngCacheDecoder(secret));
    registry.register(ApngDecoder.class, Drawable.class, new ApngDrawableTranscoder());

    registry.prepend(BlurHash.class, Bitmap.class, new BlurHashResourceDecoder());
    registry.prepend(StoryTextPostModel.class, Bitmap.class, new StoryTextPostModel.Decoder());

    registry.append(StoryTextPostModel.class, StoryTextPostModel.class, UnitModelLoader.Factory.getInstance());
    registry.append(ConversationShortcutPhoto.class, Bitmap.class, new ConversationShortcutPhoto.Loader.Factory(context));
    registry.append(ContactPhoto.class, InputStream.class, new ContactPhotoLoader.Factory(context));
    registry.append(DecryptableUri.class, InputStreamFactory.class, new DecryptableUriStreamLoader.Factory(context));
    registry.append(InputStreamFactory.class, Bitmap.class, new InputStreamFactoryBitmapDecoder(context, glide, registry));
    registry.append(ChunkedImageUrl.class, InputStream.class, new ChunkedImageUrlLoader.Factory());
    registry.append(StickerRemoteUri.class, InputStream.class, new StickerRemoteUriLoader.Factory());
    registry.append(BlurHash.class, BlurHash.class, new BlurHashModelLoader.Factory());
    registry.append(Badge.class, InputStream.class, BadgeLoader.createFactory());
    registry.append(GiftBadgeModel.class, InputStream.class, GiftBadgeModel.createFactory());
    registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
  }
}
